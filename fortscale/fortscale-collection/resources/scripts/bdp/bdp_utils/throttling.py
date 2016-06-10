import time
import re

import os
import sys
from data_sources import data_source_to_enriched_tables
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from automatic_config.common.utils import time_utils, impala_utils


class Throttler:
    def __init__(self,
                 logger,
                 host,
                 data_source,
                 max_batch_size,
                 force_max_batch_size_in_minutes,
                 max_gap,
                 convert_to_minutes_timeout,
                 start,
                 end):
        self._logger = logger
        self._data_source = data_source
        self._host = host
        self._impala_connection = impala_utils.connect(host=host)
        self._max_batch_size = max_batch_size
        self._force_max_batch_size_in_minutes = force_max_batch_size_in_minutes
        self._max_batch_size_minutes = force_max_batch_size_in_minutes
        self._convert_to_minutes_timeout = convert_to_minutes_timeout
        self._max_gap = max_gap
        self._max_gap_minutes = None
        self._start = start
        self._end = end
        self._time_granularity_minutes = 5
        self._count_per_time_bucket = None
        self._validate_arguments()

    def _validate_arguments(self):
        if self._force_max_batch_size_in_minutes is None and self._max_gap < self._max_batch_size:
            raise Exception('max_gap must be greater or equal to max_batch_size')
        max_batch_size_in_minutes = self.get_max_batch_size_in_minutes()
        if max_batch_size_in_minutes < 15 and self._force_max_batch_size_in_minutes is None:
            raise Exception('max_batch_size is relatively small. It translates to forwardingBatchSizeInMinutes=' +
                            str(max_batch_size_in_minutes) +
                            '. If you wish to proceed, run the script with "--force_max_batch_size_in_minutes ' +
                            str(max_batch_size_in_minutes) + '"')
        self._logger.info('using batch size of ' + str(max_batch_size_in_minutes) + ' minutes')
        max_gap_in_minutes = self.get_max_gap_in_minutes()
        if self._force_max_batch_size_in_minutes is not None and \
                        max_gap_in_minutes < self._force_max_batch_size_in_minutes:
            raise Exception('max_gap is too small. It translated to maxSourceDestinationTimeGap=' +
                            str(max_gap_in_minutes) +
                            ' which is smaller than what was provided by --force_max_batch_size_in_minutes')
        self._logger.info('using gap size of ' + str(max_gap_in_minutes) + ' minutes')

    @staticmethod
    def _kabab_to_camel_case(s):
        return re.sub('_(.)', lambda match: match.group(1).upper(), '_' + s)

    def _calc_count_per_time_bucket(self):
        if self._count_per_time_bucket is None:
            self._count_per_time_bucket = []
            start_time = time.time()
            for partition in self._get_partitions():
                self._count_per_time_bucket += self._get_count_per_time_bucket(partition)
                if 0 <= self._convert_to_minutes_timeout < time.time() - start_time:
                    break
        return self._count_per_time_bucket

    def _get_partitions(self):
        c = self._impala_connection.cursor()
        c.execute('show partitions ' + data_source_to_enriched_tables[self._data_source])
        partitions = [p[0] for p in c
                      if p[0] != 'Total' and
                      time_utils.get_impala_partition(self._start) <= p[0] and
                      (self._end is None or p[0] < time_utils.get_impala_partition(self._end))]
        c.close()
        return partitions

    def _get_count_per_time_bucket(self, partition):
        if 24 * 60 % self._time_granularity_minutes != 0:
            raise Exception('time_granularity_minutes must divide a day to equally sized buckets')
        c = self._impala_connection.cursor()
        c.execute('select floor(date_time_unix / (60 * ' + str(self._time_granularity_minutes) +
                  ')) time_bucket, count(*) from ' + data_source_to_enriched_tables[self._data_source] +
                  ' where yearmonthday = ' + partition +
                  ' group by time_bucket')
        buckets = dict(((time_utils.get_epochtime(partition) + minute * 60) / (60 * self._time_granularity_minutes), 0)
                       for minute in xrange(60 * 24))
        buckets.update(dict(c))
        c.close()
        return [count for time, count in sorted(buckets.iteritems())]

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
