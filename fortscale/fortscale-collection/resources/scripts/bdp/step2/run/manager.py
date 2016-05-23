import datetime
import logging
import re
import subprocess
import sys
import time
from impala.dbapi import connect
import os
from job import validate, run as run_job

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from bdp_utils.log import log_and_send_mail
from bdp_utils.kafka import send
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from automatic_config.common.utils import time_utils

logger = logging.getLogger('step2')


class Manager:
    _HOUR = 60 * 60

    def __init__(self,
                 host,
                 is_online_mode,
                 start,
                 block_on_tables,
                 wait_between_batches,
                 min_free_memory,
                 polling_interval,
                 validation_batches_delay,
                 max_delay,
                 batch_size_in_hours):
        self._host = host
        self._is_online_mode = is_online_mode
        self._impala_connection = connect(host=host, port=21050)
        self._last_job_real_time = time.time()
        self._last_batch_end_time = start
        self._tables = block_on_tables
        self._wait_between_batches = wait_between_batches
        self._min_free_memory = min_free_memory
        self._polling_interval = polling_interval
        self._validation_batches_delay = validation_batches_delay
        self._max_delay = max_delay
        self._batch_size_in_hours = batch_size_in_hours

    def _wait_until(self, cb):
        while True:
            fail_msg = cb()
            if type(fail_msg) == str:
                if time.time() - self._last_job_real_time > self._max_delay:
                    log_and_send_mail('failed for more than ' +
                                      str(int(self._max_delay / (60 * 60))) + ' hours: ' + fail_msg)
                logger.info(fail_msg + '. going to sleep for ' + str(int(self._polling_interval / 60)) +
                            ' minute' + ('s' if self._polling_interval / 60 > 1 else ''))
                time.sleep(self._polling_interval)
            elif fail_msg:
                break

    def _reached_next_barrier(self):
        slowest_time = self._get_slowest_table_last_event_time()
        slowest_data_source_reached_barrier = time_utils.get_timedelta_total_seconds(
            slowest_time - self._last_batch_end_time) >= self._batch_size_in_hours * Manager._HOUR
        return slowest_data_source_reached_barrier or 'data sources have not filled an hour yet'

    def _enough_memory(self):
        output = subprocess.Popen(['free', '-b'], stdout=subprocess.PIPE).communicate()[0]
        free_memory = int(re.search('(\d+)\W*$', output.split('\n')[2]).groups()[0])
        return free_memory >= self._min_free_memory or \
               'not enough free memory (only ' + str(free_memory / 1024 ** 3) + ' GB)'

    def run(self):
        while True:
            if self._is_online_mode:
                self._wait_until(self._reached_next_barrier)
            elif self._reached_next_barrier() is not True:
                logger.info('sending dummy event...')
                send(logger=logger,
                     host=self._host,
                     topic='fortscale-vpn-event-score-from-hdfs',
                     message='{\\"data_source\\": \\"dummy\\", \\"date_time_unix\\": ' +
                             str(self._last_batch_end_time + 1) + '}')
                validation_end_time = time_utils.get_epoch(self._last_batch_end_time)
                validation_start_time = \
                    validation_end_time - self._validation_batches_delay * self._batch_size_in_hours * 60 * 60
                validate(host=self._host,
                         start_time_epoch=validation_start_time,
                         end_time_epoch=validation_end_time,
                         wait_between_validations=self._polling_interval,
                         max_delay=self._max_delay,
                         timeout=self._timeout,
                         polling_interval=self._polling_interval)
                logger.info('DONE - no more data')
                break
            self._wait_until(self._enough_memory)
            self._barrier_reached()

    def _barrier_reached(self):
        hours_str = str(self._batch_size_in_hours) + ' hour' + ('s' if self._batch_size_in_hours > 1 else '')
        logger.info(hours_str + ' has been filled - running job for the next ' + hours_str)
        self._last_job_real_time = time.time()
        last_batch_end_time_epoch = time_utils.get_epoch(self._last_batch_end_time)
        run_job(start_time_epoch=last_batch_end_time_epoch,
                batch_size_in_hours=self._batch_size_in_hours)
        validation_start_time = \
            last_batch_end_time_epoch - self._validation_batches_delay * self._batch_size_in_hours * 60 * 60
        validate(host=self._host,
                 start_time_epoch=validation_start_time,
                 end_time_epoch=validation_start_time + self._batch_size_in_hours * 60 * 60,
                 wait_between_validations=self._polling_interval,
                 max_delay=self._max_delay,
                 timeout=0,
                 polling_interval=0)
        self._last_batch_end_time += datetime.timedelta(hours=self._batch_size_in_hours)
        wait_time = self._wait_between_batches - (time.time() - self._last_job_real_time)
        if wait_time > 0:
            logger.info('going to sleep for ' + str(int(wait_time / 60)) + ' minutes')
            time.sleep(wait_time)

    def _get_slowest_table_last_event_time(self):
        logger.info('polling impala tables (to see if we can run next batch ' +
                     time_utils.interval_to_str(self._last_batch_end_time,
                                                self._last_batch_end_time +
                                                datetime.timedelta(hours=self._batch_size_in_hours)) + ')...')
        return min([self._get_last_event(table) for table in self._tables])

    def _get_last_event(self, table):
        c = self._impala_connection.cursor()
        c.execute('select max(date_time) from ' + table +
                  ' where yearmonthday=' + time_utils.get_impala_partition(self._last_batch_end_time) +
                  (' or yearmonthday=' + time_utils.get_impala_partition(self._last_batch_end_time +
                                                                         datetime.timedelta(days=1))
                   if time_utils.get_datetime(self._last_batch_end_time).hour == 23
                   else ''))
        res = c.next()[0]
        c.close()
        if res is None:
            logger.info('impala table ' + table + ' has no data since last batch')
        else:
            logger.info('impala table ' + table + ' has reached to at least ' + str(res))
        return res or datetime.datetime.utcfromtimestamp(0)
