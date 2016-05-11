import logging
import time
import re

import os
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from validation.missing_events.validation import validate_no_missing_events
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils import run as run_bdp
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from utils.data_sources import data_source_to_enriched_tables
from automatic_config.common.utils import time_utils, impala_utils

logger = logging.getLogger('step1')


class Manager:
    def __init__(self,
                 host,
                 data_source,
                 max_batch_size,
                 force_max_batch_size_in_minutes,
                 max_gap,
                 validation_timeout,
                 validation_polling_interval,
                 start,
                 end):
        self._data_source = data_source
        if not os.path.isfile(self._get_bdp_properties_file_name()):
            raise Exception(self._get_bdp_properties_file_name() +
                            ' does not exist. Please download this file from google drive')
        self._host = host
        self._impala_connection = impala_utils.connect(host=host)
        self._max_batch_size = max_batch_size
        self._max_batch_size_minutes = force_max_batch_size_in_minutes
        self._max_gap = max_gap
        self._max_gap_minutes = None
        self._validation_timeout = validation_timeout
        self._validation_polling_interval = validation_polling_interval
        self._start = start
        self._end = end
        self._time_granularity_minutes = 5
        self._count_per_time_bucket = None

    def _get_bdp_properties_file_name(self):
        return '/home/cloudera/devowls/Bdp' + self._data_source[0].upper() + \
               re.sub('_(.)', lambda match: match.group(1).upper(), self._data_source[1:]) + \
               'EnrichedToScoring.properties'

    def run(self):
        run_bdp(logger=logger,
                path_to_bdp_properties=self._get_bdp_properties_file_name(),
                start=self._start,
                end=self._end,
                block=True,
                additional_cmd_params=['forwardingBatchSizeInMinutes=' + self.get_max_batch_size_in_minutes(),
                                       'maxSourceDestinationTimeGap=' + self.get_max_gap_in_minutes()])

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
        c.execute('show partitions ' + data_source_to_enriched_tables[self._data_source])
        partitions = [p[0] for p in c
                      if time_utils.get_impala_partition(self._start) <= p[0] < time_utils.get_impala_partition(self._end)]
        c.close()
        return partitions

    def _get_count_per_time_bucket(self, partition):
        if 24 * 60 % self._time_granularity_minutes != 0:
            raise Exception('time_granularity_minutes must divide a day to equally sized buckets')
        c = self._impala_connection.cursor()
        c.execute('select count(*), floor(date_time_unix / (60 * ' + str(self._time_granularity_minutes) +
                  ')) time_bucket from ' + data_source_to_enriched_tables[self._data_source] +
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

    def validate(self):
        res = validate_no_missing_events(host=self._host,
                                         data_source=self._data_source,
                                         timeout=self._validation_timeout * 60,
                                         polling_interval=60 * self._validation_polling_interval,
                                         start=self._start,
                                         end=self._end)
        if self._data_source == 'vpn':
            res += validate_no_missing_events(host=self._host,
                                              data_source='vpn_session',
                                              timeout=self._validation_timeout * 60,
                                              polling_interval=60 * self._validation_polling_interval,
                                              start=self._start,
                                              end=self._end)

        return res
