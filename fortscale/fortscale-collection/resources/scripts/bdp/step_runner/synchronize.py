import datetime
import logging
import sys
import time
from impala.dbapi import connect

from log import log_and_send_mail

logger = logging.getLogger('step_runner')

from bdp import run_step_and_validate

import os
sys.path.append(os.path.sep.join([os.path.dirname(__file__), '..', '..']))
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
        self._host = host
        self._impala_connection = connect(host=host, port=21050)
        self._last_real_time_synced = time.time()
        self._last_event_synced_time = start
        self._tables = block_on_tables
        self._wait_between_syncs = wait_between_syncs
        self._polling_interval = polling_interval
        self._retro_validation_gap = retro_validation_gap
        self._max_delay = max_delay

    def run(self):
        sync_batch_size_in_hours = 1
        sync_batch_size_in_seconds = sync_batch_size_in_hours * Synchronizer._HOUR
        while True:
            slowest_time = self._get_slowest_table_last_event_time(sync_batch_size_in_hours)
            slowest_data_source_reached_barrier = time_utils.get_timedelta_total_seconds(slowest_time - self._last_event_synced_time) >= \
                                                  sync_batch_size_in_seconds
            if slowest_data_source_reached_barrier:
                self._barrier_reached(sync_batch_size_in_hours)
            else:
                if time.time() - self._last_real_time_synced > self._max_delay:
                    msg = 'no raw events for more than ' + str(int(self._max_delay / (60 * 60))) + ' hours'
                    log_and_send_mail(msg)
                logger.info('data sources have not filled an hour yet - going to sleep for ' +
                            str(int(self._polling_interval / 60)) +
                            ' minute' + ('s' if self._polling_interval / 60 > 1 else ''))
                time.sleep(self._polling_interval)

    def _barrier_reached(self, sync_batch_size_in_hours):
        hours_str = str(sync_batch_size_in_hours) + ' hour' + ('s' if sync_batch_size_in_hours > 1 else '')
        logger.info(hours_str + ' has been filled - running bdp for the next ' + hours_str)
        self._last_real_time_synced = time.time()
        run_step_and_validate(host=self._host,
                              start_time_epoch=time_utils.get_timedelta_total_seconds(self._last_event_synced_time - datetime.datetime.utcfromtimestamp(0)),
                              hours_to_run=sync_batch_size_in_hours,
                              retro_validation_gap=self._retro_validation_gap,
                              wait_between_validations=self._polling_interval,
                              max_delay=self._max_delay)
        self._last_event_synced_time += datetime.timedelta(hours=sync_batch_size_in_hours)
        wait_time = self._wait_between_syncs - (time.time() - self._last_real_time_synced)
        if wait_time > 0:
            logger.info('going to sleep for ' + str(int(wait_time / 60)) + ' minutes')
            time.sleep(wait_time)

    def _get_slowest_table_last_event_time(self, sync_batch_size_in_hours):
        logger.info('polling impala tables (to see if we can sync ' +
                     time_utils.interval_to_str(self._last_event_synced_time,
                                                self._last_event_synced_time + datetime.timedelta(hours=sync_batch_size_in_hours)) + ')...')
        return min([self._get_last_event(table) for table in self._tables])

    def _get_last_event(self, table):
        c = self._impala_connection.cursor()
        # note: we need to query the next day as well for the case where self._last_event_synced_time is 23:00
        c.execute('select max(date_time) from ' + table +
                  ' where yearmonthday=' + time_utils.time_to_impala_partition(self._last_event_synced_time) +
                  ' or yearmonthday=' + (time_utils.time_to_impala_partition(self._last_event_synced_time + datetime.timedelta(days=1))))
        res = c.next()[0]
        if res is None:
            logger.info('impala table ' + table + ' has no data since last sync')
        else:
            logger.info('impala table ' + table + ' has reached to ' + str(res))
        return res or datetime.datetime.utcfromtimestamp(0)
