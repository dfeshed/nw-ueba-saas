import datetime
import logging
import sys
import time
from impala.dbapi import connect

from bdp import run_step_and_validate

sys.path.append(__file__ + r'\..\..\..')
from automatic_config.common.utils import time_utils


class Synchronizer:
    _HOUR = 60 * 60

    def __init__(self,
                 host,
                 start,
                 block_on_tables,
                 wait_between_syncs,
                 polling_interval,
                 retro_validation_gap,
                 max_delay):
        self._connection = connect(host=host, port=21050)
        self._last_event_synched_time = start
        self._tables = block_on_tables
        self._wait_between_syncs = wait_between_syncs
        self._polling_interval = polling_interval
        self._retro_validation_gap = retro_validation_gap
        self._max_delay = max_delay

    def run(self):
        sync_batch_size_in_hours = 1
        last_real_time_synced = time.time()
        while True:
            logging.info('polling impala tables (query if we can sync ' +
                         time_utils.interval_to_str(self._last_event_synched_time,
                                                    self._last_event_synched_time + datetime.timedelta(hours=sync_batch_size_in_hours)) + ')...')
            barrier = min([self._get_last_event(table) for table in self._tables])
            if (barrier - self._last_event_synched_time).total_seconds() >= Synchronizer._HOUR * sync_batch_size_in_hours:
                logging.info('an hour has been filled - running bdp for ' + str(sync_batch_size_in_hours) +
                             ' hour' + ('s' if sync_batch_size_in_hours > 1 else ''))
                last_real_time_synced = time.time()
                run_step_and_validate(start_time_epoch=(self._last_event_synched_time - datetime.datetime.utcfromtimestamp(0)).total_seconds(),
                                      hours_to_run=sync_batch_size_in_hours,
                                      retro_validation_gap=self._retro_validation_gap,
                                      wait_between_validations=self._polling_interval,
                                      max_delay=self._max_delay)
                self._last_event_synched_time += datetime.timedelta(hours=sync_batch_size_in_hours)
                wait_time = self._wait_between_syncs - (time.time() - last_real_time_synced)
                if wait_time > 0:
                    logging.info('going to sleep for ' + str(int(wait_time / 60)) + ' minutes')
                    time.sleep(wait_time)
            else:
                if time.time() - last_real_time_synced > self._max_delay:
                    logging.critical('no raw events for more than ' + str(int(self._max_delay / (60*60))) + ' hours')
                logging.info('data sources have not filled an hour yet - going to sleep for ' + str(int(self._polling_interval / 60)) +
                             ' minute' + ('s' if self._polling_interval / 60 > 1 else ''))
                time.sleep(self._polling_interval)

    def _get_last_event(self, table):
        c = self._connection.cursor()
        c.execute('select max(date_time) from ' + table +
                  ' where yearmonthday=' + time_utils.time_to_impala_partition(self._last_event_synched_time) +
                  ' or yearmonthday=' + (time_utils.time_to_impala_partition(self._last_event_synched_time + datetime.timedelta(days=1))))
        res = c.next()[0]
        if res is None:
            logging.info('impala table ' + table + ' has no data')
        else:
            logging.info('impala table ' + table + ' has reached to ' + str(res))
        return res or datetime.datetime.utcfromtimestamp(0)
