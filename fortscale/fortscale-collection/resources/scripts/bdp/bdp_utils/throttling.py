import re

import os
import sys
from data_sources import data_source_to_enriched_tables
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from automatic_config.common.utils import impala_utils


class Throttler:
    def __init__(self,
                 logger,
                 host,
                 data_source,
                 max_batch_size,
                 force_max_batch_size_in_minutes,
                 max_gap,
                 force_max_gap_in_seconds,
                 convert_to_minutes_timeout,
                 start,
                 end):
        self._logger = logger
        self._data_source = data_source
        self._host = host
        self._max_batch_size = max_batch_size
        self._force_max_batch_size_in_minutes = force_max_batch_size_in_minutes
        self._max_batch_size_minutes = force_max_batch_size_in_minutes
        self._convert_to_minutes_timeout = convert_to_minutes_timeout
        self._max_gap = max_gap
        self._max_gap_seconds = force_max_gap_in_seconds if force_max_gap_in_seconds is not None else None
        self._start = start
        self._end = end
        self._time_granularity_minutes = 5
        self._count_per_time_bucket = None
        self._validate_arguments()

    def _validate_arguments(self):
        max_batch_size_in_minutes = self.get_max_batch_size_in_minutes()
        max_gap_in_seconds = self.get_max_gap_in_seconds()
        if max_gap_in_seconds < max_batch_size_in_minutes * 60:
            raise Exception('max gap must be greater or equal to max batch size')
        if max_batch_size_in_minutes < 15 and self._force_max_batch_size_in_minutes is None:
            raise Exception('max_batch_size is relatively small. It translates to forwardingBatchSizeInMinutes=' +
                            str(max_batch_size_in_minutes) +
                            '. If you wish to proceed, run the script with "--force_max_batch_size_in_minutes ' +
                            str(max_batch_size_in_minutes) + '"')
        self._logger.info('using batch size of ' + str(max_batch_size_in_minutes) + ' minutes')
        self._logger.info('using gap size of ' + str(max_gap_in_seconds) + ' seconds')

    @staticmethod
    def _kabab_to_camel_case(s):
        return re.sub('_(.)', lambda match: match.group(1).upper(), '_' + s)

    def _calc_count_per_time_bucket(self):
        if self._count_per_time_bucket is None:
            self._count_per_time_bucket = impala_utils.calc_count_per_time_bucket(host=self._host,
                                                                                  table=data_source_to_enriched_tables[self._data_source],
                                                                                  time_granularity_minutes=self._time_granularity_minutes,
                                                                                  start=self._start,
                                                                                  end=self._end,
                                                                                  timeout=self._convert_to_minutes_timeout)
        return self._count_per_time_bucket

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

    def get_max_gap_in_seconds(self):
        self._max_gap_seconds = self._max_gap_seconds or \
                                self._calc_biggest_time_period_which_fits_num_of_events(
                                    max_num_of_events_per_batch=self._max_gap
                                ) * 60
        return self._max_gap_seconds
