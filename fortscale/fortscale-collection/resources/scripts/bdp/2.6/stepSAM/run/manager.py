import logging
import json
import sys
import os

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from validation.started_processing_everything.validation import validate_started_processing_everything
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils.manager import DontReloadModelsOverridingManager, cleanup_everything_but_models
from bdp_utils.data_sources import data_source_to_enriched_tables
from bdp_utils.throttling import Throttler
from bdp_utils.samza import restart_task
import bdp_utils.run
from step2.validation.validation import block_until_everything_is_validated
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..']))
from automatic_config.common.utils import time_utils, impala_utils, io
from automatic_config.common.utils.mongo import update_models_time, get_collections_size


logger = logging.getLogger('stepSAM')


class Manager(DontReloadModelsOverridingManager):
    _MODEL_CONFS_OVERRIDING_PATH = '/home/cloudera/fortscale/config/asl/models/overriding'
    _MODEL_CONFS_ADDITIONAL_PATH = '/home/cloudera/fortscale/config/asl/models/additional'

    def __init__(self,
                 host,
                 data_sources,
                 polling_interval,
                 max_batch_size,
                 force_max_batch_size_in_minutes,
                 max_gap,
                 force_max_gap_in_seconds,
                 convert_to_minutes_timeout,
                 timeoutInSeconds,
                 cleanup_first,
                 start=None,
                 end=None):
        super(Manager, self).__init__(logger=logger,
                                      host=host,
                                      scoring_task_name_that_should_not_reload_models='RAW_EVENTS_SCORING')
        self._host = host
        self._polling_interval = polling_interval
        self._timeoutInSeconds = timeoutInSeconds
        self._start = time_utils.get_epochtime(start) if start is not None else None
        self._end = time_utils.get_epochtime(end) if end is not None else None
        self._cleanup_first = cleanup_first
        self._impala_connection = impala_utils.connect(host=host)
        data_sources_before_filtering = data_sources
        data_sources = [data_source
                        for data_source in data_sources
                        if impala_utils.get_last_event_time(connection=self._impala_connection,
                                                            table=data_source_to_enriched_tables[data_source]) is not None]
        if data_sources != data_sources_before_filtering:
            logger.warning("some of the data sources don't contain data in the enriched table. "
                           "Using only the following data sources: " + ', '.join(data_sources))
        self._data_sources = data_sources
        self._data_source_to_throttler = dict((data_source, Throttler(logger=logger,
                                                                      host=host,
                                                                      data_source=data_source,
                                                                      max_batch_size=max_batch_size,
                                                                      force_max_batch_size_in_minutes=force_max_batch_size_in_minutes.get(data_source) if force_max_batch_size_in_minutes is not None else None,
                                                                      max_gap=max_gap,
                                                                      force_max_gap_in_seconds=force_max_gap_in_seconds.get(data_source) if force_max_gap_in_seconds is not None else None,
                                                                      convert_to_minutes_timeout=convert_to_minutes_timeout,
                                                                      start=self._get_start(data_source=data_source),
                                                                      end=self._get_end(data_source=data_source))) for data_source in self._data_sources)
        self._runner = bdp_utils.run.Runner(name='stepSAM.scores',
                                            logger=logger,
                                            host=host,
                                            block=False)
        self._builder = bdp_utils.run.Runner(name='stepSAM.build_models',
                                             logger=logger,
                                             host=host,
                                             block=True,
                                             block_until_log_reached='Spring context closed')

    def _update_model_confs(self, path, model_confs, original_to_backup):
        updated = False
        for model_conf in model_confs['ModelConfs']:
            builder = model_conf['builder']
            if builder['type'] == 'category_rarity_model_builder':
                builder['entriesToSaveInModel'] = 100000
                updated = True
        if updated:
            logger.info('updating category rarity model builders of ' + path + '...')
            original_to_backup[path] = io.backup(path=path) if os.path.isfile(path) else None
            with io.FileWriter(path) as f:
                json.dump(model_confs, f, indent=4)

    def _prepare_overriding_model_builders_config(self):
        logger.info('updating overriding models...')
        original_to_backup = {}
        for data_source in self._data_sources:
            data_source_raw_events_model_file_name = \
                'raw_events_model_confs_' + (data_source if data_source != 'kerberos' else 'kerberos_logins') + '.json'
            data_source_model_confs_path = Manager._MODEL_CONFS_OVERRIDING_PATH + '/' + \
                                           data_source_raw_events_model_file_name
            with io.open_overrides_file(overriding_path=data_source_model_confs_path,
                                        jar_name='fortscale-ml-1.1.0-SNAPSHOT.jar',
                                        path_in_jar='config/asl/models/' +
                                                data_source_raw_events_model_file_name) as f:
                model_confs = json.load(f)
            self._update_model_confs(path=data_source_model_confs_path,
                                     model_confs=model_confs,
                                     original_to_backup=original_to_backup)
        return original_to_backup

    def _prepare_additional_model_builders_config(self):
        logger.info('updating additional models...')
        original_to_backup = {}
        if os.path.exists(Manager._MODEL_CONFS_ADDITIONAL_PATH):
            for filename in os.listdir(Manager._MODEL_CONFS_ADDITIONAL_PATH):
                file_path = os.path.sep.join([Manager._MODEL_CONFS_ADDITIONAL_PATH, filename])
                with open(file_path, 'r') as f:
                    model_confs = json.load(f)
                self._update_model_confs(path=file_path,
                                         model_confs=model_confs,
                                         original_to_backup=original_to_backup)
        return original_to_backup

    def _backup_and_override(self):
        original_to_backup = super(Manager, self)._backup_and_override()
        original_to_backup.update(self._prepare_overriding_model_builders_config())
        original_to_backup.update(self._prepare_additional_model_builders_config())
        return original_to_backup

    def _run_after_task_restart(self):
        sub_steps = [
            ('run scores', lambda data_source: self._skip_if_there_are_models(data_source, self._run_scores), True),
            ('build models', lambda data_source: self._skip_if_there_are_models(data_source, self._build_models), True),
            ('cleanup', self._cleanup, False),
            ('run scores after modeling', self._run_scores, True)
        ]
        if self._cleanup_first:
            sub_steps.insert(0, ('cleanup before starting to process data sources',
                                 lambda data_source: self._cleanup(data_source=data_source, fail_if_no_models=False),
                                 False))
        for step_name, step, run_once_per_data_source in sub_steps:
            for data_source in self._data_sources if run_once_per_data_source else [None]:
                logger.info('running sub step ' + step_name + ' for ' +
                            (data_source if data_source is not None else 'all data sources') + '...')
                if not step(data_source):
                    return False
        return True

    def _prepare_bdp_overrides(self, data_source):
        forwarding_batch_size_in_minutes = self._data_source_to_throttler[data_source].get_max_batch_size_in_minutes()
        max_source_destination_time_gap = self._data_source_to_throttler[data_source].get_max_gap_in_seconds()
        really_big_epochtime = time_utils.get_epochtime('29990101')
        overrides = [
            'data_sources = ' + data_source,
            'throttlingSleep = 30',
            'forwardingBatchSizeInMinutes = ' + str(forwarding_batch_size_in_minutes),
            'maxSourceDestinationTimeGap = ' + str(max_source_destination_time_gap),
            'maxSyncGapInSeconds = ' + str(really_big_epochtime),
            'secondsBetweenSyncs = ' + str(really_big_epochtime)
        ]
        if self._timeoutInSeconds is not None:
            overrides.append('timeoutInSeconds = ' + str(self._timeoutInSeconds))
        return overrides

    def _get_start(self, data_source):
        table = data_source_to_enriched_tables[data_source]
        return self._start or impala_utils.get_first_event_time(connection=self._impala_connection, table=table)

    def _get_end(self, data_source):
        table = data_source_to_enriched_tables[data_source]
        return self._end or impala_utils.get_last_event_time(connection=self._impala_connection, table=table)

    def _skip_if_there_are_models(self, data_source, step_cb):
        if get_collections_size(host=self._host,
                                collection_names_regex=r'model_.*\.' + data_source) > 0:
            logger.info('skipping ' + data_source + ' because there are already models in mongo')
            return True
        return step_cb(data_source)

    def _run_scores(self, data_source):
        start = self._get_start(data_source=data_source)
        end = self._get_end(data_source=data_source)
        overrides = self._prepare_bdp_overrides(data_source=data_source) + ['buildModelsFirst = false']
        kill_process = self._runner \
            .set_start(start) \
            .set_end(end) \
            .run(overrides_key='stepSAM', overrides=overrides)
        if not self._validate_scores(data_source=data_source,
                                     start_time_epoch=start,
                                     end_time_epoch=end):
            return False
        logger.info('making sure bdp process exits...')
        kill_process()
        return self._restart_aggregation_task()

    def _validate_scores(self, data_source, start_time_epoch, end_time_epoch):
        if start_time_epoch % (60 * 60) != 0:
            start_time_epoch -= (start_time_epoch % (60 * 60))
        if end_time_epoch % (60 * 60) != 0:
            end_time_epoch += (-end_time_epoch) % (60 * 60)
        return validate_started_processing_everything(host=self._host,
                                                      data_source=data_source,
                                                      end_time_epoch=end_time_epoch) and \
               block_until_everything_is_validated(host=self._host,
                                                   start_time_epoch=start_time_epoch,
                                                   end_time_epoch=end_time_epoch,
                                                   wait_between_validations=self._polling_interval,
                                                   max_delay=-1,
                                                   timeout=0,
                                                   polling_interval=0,
                                                   data_sources=[data_source],
                                                   logger=logger)

    def _build_models(self, data_source):
        start = self._get_start(data_source=data_source)
        end = self._get_end(data_source=data_source)
        end += (-end) % (24 * 60 * 60)
        overrides = self._prepare_bdp_overrides(data_source=data_source) + ['buildModelsFirst = true']
        self._builder \
            .set_start(end) \
            .set_end(end) \
            .run(overrides_key='stepSAM', overrides=overrides)
        if not update_models_time(logger=logger,
                                  host=self._host,
                                  collection_names_regex='^model_',
                                  time=start):
            return False
        return True

    def _cleanup(self, data_source=None, fail_if_no_models=True):
        return cleanup_everything_but_models(logger=logger,
                                             host=self._host,
                                             clean_overrides_key='stepSAM.cleanup',
                                             infer_start_and_end_from_collection_names_regex='^aggr_',
                                             fail_if_no_models=fail_if_no_models)

    def _restart_aggregation_task(self):
        return restart_task(logger=logger, host=self._host, task_name='AGGREGATION_EVENTS_STREAMING')
