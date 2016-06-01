import logging
import sys
import os

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils.manager import OnlineManager
from bdp_utils.data_sources import data_source_to_enriched_tables
import bdp_utils.run

logger = logging.getLogger('stepSAM')


class Manager(OnlineManager):
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
                                      else self._calc_data_sources_size_in_hours_since(start))
        self._data_sources = data_sources
        self._runner = bdp_utils.run.Runner(name='stepSAM',
                                            logger=logger,
                                            host=host,
                                            block=False)

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
            self._validate()
            logger.info('making sure bdp process exits...')
            kill_process()
            self._restart_task_if_needed()
        return True

    def _restart_task_if_needed(self):
        if self._batch_size_in_hours > 1:
            # TODO: implement
            pass

    def _calc_data_sources_size_in_hours_since(self, epochtime):
        # TODO: implement
        pass

    def _validate(self):
        # TODO: implement
        pass
