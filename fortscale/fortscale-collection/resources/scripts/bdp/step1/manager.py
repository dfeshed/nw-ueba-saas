import logging
import time
from impala.dbapi import connect

logger = logging.getLogger('step1')


class Manager:
    def __init__(self,
                 host,
                 table_name,
                 max_batch_size,
                 force_max_batch_size_in_minutes,
                 max_gap):
        self._impala_connection = connect(host=host, port=21050)
        self._table_name = table_name
        self._max_batch_size = max_batch_size
        self._max_batch_size_minutes = force_max_batch_size_in_minutes
        self._max_gap = max_gap
        self._max_gap_minutes = None
        self._time_granularity_minutes = 5
        self._count_per_time_bucket = None

    def run(self):
        pass

    def _calc_count_per_time_bucket(self):
        if self._count_per_time_bucket is None:
            TIMEOUT = 60
            self._count_per_time_bucket = []
            start_time = time.time()
            for partition in self._get_partitions():
                self._count_per_time_bucket += self._get_count_per_time_bucket(partition)
                if time.time() - start_time > TIMEOUT:
                    break
        return self._count_per_time_bucket

    def _get_partitions(self):
        c = self._impala_connection.cursor()
        c.execute('show partitions ' + self._table_name)
        partitions = [p[0] for p in c]
        c.close()
        return partitions

    def _get_count_per_time_bucket(self, partition):
        if 24 * 60 % self._time_granularity_minutes != 0:
            raise Exception('time_granularity_minutes must divide a day to equally sized buckets')
        c = self._impala_connection.cursor()
        c.execute('select count(*), floor(date_time_unix / (60 * ' + str(self._time_granularity_minutes) +
                  ')) time_bucket from ' + self._table_name +
                  ' where yearmonthday = ' + partition +
                  ' group by time_bucket order by time_bucket')
        count_per_time_bucket = [res[0] for res in c]
        c.close()
        return count_per_time_bucket

    def _calc_biggest_time_period_which_fits_num_of_events(self, max_num_of_events_per_batch):
        count_per_time_bucket = self._calc_count_per_time_bucket()
        for time_buckets_in_batch in xrange(len(count_per_time_bucket), 0, -1):
            for batch_start in xrange(0, len(count_per_time_bucket), time_buckets_in_batch):
                if sum(count_per_time_bucket[batch_start:batch_start + time_buckets_in_batch]) > max_num_of_events_per_batch:
                    break
            else:
                return time_buckets_in_batch * self._time_granularity_minutes
        return 0

    def get_max_batch_size_in_minutes(self):
        self._max_batch_size_minutes = self._max_batch_size_minutes or \
                                       self._calc_biggest_time_period_which_fits_num_of_events(
                                           max_num_of_events_per_batch=self._max_batch_size
                                       )
        return self._max_batch_size_minutes

    def get_max_gap_in_minutes(self):
        self._max_gap_minutes = self._max_gap_minutes or \
                                self._calc_biggest_time_period_which_fits_num_of_events(
                                    max_num_of_events_per_batch=self._max_gap
                                )
        return self._max_gap_minutes
