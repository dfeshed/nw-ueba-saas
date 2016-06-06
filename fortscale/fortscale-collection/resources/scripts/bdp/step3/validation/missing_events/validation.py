import json
import time
import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils.mongo import get_collection_names, get_collections_size
from bdp_utils.kafka import read_metrics
from bdp_utils import overrides
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..']))
from automatic_config.common.utils import time_utils

import logging

logger = logging.getLogger('step3.validation')


def _get_num_of_fs_and_ps(host, start, end):
    collection_names = get_collection_names(host=host, collection_names_regex='^aggr_')
    with overrides.open_overrides_file(overriding_path='/home/cloudera/fortscale/config/asl/aggregation_events/overriding/aggregated_feature_events.json',
                                       jar_name='fortscale-aggregation-1.1.0-SNAPSHOT.jar',
                                       path_in_jar='config/asl/aggregated_feature_events.json') as f:
        aggr_asl = json.load(f)
    res = 0
    logger.info('calculating number of Fs and Ps produced by each bucket...')
    for collection_name in collection_names:
        bucket_conf_name = collection_name.replace('aggr_', '')
        features_in_bucket = len(filter(lambda aggr: aggr['bucketConfName'] == bucket_conf_name,
                                        aggr_asl['AggregatedFeatureEvents']))
        logger.info(str(features_in_bucket) + ' features in ' + str(bucket_conf_name) + ' bucket')
        res += features_in_bucket * get_collections_size(host=host,
                                                         collection_names_regex='^' + collection_name + '$',
                                                         find_query={
                                                             'endTime': {
                                                                 '$gte': time_utils.get_epochtime(start),
                                                                 '$lt': time_utils.get_epochtime(end)
                                                             }
                                                         })
    logger.info('done - in total there are ' + str(res) + ' Fs and Ps')
    return res


def validate_no_missing_events(host, timeout, start, end):
    logger.info('validating that there are no missing events...')
    num_of_fs_and_ps_to_be_processed = _get_num_of_fs_and_ps(host=host, start=start, end=end)
    last_progress_time = time.time()
    metric_to_count = {}
    metric_aggr_prevalence_processed_count = 'aggr-prevalence-processed-count'
    metric_entity_events_streaming_received_message_count = 'entity-events-streaming-received-message-count'
    metric_event_scoring_persistency_message_count = 'event-scoring-persistency-message-count'
    metric_aggr_prevalence_skip_count = 'aggr-prevalence-skip-count'
    with read_metrics(logger,
                      host,
                      metric_aggr_prevalence_processed_count,
                      metric_entity_events_streaming_received_message_count,
                      metric_event_scoring_persistency_message_count,
                      metric_aggr_prevalence_skip_count) as m:
        for metric, count in m:
            if count > metric_to_count.get(metric, 0):
                last_progress_time = time.time()
                metric_to_count[metric] = count
                logger.info('metrics have progressed: ' + str(metric_to_count))
            if metric_to_count.get(metric_aggr_prevalence_skip_count, 0) == 0 and \
                            metric_to_count.get(metric_aggr_prevalence_processed_count, 0) == metric_to_count.get(metric_event_scoring_persistency_message_count, 0) and \
                            metric_to_count.get(metric_entity_events_streaming_received_message_count, 0) == num_of_fs_and_ps_to_be_processed:
                logger.info('OK')
                return True
            if time.time() - last_progress_time >= timeout:
                logger.error('FAILED')
                return False
