import os
import sys

import time

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..']))
from bdp_utils.data_sources import data_source_to_enriched_tables
from bdp_utils.kafka import read_metrics
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..', '..']))
from automatic_config.common.utils import impala_utils, time_utils

import logging

logger = logging.getLogger('stepSAM.validation')


def validate_started_processing_everything(host,
                                           data_source,
                                           end_time_epoch,
                                           filtered_gap_in_minutes,
                                           filtered_timeout_in_minutes):
    connection = impala_utils.connect(host=host)
    logger.info('validating that all events' +
                ((' up until ' + time_utils.timestamp_to_str(end_time_epoch)) if end_time_epoch else '') +
                ' of ' + data_source + ' have been sent to processing...')
    last_event_time = impala_utils.get_last_event_time(connection=connection,
                                                       table=data_source_to_enriched_tables[data_source],
                                                       limit_end=end_time_epoch)
    logger.info('last enriched event to be processed occurred at ' + str(last_event_time))
    if last_event_time is None:
        logger.info('there are no enriched events in this data source')
        return True

    time_entered_filtered_gap = None
    with read_metrics(logger,
                      host,
                      'aggregation-events-streaming-last-message-epochtime') as m:
        for metric, processed_event_time in m:
            logger.info('the event at ' + str(processed_event_time) + ' has been processed')
            if processed_event_time == last_event_time:
                logger.info('DONE')
                return True
            elif processed_event_time > last_event_time:
                logger.error("FAILED: "
                             "validation can't be performed on data source whose enriched table is being filled")
                return False
            elif processed_event_time >= last_event_time - filtered_gap_in_minutes * 60:
                if time_entered_filtered_gap is None:
                    time_entered_filtered_gap = time.time()
                if time.time() - time_entered_filtered_gap >= filtered_timeout_in_minutes * 60:
                    logger.info('some of the last events were not processed - maybe due to filtering')
                    logger.info('DONE')
                    return True
