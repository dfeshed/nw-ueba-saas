import logging
import re
from collections import namedtuple

import os
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from validation.missing_events.validation import validate_no_missing_events
from validation.scores_anomalies.__main__ import run as run_scores_anomalies
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
import bdp_utils.run
from bdp_utils.throttling import Throttler

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
                 force_max_gap_in_seconds,
                 convert_to_minutes_timeout,
                 validation_timeout,
                 validation_polling_interval,
                 start,
                 end,
                 scores_anomalies_path,
                 scores_anomalies_warming_period,
                 scores_anomalies_threshold):
        self._throttler = Throttler(logger=logger,
                                    host=host,
                                    data_source=data_source,
                                    max_batch_size=max_batch_size,
                                    force_max_batch_size_in_minutes=force_max_batch_size_in_minutes.get(data_source) if force_max_batch_size_in_minutes is not None else None,
                                    max_gap=max_gap,
                                    force_max_gap_in_seconds=force_max_gap_in_seconds.get(data_source) if force_max_gap_in_seconds is not None else None,
                                    convert_to_minutes_timeout=convert_to_minutes_timeout,
                                    start=start,
                                    end=end)
        self._runner = bdp_utils.run.Runner(name='Bdp' +
                                                 self._kabab_to_camel_case(data_source) +
                                                 'EnrichedToScoring',
                                            logger=logger,
                                            host=host,
                                            block=True)
        self._data_source = data_source
        self._host = host
        self._validation_timeout = validation_timeout
        self._validation_polling_interval = validation_polling_interval
        self._start = start
        self._end = end
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
                     'forwardingBatchSizeInMinutes = ' + str(self._throttler.get_max_batch_size_in_minutes()),
                     'maxSourceDestinationTimeGap = ' + str(self._throttler.get_max_gap_in_minutes() * 60),
                     'data_sources = ' + self._data_source
                 ])

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
