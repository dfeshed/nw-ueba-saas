import logging
import time
import re
from collections import namedtuple

import os
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from validation.missing_events.validation import validate_no_missing_events
from validation.scores_anomalies.__main__ import run as run_scores_anomalies
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
import bdp_utils.run
from bdp_utils.data_sources import data_source_to_enriched_tables
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from automatic_config.common.utils import time_utils, impala_utils

logger = logging.getLogger('step1')


def create_pojo(dictionary):
    return namedtuple('POJO', dictionary.keys())(**dictionary)


class Manager:
    def __init__(self,
                 host,
                 data_source,
                 max_batch_size,
                 force_max_batch_size_in_minutes,
                 max_gap,
                 convert_to_minutes_timeout,
                 validation_timeout,
                 validation_polling_interval,
                 start,
                 end,
                 scores_anomalies_path,
                 scores_anomalies_warming_period,
                 scores_anomalies_threshold):
        self._runner = bdp_utils.run.Runner(name='Bdp' +
                                                 self._kabab_to_camel_case(data_source) +
                                                 'EnrichedToScoring',
                                            logger=logger,
                                            host=host,
                                            block=True)
        self._data_source = data_source
        self._host = host
        self._impala_connection = impala_utils.connect(host=host)
        self._max_batch_size = max_batch_size
        self._max_batch_size_minutes = force_max_batch_size_in_minutes
        self._convert_to_minutes_timeout = convert_to_minutes_timeout
        self._max_gap = max_gap
        self._max_gap_minutes = None
        self._validation_timeout = validation_timeout
        self._validation_polling_interval = validation_polling_interval
        self._start = start
        self._end = end
        self._time_granularity_minutes = 5
        self._count_per_time_bucket = None
        self._scores_anomalies_path = scores_anomalies_path
        self._scores_anomalies_warming_period = scores_anomalies_warming_period
        self._scores_anomalies_threshold = scores_anomalies_threshold

    @staticmethod
    def _kabab_to_camel_case(s):
        return re.sub('_(.)', lambda match: match.group(1).upper(), '_' + s)

    def run(self):
        self._runner \
            .set_start(self._start) \
            .set_end(self._end) \
            .run(overrides_key='step1',
                 overrides=[
                     'forwardingBatchSizeInMinutes = ' + str(self.get_max_batch_size_in_minutes()),
                     'maxSourceDestinationTimeGap = ' + str(self.get_max_gap_in_minutes() * 60)
                 ])

    def _calc_count_per_time_bucket(self):
        if self._count_per_time_bucket is None:
            self._count_per_time_bucket = []
            start_time = time.time()
            for partition in self._get_partitions():
                self._count_per_time_bucket += self._get_count_per_time_bucket(partition)
                if time.time() - start_time > self._convert_to_minutes_timeout:
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
                                         timeout=self._validation_timeout,
                                         polling_interval=self._validation_polling_interval,
                                         start=self._start,
                                         end=self._end)
        if self._data_source == 'vpn':
            res &= validate_no_missing_events(host=self._host,
                                              data_source='vpn_session',
                                              timeout=self._validation_timeout,
                                              polling_interval=self._validation_polling_interval,
                                              start=self._start,
                                              end=self._end)
        run_scores_anomalies(arguments=create_pojo({
            'host': self._host,
            'path': self._scores_anomalies_path,
            'data_sources': [self._data_source],
            'start': self._start,
            'end': self._end,
            'warming_period': self._scores_anomalies_warming_period,
            'score_fields': None,
            'threshold': self._scores_anomalies_threshold
        }), should_query=True, should_find_anomalies=True)

        return res
