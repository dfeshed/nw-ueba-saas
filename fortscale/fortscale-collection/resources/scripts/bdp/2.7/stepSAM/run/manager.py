import logging
import json
from cm_api.api_client import ApiResource
import math
import sys
import os
import impala_stats

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from validation.started_processing_everything.validation import validate_started_processing_everything
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils.manager import OnlineManager
from bdp_utils.data_sources import data_source_to_enriched_tables
from bdp_utils import overrides
from bdp_utils.throttling import Throttler
import bdp_utils.run
from step2.validation.validation import block_until_everything_is_validated
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..']))
from automatic_config.common.utils import time_utils, impala_utils, io

logger = logging.getLogger('stepSAM')


class Manager(OnlineManager):
    _FORTSCALE_OVERRIDING_PATH = '/home/cloudera/fortscale/streaming/config/fortscale-overriding-streaming.properties'
    _MODEL_CONFS_OVERRIDING_PATH = '/home/cloudera/fortscale/config/asl/models/overriding'

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
                 convert_to_minutes_timeout):
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
        self._runner = bdp_utils.run.Runner(name='stepSAM',
                                            logger=logger,
                                            host=host,
                                            block=False)

    def run(self):
        logger.info('preparing configurations...')
        original_to_backup = {}
        original_to_backup.update(self._prepare_fortscale_streaming_config())
        original_to_backup.update(self._prepare_model_builders_config())
        logger.info('DONE')
        try:
            self._restart_task()
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

    def _prepare_model_builders_config(self):
        original_to_backup = {}
        for data_source in self._data_sources:
            data_source_raw_events_model_file_name = 'raw_events_model_confs_' + data_source + '.json'
            data_source_model_confs_path = Manager._MODEL_CONFS_OVERRIDING_PATH + '/' + \
                                           data_source_raw_events_model_file_name
            with overrides.open_overrides_file(overriding_path=data_source_model_confs_path,
                                               jar_name='fortscale-ml-1.1.0-SNAPSHOT.jar',
                                               path_in_jar='config/asl/models/' +
                                                       data_source_raw_events_model_file_name) as f:
                model_confs = json.load(f)
            updated = False
            for model_conf in model_confs['ModelConfs']:
                builder = model_conf['builder']
                if builder['type'] == 'category_rarity_model_builder':
                    builder['entriesToSaveInModel'] = 100000
                    updated = True
            if updated:
                logger.info('updating category rarity model builders of ' +
                            data_source_raw_events_model_file_name + '...')
            original_to_backup[data_source_model_confs_path] = \
                io.backup(path=data_source_model_confs_path) \
                    if os.path.isfile(data_source_model_confs_path) \
                    else None
            with io.FileWriter(data_source_model_confs_path) as f:
                json.dump(model_confs, f, indent=4)
        return original_to_backup

    def _revert_configurations(self, original_to_backup):
        logger.info('reverting configurations...')
        for original, backup in original_to_backup.iteritems():
            os.remove(original)
            if backup is not None:
                os.rename(backup, original)
        logger.warning("DONE. Don't forget to restart the task in order to apply them")

    def _run_batch(self, start_time_epoch):
        for data_source in self._data_sources:
            end_time_epoch = start_time_epoch + self._batch_size_in_hours * 60 * 60
            kill_process = self._runner \
                .set_start(start_time_epoch) \
                .set_end(end_time_epoch) \
                .run(overrides_key='stepSAM',
                     overrides=[
                         'dataSource = ' + data_source,
                         'forwardingBatchSizeInMinutes = ' +
                         str(self._data_source_to_throttler[data_source].get_max_batch_size_in_minutes()),
                         'maxSourceDestinationTimeGap = ' +
                         str(self._data_source_to_throttler[data_source].get_max_gap_in_minutes() * 60),
                         # in online mode the script must manage when to create models, so build it once a day
                         'buildModelsFirst = ' + str(self._is_online_mode and
                                                     start_time_epoch % (60 * 60 * 24) == 0).lower(),
                         # in online mode we don't want the bdp to sync (because then it'll close the aggregation
                         # buckets - and then we won't be able to run the next data source on the same time batch
                         # without restarting the task - which is expensive) - so if we sync every minus hour then
                         # effectively no sync will happen (WTF???). In online mode on the other hand we want to
                         # build models once a day
                         'secondsBetweenSyncs = ' + str(-3600 if self._is_online_mode else 24 * 60 * 60)
                     ])
            if not self._validate(data_source=data_source,
                                  start_time_epoch=start_time_epoch,
                                  end_time_epoch=end_time_epoch):
                return False
            logger.info('making sure bdp process exits...')
            kill_process()
            if self._batch_size_in_hours > 1 and not self._restart_task():
                return False
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

    def _restart_task(self):
        aggregation_task_id = 'AGGREGATION_EVENTS_STREAMING'
        logger.info('restarting samza task ' + aggregation_task_id + '...')
        api = ApiResource(self._host, username='admin', password='admin')
        cluster = filter(lambda c: c.name == 'cluster', api.get_all_clusters())[0]
        fsstreaming = filter(lambda service: service.name == 'fsstreaming', cluster.get_all_services())[0]
        aggregation_task = [s for s in fsstreaming.get_all_roles() if s.type == aggregation_task_id][0]
        if fsstreaming.restart_roles(aggregation_task.name)[0].wait().success:
            logger.info('task restarted successfully')
            return True
        else:
            logger.error('task failed to restart')
            return False

    def _calc_data_sources_size_in_hours_since(self, data_sources, epochtime):
        max_size = 0
        impala_connection = impala_utils.connect(host=self._host)
        for data_source in data_sources:
            last_event_time = impala_utils.get_last_event_time(connection=impala_connection,
                                                               table=data_source_to_enriched_tables[data_source])
            max_size = max(max_size,
                           math.ceil((time_utils.get_epochtime(last_event_time) - epochtime) / (60 * 60.)))
        return max_size
