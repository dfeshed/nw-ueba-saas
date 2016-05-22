import json
import time
import zipfile
import os
import sys
from contextlib import contextmanager

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils.mongo import get_collection_names, get_collections_size
from bdp_utils.metrics import metrics_reader
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..']))
from automatic_config.common.utils import time_utils

import logging

logger = logging.getLogger('step3.validation')


@contextmanager
def open_aggregated_feature_events():
    overriding_filename = '/home/cloudera/fortscale/config/asl/entity_events/overriding/aggregated_feature_events.json'
    if os.path.isfile(overriding_filename):
        f = open(overriding_filename, 'r')
        yield f
        f.close()
    else:
        zf = zipfile.ZipFile('/home/cloudera/fortscale/streaming/lib/fortscale-aggregation-1.1.0-SNAPSHOT.jar', 'r')
        f = zf.open('config/asl/aggregated_feature_events.json', 'r')
        yield f
        f.close()
        zf.close()


def _get_num_of_fs_and_ps(host, start, end):
    collection_names = get_collection_names(host=host, collection_names_regex='^aggr_')
    with open_aggregated_feature_events() as f:
        aggr_asl = json.load(f)
    res = 0
    logger.info('calculating number of Fs and Ps produced by each bucket...')
    for collection_name in collection_names:
        bucket_conf_name = collection_name.replace('aggr_', '')
        features_in_bucket = len(filter(lambda aggr: aggr['bucketConfName'] == bucket_conf_name,
                                        aggr_asl['AggregatedFeatureEvents']))
        logger.info(features_in_bucket, ' features in', bucket_conf_name, 'bucket')
        res += features_in_bucket * get_collections_size(host=host,
                                                         collection_names_regex='^' + collection_name + '$',
                                                         find_query={
                                                             'endTime': {
                                                                 '$gte': time_utils.get_epoch(start),
                                                                 '$lt': time_utils.get_epoch(end)
                                                             }
                                                         })
    logger.info('done - in total there are', res, 'Fs and Ps')
    return res


def validate_no_missing_events(host, timeout, start, end):
    logger.info('validating that there are no missing events...')
    num_of_fs_and_ps_to_be_processed = _get_num_of_fs_and_ps(host=host, start=start, end=end)
    last_progress_time = time.time()
    metrics = {}
    metric_aggr_prevalence_processed_count = 'aggr-prevalence-processed-count'
    metric_entity_events_streaming_received_message_count = 'entity-events-streaming-received-message-count'
    metric_event_scoring_persistency_message_count = 'event-scoring-persistency-message-count'
    metric_aggr_prevalence_skip_count = 'aggr-prevalence-skip-count'
    with metrics_reader(logger,
                        host,
                        metric_aggr_prevalence_processed_count,
                        metric_entity_events_streaming_received_message_count,
                        metric_event_scoring_persistency_message_count,
                        metric_aggr_prevalence_skip_count) as m:
        for metric_type, count in m:
            if count > metrics.get(metric_type, 0):
                last_progress_time = time.time()
                metrics[metric_type] = count
                logger.info('metrics have progressed:', metrics)
            if metrics.get(metric_aggr_prevalence_skip_count, 0) == 0 and \
                            metrics.get(metric_aggr_prevalence_processed_count, 0) == metrics.get(metric_event_scoring_persistency_message_count, 0) and \
                            metrics.get(metric_entity_events_streaming_received_message_count, 0) == num_of_fs_and_ps_to_be_processed:
                logger.info('OK')
                return True
            if time.time() - last_progress_time >= timeout:
                logger.error('FAILED')
                return False
