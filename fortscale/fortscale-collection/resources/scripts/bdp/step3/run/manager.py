import logging
import shutil
import os
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from validation import validate_no_missing_events, validate_entities_synced

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
import bdp_utils.run
from bdp_utils.kafka import send
from bdp_utils.manager import DontReloadModelsOverridingManager, cleanup_everything_but_models

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from automatic_config.common import config
from automatic_config.common.utils import io
from automatic_config.common.utils.mongo import get_collections_time_boundary
from automatic_config.common.results.committer import update_configurations
from automatic_config.common.results.store import Store
from automatic_config.fs_reduction import main as fs_main
from automatic_config.alphas_and_betas import main as weights_main
from automatic_config.common.utils.mongo import update_models_time


logger = logging.getLogger('step3')


class Manager(DontReloadModelsOverridingManager):
    _SUB_STEP_RUN_SCORES = 'run_scores'
    _SUB_STEP_BUILD_MODELS = 'build_models'
    _SUB_STEP_CLEANUP_AND_MOVE_MODELS_BACK_IN_TIME = 'cleanup_and_move_models_back_in_time'
    _SUB_STEP_RUN_SCORES_AFTER_MODELS_HAVE_BEEN_BUILT = 'run_scores_after_models_have_been_built'
    _SUB_STEP_CALC_REDUCERS_AND_ALPHAS_AND_BETAS = 'calc_reducers_and_alphas_and_betas'
    _SUB_STEP_CLEANUP_AFTER_EVERYTHING_IS_SET_UP = 'cleanup_after_everything_is_set_up'
    _SUB_STEP_RUN_SCORES_AFTER_REDUCERS_AND_ALPHAS_AND_BETAS_HAVE_BEEN_CALCULATED = 'run_scores_after_reducers_and_alphas_abd_betas_have_been_calculated'

    SUB_STEPS = [
        _SUB_STEP_RUN_SCORES,
        _SUB_STEP_BUILD_MODELS,
        _SUB_STEP_CLEANUP_AND_MOVE_MODELS_BACK_IN_TIME,
        _SUB_STEP_RUN_SCORES_AFTER_MODELS_HAVE_BEEN_BUILT,
        _SUB_STEP_CALC_REDUCERS_AND_ALPHAS_AND_BETAS,
        _SUB_STEP_CLEANUP_AFTER_EVERYTHING_IS_SET_UP,
        _SUB_STEP_RUN_SCORES_AFTER_REDUCERS_AND_ALPHAS_AND_BETAS_HAVE_BEEN_CALCULATED
    ]

    def __init__(self,
                 host,
                 validation_timeout,
                 validation_polling,
                 days_to_ignore,
                 skip_to,
                 run_until):
        super(Manager, self).__init__(logger=logger)
        self._runner = bdp_utils.run.Runner(name='step3.scores',
                                            logger=logger,
                                            host=host,
                                            block=False)
        self._builder = bdp_utils.run.Runner(name='step3.build_models',
                                             logger=logger,
                                             host=host,
                                             block=True,
                                             block_until_log_reached='Spring context closed')
        self._host = host
        self._validation_timeout = validation_timeout
        self._validation_polling = validation_polling
        self._days_to_ignore = days_to_ignore
        self._skip_to = skip_to
        self._run_until = run_until

    def _run(self):
        self._runner.infer_start_and_end(collection_names_regex='^aggr_')
        end = self._runner.get_end()
        end += (-end) % (24 * 60 * 60)
        self._builder.set_start(end).set_end(end)
        for sub_step_name, sub_step in [
            (Manager._SUB_STEP_RUN_SCORES, self._run_scores),
            (Manager._SUB_STEP_BUILD_MODELS, self._build_models),
            (Manager._SUB_STEP_CLEANUP_AND_MOVE_MODELS_BACK_IN_TIME, self._cleanup_and_move_models_back_in_time),
            (Manager._SUB_STEP_RUN_SCORES_AFTER_MODELS_HAVE_BEEN_BUILT, self._run_scores),
            (Manager._SUB_STEP_CALC_REDUCERS_AND_ALPHAS_AND_BETAS, self._calc_reducers_and_alphas_and_betas),
            (Manager._SUB_STEP_CLEANUP_AFTER_EVERYTHING_IS_SET_UP, self._cleanup_and_move_models_back_in_time),
            (Manager._SUB_STEP_RUN_SCORES_AFTER_REDUCERS_AND_ALPHAS_AND_BETAS_HAVE_BEEN_CALCULATED, self._run_scores),
        ]:
            if self._skip_to is not None and self._skip_to != sub_step_name:
                logger.info('skipping sub-step ' + sub_step_name)
                continue
            self._skip_to = None
            logger.info('executing sub-step ' + sub_step_name)
            if not sub_step():
                return False
            if self._run_until == sub_step_name:
                logger.info('skipping all the sub-steps to follow')
                break
        return True

    def _run_scores(self):
        kill_process = self._runner.run(overrides_key='step3.scores')
        is_valid = validate_no_missing_events(host=self._host,
                                              timeout=self._validation_timeout,
                                              start=self._runner.get_start(),
                                              end=self._runner.get_end())
        logger.info('making sure bdp process exits...')
        kill_process()
        return is_valid and self._sync_entities()

    def _sync_entities(self):
        logger.info('syncing entities...')
        send(logger=logger,
             host=self._host,
             topic='fortscale-entity-event-stream-control',
             message='{\\"type\\": \\"entity_event_sync\\"}')
        return validate_entities_synced(host=self._host,
                                        timeout=self._validation_timeout,
                                        polling=self._validation_polling)

    def _build_models(self):
        self._builder.run(overrides_key='step3.build_models')
        return True

    def _cleanup_and_move_models_back_in_time(self):
        return cleanup_everything_but_models(logger=logger,
                                             host=self._host,
                                             clean_overrides_key='step3.cleanup',
                                             infer_start_and_end_from_collection_names_regex='^aggr_') and \
               update_models_time(logger=logger,
                                  host=self._host,
                                  collection_names_regex='^model_',
                                  infer_start_from_collection_names_regex='^aggr_')

    def _calc_reducers_and_alphas_and_betas(self):
        if os.path.exists(config.interim_results_path):
            logger.info('removing interim results of previous run of automatic_config')
            shutil.rmtree(config.interim_results_path)
        # extract entity_events.json to the overriding folder (if not already there)
        overriding_path='/home/cloudera/fortscale/config/asl/entity_events/overriding/entity_events.json'
        io.open_overrides_file(overriding_path=overriding_path,
                               jar_name='fortscale-aggregation-1.1.0-SNAPSHOT.jar',
                               path_in_jar='config/asl/entity_events.json',
                               create_if_not_exist=True)
        # calculate Fs reducers and alphas and betas
        start = get_collections_time_boundary(host=self._host,
                                              collection_names_regex='^aggr_',
                                              is_start=True)
        end = get_collections_time_boundary(host=self._host,
                                            collection_names_regex='^aggr_',
                                            is_start=False)
        logger.info('calculating Fs reducers (using start time ' + str(start) + ' and end time ' + str(end) + ')...')
        fs_main.load_data_and_run_algo(start=start, end=end)
        start += 60 * 60 * 24 * self._days_to_ignore
        logger.info('calculating alphas and betas (ignoring first ' + str(self._days_to_ignore) +
                    ' days - using start time ' + str(start) + ' and end time ' + str(end) + ')...')
        weights_main.load_data_and_run_algo(start=start, end=end)
        # commit everything
        logger.info('updating configuration files with Fs reducers and alphas and betas...')
        update_configurations()
        return not Store(config.interim_results_path + '/results.json').is_empty()
