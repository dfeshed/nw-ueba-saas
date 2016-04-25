import itertools
import os
import sys

import impala_stats
import mongo_stats

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from automatic_config.common.utils import time_utils

import logging
logger = logging.getLogger('validation')


def _get_collection_name(context_type, data_source, is_daily):
    return 'aggr_%s_%s_%s' % (context_type, data_source, 'daily' if is_daily else 'hourly')


def _calc_dict_diff(first, second):
    diff = {}
    for key in first.iterkeys():
        if (not second.has_key(key)):
            diff[key] = (first[key], 0)
        elif (first[key] != second[key]):
            diff[key] = (first[key], second[key])
    for key in second.iterkeys():
        if (not first.has_key(key)):
            diff[key] = (0, second[key])
    return diff


def validate_all_buckets_synced(host, start_time_epoch, end_time_epoch, use_start_time=False):
    logger.info('validating that all buckets inside FeatureBucketMetadata have been synced...')
    is_synced = mongo_stats.all_buckets_synced(host=host,
                                               start_time_epoch=start_time_epoch,
                                               end_time_epoch=end_time_epoch,
                                               use_start_time=use_start_time)
    logger.info('validation ' + ('succeeded' if is_synced else 'failed'))
    return is_synced


def validate_no_missing_events(host, start_time_epoch, end_time_epoch, data_sources, context_types):
    logger.info('validating that there are no missing events...\n')
    if start_time_epoch % 60*60 != 0 or end_time_epoch % 60*60 != 0:
        raise Exception('start time and end time must be rounded hour')

    if data_sources is None:
        data_sources = [mongo_stats.get_collection_data_source(host=host, collection_name=collection_name)
                        for collection_name in mongo_stats.get_all_aggr_collection_names(host=host)]
    if context_types is None:
        context_types = mongo_stats.get_all_context_types(host=host)

    success = True
    for data_source, context_type in itertools.product(data_sources, context_types):
        for is_daily in [True, False]:
            collection_name = _get_collection_name(context_type=context_type,
                                                   data_source=data_source,
                                                   is_daily=is_daily)
            logger.info('validating ' + collection_name + '...')
            try:
                mongo_sums = mongo_stats.get_sum_from_mongo(host=host,
                                                            collection_name=collection_name,
                                                            start_time_epoch=start_time_epoch,
                                                            end_time_epoch=end_time_epoch)
            except mongo_stats.MongoWarning, e:
                logger.warning(e)
                logger.warning('')
                continue
            impala_sums = impala_stats.get_sum_from_impala(host=host,
                                                           data_source=data_source,
                                                           start_time_epoch=start_time_epoch,
                                                           end_time_epoch=end_time_epoch,
                                                           is_daily=is_daily)
            diff = _calc_dict_diff(impala_sums, mongo_sums)
            if len(diff) > 0:
                logger.error('FAILED')
                for time, (impala_sum, mongo_sum) in diff.iteritems():
                    logger.error('\t' + time_utils.timestamp_to_str(time) +
                                 ': impala - ' + str(impala_sum) + ', mongo - ' + str(mongo_sum))
            else:
                logger.info('OK')
            logger.info('')
            if len(diff) > 0:
                success = False
    if success:
        logger.info('validation succeeded')
    else:
        logger.error('validation failed')
    return success
