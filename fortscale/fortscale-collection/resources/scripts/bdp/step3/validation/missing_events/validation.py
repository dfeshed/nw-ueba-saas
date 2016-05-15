import json
import time
import subprocess
import os
import sys
import itertools
from contextlib import contextmanager
from mongo_stats import count_aggregated_collection

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils.mongo import get_all_aggr_collection_names
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..']))
from automatic_config.common.utils import time_utils

import logging

logger = logging.getLogger('step3.validation')


def _get_num_of_fs_and_ps(host, start, end):
    collection_names = get_all_aggr_collection_names(host=host)
    with open(os.path.sep.join([os.path.dirname(os.path.abspath(__file__))[:os.path.dirname(os.path.abspath(__file__)).index('fortscale-collection')],
                                'fortscale-aggregation', 'src', 'main', 'resources', 'config', 'asl',
                                'aggregated_feature_events.json']), 'r') as f:
        aggr_asl = json.load(f)
    res = 0
    for collection_name in collection_names:
        features_in_bucket = len(filter(lambda aggr: aggr['bucketConfName'] == collection_name.replace('aggr_', ''),
                                        aggr_asl['AggregatedFeatureEvents']))
        res += features_in_bucket * count_aggregated_collection(host=host,
                                                                collection_name=collection_name,
                                                                start_time_epoch=time_utils.get_epoch(start),
                                                                end_time_epoch=time_utils.get_epoch(end))
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
    logger.info('waiting for metrics: ' + ' '.join(kafka_console_consumer_args) + ' | ' + ' '.join(grep_args))
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
            if metrics.get('aggr-prevalence-processed-count', 0) == metrics.get('event-scoring-persistency-message-count', 0) and \
                            metrics.get('entity-events-streaming-received-message-count', 0) == num_of_fs_and_ps_to_be_processed:
                logger.info('OK')
                return True
            if time.time() - last_progress_time >= timeout:
                logger.error('FAILED')
                return False
