import datetime
import re
import subprocess
import sys
import time
import os

from log import log_and_send_mail
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from automatic_config.common.utils import time_utils, impala_utils


class OnlineManager:
    _HOUR = 60 * 60

    def __init__(self,
                 logger,
                 host,
                 is_online_mode,
                 start,
                 block_on_tables,
                 wait_between_batches,
                 min_free_memory,
                 polling_interval,
                 max_delay,
                 batch_size_in_hours):
        self._logger = logger
        self._impala_connection = impala_utils.connect(host=host)
        self._is_online_mode = is_online_mode
        self._last_job_real_time = time.time()
        self._last_batch_end_time = time_utils.get_datetime(start)
        self._block_on_tables = block_on_tables
        self._wait_between_batches = wait_between_batches
        self._min_free_memory = min_free_memory
        self._polling_interval = polling_interval
        self._max_delay = max_delay
        self._batch_size_in_hours = batch_size_in_hours

    def _run_batch(self, start_time_epoch):
        raise NotImplementedException()

    def run(self):
        while True:
            if self._is_online_mode:
                self._wait_until(self._reached_next_barrier)
            self._wait_until(self._enough_memory)
            if not self._is_online_mode and self._reached_next_barrier() is not True:
                self._logger.info("there's not enough data to fill a whole batch - running partial batch...")
                self._run_next_batch()
                self._logger.info('DONE - no more data')
                break
            self._logger.info(str(self._batch_size_in_hours) + ' hour' +
                              ('s' if self._batch_size_in_hours > 1 else '') + ' have been filled')
            self._run_next_batch()

    def _wait_until(self, cb):
        while True:
            fail_msg = cb()
            if type(fail_msg) == str:
                if time.time() - self._last_job_real_time > self._max_delay:
                    log_and_send_mail('failed for more than ' +
                                      str(int(self._max_delay / (60 * 60))) + ' hours: ' + fail_msg)
                self._logger.info(fail_msg + '. going to sleep for ' + str(int(self._polling_interval / 60)) +
                                  ' minute' + ('s' if self._polling_interval / 60 > 1 else ''))
                time.sleep(self._polling_interval)
            elif fail_msg:
                break

    def _reached_next_barrier(self):
        self._logger.info('polling impala tables (to see if we can run next batch ' +
                          time_utils.interval_to_str(self._last_batch_end_time,
                                                     self._last_batch_end_time +
                                                     datetime.timedelta(hours=self._batch_size_in_hours)) + ')...')
        for table in self._block_on_tables:
            if not self._has_table_reached_barrier(table=table):
                return 'data sources have not filled an hour yet'
        return True

    def _enough_memory(self):
        output = subprocess.Popen(['free', '-b'], stdout=subprocess.PIPE).communicate()[0]
        free_memory = int(re.search('(\d+)\W*$', output.split('\n')[2]).groups()[0])
        return free_memory >= self._min_free_memory or \
               'not enough free memory (only ' + str(free_memory / 1024 ** 3) + ' GB)'

    def _run_next_batch(self):
        self._logger.info('running next batch...')
        self._last_job_real_time = time.time()
        last_batch_end_time_epoch = time_utils.get_epochtime(self._last_batch_end_time)
        self._run_batch(start_time_epoch=last_batch_end_time_epoch)
        self._last_batch_end_time += datetime.timedelta(hours=self._batch_size_in_hours)
        wait_time = self._wait_between_batches - (time.time() - self._last_job_real_time)
        if wait_time > 0:
            self._logger.info('going to sleep for ' + str(int(wait_time / 60)) + ' minutes')
            time.sleep(wait_time)

    def _get_partitions(self, table):
        c = self._impala_connection.cursor()
        c.execute('show partitions ' + table)
        partitions = [p[0] for p in c if p[0] != 'Total']
        c.close()
        return partitions

    def _has_table_reached_barrier(self, table):
        for partition in self._get_partitions(table=table):
            if partition < time_utils.get_impala_partition(self._last_batch_end_time):
                continue
            c = self._impala_connection.cursor()
            c.execute('select max(date_time) from ' + table + ' where yearmonthday=' + partition)
            res = c.next()[0]
            c.close()
            if res is not None and time_utils.get_timedelta_total_seconds(
                            res - self._last_batch_end_time) >= self._batch_size_in_hours * OnlineManager._HOUR:
                self._logger.info('impala table ' + table + ' has reached to at least ' + str(res))
                return True
        self._logger.info('impala table ' + table + ' has not enough data since last batch')
        return False
