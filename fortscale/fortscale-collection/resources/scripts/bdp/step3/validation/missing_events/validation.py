import json
import time
import subprocess
import zipfile
import os
import sys
import itertools
from contextlib import contextmanager

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from mongo_stats import get_collections_size
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils.mongo import get_all_aggr_collection_names
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
    collection_names = get_all_aggr_collection_names(host=host)
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


@contextmanager
def metrics_reader(host):
    kafka_console_consumer_args = [
        'kafka-console-consumer',
        '--from-beginning',
        '--topic', 'metrics',
        '--zookeeper', host + ':2181'
    ]
    grep_args = [
        'grep',
        '-o',
        '-P', '\"(aggr-prevalence-processed-count|entity-events-streaming-received-message-count|event-scoring-persistency-message-count|aggr-prevalence-skip-count)\":(\d+)'
    ]
    logger.info('inspecting metrics: ' + ' '.join(kafka_console_consumer_args) + ' | ' + ' '.join(grep_args))
    kafka_p = subprocess.Popen(kafka_console_consumer_args, stdout=subprocess.PIPE)
    grep_p = subprocess.Popen(grep_args, stdin=kafka_p.stdout, stdout=subprocess.PIPE)
    yield itertools.imap(lambda l: (l[1:l.index('"', 1)], int(l[l.index(':') + 1:])), iter(grep_p.stdout.readline, ''))
    kafka_p.kill()
    grep_p.kill()


def validate_no_missing_events(host, timeout, start, end):
    logger.info('validating that there are no missing events...')
    num_of_fs_and_ps_to_be_processed = _get_num_of_fs_and_ps(host=host, start=start, end=end)
    last_progress_time = time.time()
    metrics = {}
    with metrics_reader(host) as m:
        for metric_type, count in m:
            if count > metrics.get(metric_type, 0):
                last_progress_time = time.time()
                metrics[metric_type] = count
                logger.info('metrics have progressed:', metrics)
            if metrics.get('aggr-prevalence-skip-count', 0) == 0 and \
                            metrics.get('aggr-prevalence-processed-count', 0) == metrics.get('event-scoring-persistency-message-count', 0) and \
                            metrics.get('entity-events-streaming-received-message-count', 0) == num_of_fs_and_ps_to_be_processed:
                logger.info('OK')
                return True
            if time.time() - last_progress_time >= timeout:
                logger.error('FAILED')
                return False
