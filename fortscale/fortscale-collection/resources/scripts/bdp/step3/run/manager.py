import logging
from cm_api.api_client import ApiResource
import shutil
import os
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from validation import validate_no_missing_events, validate_entities_synced, validate_cleanup_complete
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
import bdp_utils.run
from bdp_utils.kafka import send
from bdp_utils.samza import restart_all_tasks

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from automatic_config.common import config
from automatic_config.common.utils import io
from automatic_config.common.utils.mongo import get_collections_time_boundary
from automatic_config.common.results.committer import update_configurations
from automatic_config.common.results.store import Store
from automatic_config.fs_reduction import main as fs_main
from automatic_config.alphas_and_betas import main as weights_main


logger = logging.getLogger('step3')


class Manager:
    STEP_RUN_BDP = 'run_bdp'
    STEP_SYNC_ENTITIES = 'sync_entities'
    STEP_RUN_AUTOMATIC_CONFIG = 'run_automatic_config'
    STEP_CLEANUP = 'cleanup'
    STEP_START_SERVICES = 'start_services'
    STEP_RUN_BDP_AGAIN = 'run_bdp_again'

    def __init__(self,
                 host,
                 validation_timeout,
                 validation_polling,
                 days_to_ignore,
                 skip_to):
        self._runner = bdp_utils.run.Runner(name='BdpAggregatedEventsToEntityEvents',
                                            logger=logger,
                                            host=host,
                                            block=False)
        self._cleaner = bdp_utils.run.Runner(name='BdpCleanupAggregatedEventsToEntityEvents',
                                             logger=logger,
                                             host=host,
                                             block=True)
        self._host = host
        self._validation_timeout = validation_timeout
        self._validation_polling = validation_polling
        self._days_to_ignore = days_to_ignore
        self._skip_to = skip_to

    def run(self):
        for step_name, step in [(Manager.STEP_RUN_BDP, self._run_bdp),
                                (Manager.STEP_SYNC_ENTITIES, self._sync_entities),
                                (Manager.STEP_RUN_AUTOMATIC_CONFIG, self._run_automatic_config),
                                (Manager.STEP_CLEANUP, self._cleanup),
                                (Manager.STEP_START_SERVICES, self._start_services),
                                (Manager.STEP_RUN_BDP_AGAIN, self._run_bdp)]:
            if self._skip_to is not None and self._skip_to != step_name:
                logger.info('skipping sub-step ' + step_name)
                continue
            self._skip_to = None
            logger.info('executing sub-step ' + step_name)
            if not step():
                return False
        return True

    def _run_bdp(self):
        kill_process = self._runner.infer_start_and_end(collection_names_regex='^aggr_').run(overrides_key='step3.run')
        is_valid = validate_no_missing_events(host=self._host,
                                              timeout=self._validation_timeout,
                                              start=self._runner.get_start(),
                                              end=self._runner.get_end())
        logger.info('making sure bdp process exits...')
        kill_process()
        return is_valid

    def _sync_entities(self):
        logger.info('syncing entities...')
        send(logger=logger,
             host=self._host,
             topic='fortscale-entity-event-stream-control',
             message='{\\"type\\": \\"entity_event_sync\\"}')
        return validate_entities_synced(host=self._host,
                                        timeout=self._validation_timeout,
                                        polling=self._validation_polling)

    def _run_automatic_config(self):
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

    def _cleanup(self):
        self._cleaner.infer_start_and_end(collection_names_regex='^aggr_').run(overrides_key='step3.cleanup')
        return validate_cleanup_complete(host=self._host,
                                         timeout=self._validation_timeout,
                                         polling=self._validation_polling)

    def _start_services(self):
        logger.info('starting kafka...')
        api = ApiResource(self._host, username='admin', password='admin')
        cluster = filter(lambda c: c.name == 'cluster', api.get_all_clusters())[0]
        kafka = filter(lambda service: service.name == 'kafka', cluster.get_all_services())[0]
        if kafka.serviceState != 'STOPPED':
            raise Exception('kafka should be STOPPED, but it is ' + kafka.serviceState)
        if kafka.start().wait().success:
            logger.info('kafka started successfully')
        else:
            logger.error('kafka failed to start')
            return False
        return restart_all_tasks(logger=logger, host=self._host)
