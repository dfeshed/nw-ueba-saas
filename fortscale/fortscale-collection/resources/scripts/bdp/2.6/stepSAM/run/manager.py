import logging
import json
import math
import sys
import os
import impala_stats

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from validation.started_processing_everything.validation import validate_started_processing_everything
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils.manager import OnlineManager, cleanup_everything_but_models
from bdp_utils.data_sources import data_source_to_enriched_tables
from bdp_utils.throttling import Throttler
from bdp_utils.samza import restart_task
import bdp_utils.run
from step2.validation.validation import block_until_everything_is_validated
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..']))
from automatic_config.common.utils import time_utils, impala_utils, io
from automatic_config.common.utils.mongo import update_models_time


logger = logging.getLogger('stepSAM')


class Manager(OnlineManager):
    _FORTSCALE_OVERRIDING_PATH = '/home/cloudera/fortscale/streaming/config/fortscale-overriding-streaming.properties'
    _MODEL_CONFS_OVERRIDING_PATH = '/home/cloudera/fortscale/config/asl/models/overriding'
    _MODEL_CONFS_ADDITIONAL_PATH = '/home/cloudera/fortscale/config/asl/models/additional'
    _SCORE_PHASE = 'score_phase'
    _BUILD_MODELS_PHASE = 'build_models_phase'

    def __init__(self,
                 host,
                 is_online_mode,
                 start,
                 data_sources,
                 wait_between_batches,
                 min_free_memory,
                 polling_interval,
                 max_delay,
                 wait_between_loads,
                 max_batch_size,
                 force_max_batch_size_in_minutes,
                 max_gap,
                 convert_to_minutes_timeout,
                 timeoutInSeconds):
        self._host = host
        super(Manager, self).__init__(logger=logger,
                                      host=host,
                                      is_online_mode=is_online_mode,
                                      start=start,
                                      block_on_tables=[data_source_to_enriched_tables[data_source]
                                                       for data_source in data_sources],
                                      wait_between_batches=wait_between_batches,
                                      min_free_memory=min_free_memory,
                                      polling_interval=polling_interval,
                                      max_delay=max_delay,
                                      batch_size_in_hours=1 if is_online_mode
                                      else self._calc_data_sources_size_in_hours_since(data_sources=data_sources,
                                                                                       epochtime=time_utils.get_epochtime(start)))
        self._data_source_to_throttler = dict((data_source, Throttler(logger=logger,
                                                                      host=host,
                                                                      data_source=data_source,
                                                                      max_batch_size=max_batch_size,
                                                                      force_max_batch_size_in_minutes=force_max_batch_size_in_minutes,
                                                                      max_gap=max_gap,
                                                                      convert_to_minutes_timeout=convert_to_minutes_timeout,
                                                                      start=start,
                                                                      end=None)) for data_source in data_sources)
        self._data_sources = data_sources
        self._wait_between_loads = wait_between_loads
        self._timeoutInSeconds = timeoutInSeconds
        self._runner = bdp_utils.run.Runner(name='stepSAM.scores',
                                            logger=logger,
                                            host=host,
                                            block=False)
        self._builder = bdp_utils.run.Runner(name='stepSAM.build_models',
                                             logger=logger,
                                             host=host,
                                             block=True,
                                             block_until_log_reached='Spring context closed')
        self._run_phase = None

    def run(self):
        logger.info('preparing configurations...')
        original_to_backup = {}
        original_to_backup.update(self._prepare_fortscale_streaming_config())
        original_to_backup.update(self._prepare_overriding_model_builders_config())
        original_to_backup.update(self._prepare_additional_model_builders_config())
        logger.info('DONE')
        try:
            if not self._restart_aggregation_task() or \
                    not restart_task(logger=logger, host=self._host, task_name='RAW_EVENTS_SCORING'):
                raise Exception('failed to restart tasks')
            self._run_phase = Manager._SCORE_PHASE
            super(Manager, self).run()
            if not self._is_online_mode:
                self._run_phase = Manager._BUILD_MODELS_PHASE
                super(Manager, self).run()
                self._run_phase = Manager._SCORE_PHASE
                super(Manager, self).run()
        finally:
            self._revert_configurations(original_to_backup)

    def _prepare_fortscale_streaming_config(self):
        logger.info('updating fortscale-overriding-streaming.properties...')
        original_to_backup = {
            Manager._FORTSCALE_OVERRIDING_PATH: io.backup(path=Manager._FORTSCALE_OVERRIDING_PATH) \
                if os.path.isfile(Manager._FORTSCALE_OVERRIDING_PATH) \
                else None
        }
        if self._wait_between_loads is None:
            wait_between_loads = impala_stats.calc_time_to_process_most_sparse_day(connection=self._impala_connection,
                                                                                   data_sources=self._data_sources)
            logger.info('time to process most sparse day is ' + str(wait_between_loads / 60) + ' minutes')
            lower_bound = 1 * 60
            upper_bound = 10 * 60
            if wait_between_loads > upper_bound:
                logger.info('time to process most sparse day is too big. Truncating to ' +
                            str((upper_bound / 60)) + ' minutes')
                wait_between_loads = upper_bound
            if wait_between_loads < lower_bound:
                logger.info('time to process most sparse day is too small. Truncating to ' +
                            str((lower_bound / 60)) + ' minutes')
                wait_between_loads = lower_bound
        else:
            wait_between_loads = self._wait_between_loads
        configuration = ['',
                         'fortscale.model.wait.sec.between.loads=' + str(wait_between_loads),
                         'fortscale.model.max.sec.diff.before.outdated=86400']
        logger.info('overriding the following:' + '\n\t'.join(configuration))
        with open(Manager._FORTSCALE_OVERRIDING_PATH, 'a') as f:
            f.write('\n'.join(configuration))
        return original_to_backup

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
        original_to_backup = {}
        if os.path.exists(Manager._MODEL_CONFS_ADDITIONAL_PATH):
            for filename in os.listdir(Manager._MODEL_CONFS_ADDITIONAL_PATH):
                file_path = os.path.join(Manager._MODEL_CONFS_ADDITIONAL_PATH, filename)
                with open(file_path, 'r') as f:
                    model_confs = json.load(f)
                self._update_model_confs(path=file_path,
                                         model_confs=model_confs,
                                         original_to_backup=original_to_backup)
        return original_to_backup

    def _revert_configurations(self, original_to_backup):
        logger.info('reverting configurations...')
        for original, backup in original_to_backup.iteritems():
            os.remove(original)
            if backup is not None:
                os.rename(backup, original)
        logger.warning("DONE. Don't forget to restart the task in order to apply them")

    def _prepare_bdp_overrides(self, data_source, start_time_epoch):
        forwarding_batch_size_in_minutes = self._data_source_to_throttler[data_source].get_max_batch_size_in_minutes()
        max_source_destination_time_gap = self._data_source_to_throttler[data_source].get_max_gap_in_minutes() * 60
        max_sync_gap_in_seconds = 2 * 24 * 60 * 60
        diff = forwarding_batch_size_in_minutes * 60 + max_source_destination_time_gap - max_sync_gap_in_seconds
        if diff > 0:
            logger.info('forwardingBatchSizeInMinutes + maxSourceDestinationTimeGap < maxSyncGapInSeconds does '
                        'not hold. Decreasing forwardingBatchSizeInMinutes and maxSourceDestinationTimeGap')
            ratio = 1. * max_source_destination_time_gap / \
                    (forwarding_batch_size_in_minutes * 60 + max_source_destination_time_gap)
            max_source_destination_time_gap -= int(math.ceil(ratio * diff))
            forwarding_batch_size_in_minutes -= int(math.ceil((1 - ratio) * diff / 60))
        overrides = [
            'data_sources = ' + data_source,
            'throttlingSleep = 30',
            'forwardingBatchSizeInMinutes = ' + str(forwarding_batch_size_in_minutes),
            'maxSourceDestinationTimeGap = ' + str(max_source_destination_time_gap),
            # in online mode the script must manage when to create models, so build it once a day
            'buildModelsFirst = ' + str((self._is_online_mode and start_time_epoch % (60 * 60 * 24) == 0) or
                                        self._run_phase == Manager._BUILD_MODELS_PHASE).lower(),
            'maxSyncGapInSeconds = ' + str(max_sync_gap_in_seconds),
            # in online mode we don't want the bdp to sync (because then it'll close the aggregation
            # buckets - and then we won't be able to run the next data source on the same time batch
            # without restarting the task - which is expensive) - so if we sync every minus hour then
            # effectively no sync will happen (WTF???). In offline mode we also don't want the sync
            # to occur (because we don't want the models to be built - this is done in the
            # _BUILD_MODELS_PHASE phase). Giving -1 will do.
            # Note: I don't remember why, byt -1 won't do for the online case.. Sorry
            'secondsBetweenSyncs = ' + str(-3600 if self._is_online_mode else -1)
        ]
        if self._timeoutInSeconds is not None:
            overrides.append('timeoutInSeconds = ' + str(self._timeoutInSeconds))
        return overrides

    def _run_batch(self, start_time_epoch):
        for data_source in self._data_sources:
            logger.info('running batch on ' + data_source + '...')
            end_time_epoch = start_time_epoch + self._batch_size_in_hours * 60 * 60
            overrides = self._prepare_bdp_overrides(data_source=data_source, start_time_epoch=start_time_epoch)
            if self._run_phase == Manager._SCORE_PHASE:
                kill_process = self._runner \
                    .set_start(start_time_epoch) \
                    .set_end(end_time_epoch) \
                    .run(overrides_key='stepSAM', overrides=overrides)
                if not self._validate(data_source=data_source,
                                      start_time_epoch=start_time_epoch,
                                      end_time_epoch=end_time_epoch):
                    return False
                logger.info('making sure bdp process exits...')
                kill_process()
                if self._batch_size_in_hours > 1 and not self._restart_aggregation_task():
                    return False
            elif self._run_phase == Manager._BUILD_MODELS_PHASE:
                self._builder \
                    .set_start(end_time_epoch) \
                    .set_end(end_time_epoch) \
                    .run(overrides_key='stepSAM', overrides=overrides)
            else:
                raise Exception('illegal phase: ' + self._run_phase)
        if self._run_phase == Manager._BUILD_MODELS_PHASE:
            cleanup_everything_but_models(logger=logger,
                                          host=self._host,
                                          clean_overrides_key='stepSAM.cleanup',
                                          start_time_epoch=start_time_epoch,
                                          end_time_epoch=end_time_epoch)
            return update_models_time(logger=logger,
                                      host=self._host,
                                      collection_names_regex='^model_',
                                      time=start_time_epoch)
        return True

    def _validate(self, data_source, start_time_epoch, end_time_epoch):
        return validate_started_processing_everything(host=self._host, data_source=data_source) and \
               block_until_everything_is_validated(host=self._host,
                                                   start_time_epoch=start_time_epoch,
                                                   end_time_epoch=end_time_epoch,
                                                   wait_between_validations=self._polling_interval,
                                                   max_delay=self._max_delay,
                                                   timeout=0,
                                                   polling_interval=0,
                                                   data_sources=[data_source],
                                                   logger=logger)

    def _restart_aggregation_task(self):
        return restart_task(logger=logger, host=self._host, task_name='AGGREGATION_EVENTS_STREAMING')

    def _calc_data_sources_size_in_hours_since(self, data_sources, epochtime):
        max_size = 0
        impala_connection = impala_utils.connect(host=self._host)
        for data_source in data_sources:
            last_event_time = impala_utils.get_last_event_time(connection=impala_connection,
                                                               table=data_source_to_enriched_tables[data_source])
            max_size = max(max_size,
                           math.ceil((time_utils.get_epochtime(last_event_time) - epochtime) / (60 * 60.)))
        return max_size
