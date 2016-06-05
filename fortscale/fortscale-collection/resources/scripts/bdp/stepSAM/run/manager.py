import logging
import json
from cm_api.api_client import ApiResource
import math
import sys
import os

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from validation.started_processing_everything.validation import validate_started_processing_everything
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils.manager import OnlineManager
from bdp_utils.data_sources import data_source_to_enriched_tables
from bdp_utils import overrides
import bdp_utils.run
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from automatic_config.common.utils import time_utils, io

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
                 max_delay):
        super(Manager, self).__init__(host=host,
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
                                                                                       epochtime=start))
        self._data_sources = data_sources
        self._runner = bdp_utils.run.Runner(name='stepSAM',
                                            logger=logger,
                                            host=host,
                                            block=False)

    def run(self):
        self._prepare_configurations()
        try:
            self._restart_task()
            super(Manager, self).run()
        finally:
            self._revert_configurations()

    def _prepare_configurations(self):
        logger.info('preparing configurations...')
        self._original_to_backup = {}
        # fortscale-overriding-streaming.properties:
        self._original_to_backup[Manager._FORTSCALE_OVERRIDING_PATH] = \
            io.backup(path=Manager._FORTSCALE_OVERRIDING_PATH) \
                if os.path.isfile(Manager._FORTSCALE_OVERRIDING_PATH) \
                else None
        with open(Manager._FORTSCALE_OVERRIDING_PATH, 'a') as f:
            f.write('\n'.join([
                '',
                'fortscale.model.wait.sec.between.loads=0',
                'fortscale.model.max.sec.diff.before.outdated=86400'
            ]))

        # model builders:
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
            self._original_to_backup[data_source_model_confs_path] = \
                io.backup(path=data_source_model_confs_path) \
                    if os.path.isfile(data_source_model_confs_path) \
                    else None
            with io.FileWriter(data_source_model_confs_path) as f:
                json.dump(model_confs, f)

    def _revert_configurations(self):
        logger.info('reverting configurations...')
        for original, backup in self._original_to_backup.iteritems():
            os.remove(original)
            if backup is not None:
                os.rename(backup, original)
        logger.warning("DONE. Don't forget to restart the task in order to apply them")

    def _run_batch(self, start_time_epoch):
        for data_source in self._data_sources:
            kill_process = self._runner \
                .set_start(start_time_epoch) \
                .set_end(start_time_epoch + self._batch_size_in_hours * 60 * 60) \
                .run(overrides_key='stepSAM',
                     overrides=[
                         'dataSource = ' + data_source,
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
            if not validate_started_processing_everything(host=self._host, data_source=data_source):
                return False
            logger.info('making sure bdp process exits...')
            kill_process()
            if self._batch_size_in_hours > 1 and not self._restart_task():
                return False
        return True

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
        for data_source in data_sources:
            table = data_source_to_enriched_tables[data_source]
            last_partition = self._get_partitions(table=table)[-1]
            last_event_datetime = self._get_last_event_datetime(table=table, partition=last_partition)
            max_size = max(max_size,
                           math.ceil((time_utils.get_epochtime(last_event_datetime) - epochtime) / (60 * 60.)))
        return max_size
