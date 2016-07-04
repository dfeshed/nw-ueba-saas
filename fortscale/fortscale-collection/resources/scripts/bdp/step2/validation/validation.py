import itertools
import time
import os
import sys

import impala_stats
import mongo_stats

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from automatic_config.common.utils import time_utils
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils.run import validate_by_polling
from bdp_utils.log import log_and_send_mail


import logging
logger = logging.getLogger('step2.validation')


def _get_collection_name(context_type, data_source, is_daily):
    return 'aggr_%s_%s_%s' % (context_type, data_source if data_source != 'kerberos' else 'kerberos_logins', 'daily' if is_daily else 'hourly')


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


def validate_no_missing_events(host,
                               start_time_epoch,
                               end_time_epoch,
                               data_sources,
                               context_types,
                               timeout,
                               polling_interval):
    logger.info('validating that there are no missing events...\n')
    if start_time_epoch % (60*60) != 0 or end_time_epoch % (60*60) != 0:
        raise Exception('start time and end time must be rounded hour')

    if context_types is None:
        context_types = mongo_stats.get_all_context_types(host=host)

    success = True
    for data_source, context_type in itertools.product(data_sources, context_types):
        for is_daily in [True, False]:
            logger.info('validating ' + _get_collection_name(context_type=context_type,
                                                             data_source=data_source,
                                                             is_daily=is_daily) + '...')
            success &= _validate_no_missing_events(host=host,
                                                   start_time_epoch=start_time_epoch,
                                                   end_time_epoch=end_time_epoch,
                                                   data_source=data_source,
                                                   is_daily=is_daily,
                                                   context_type=context_type,
                                                   timeout=timeout,
                                                   polling_interval=polling_interval)
    if success:
        logger.info('validation finished successfully')
    else:
        logger.error('validation failed')
    return success


def _validate_no_missing_events(host,
                                start_time_epoch,
                                end_time_epoch,
                                data_source,
                                is_daily,
                                context_type,
                                timeout,
                                polling_interval):
    return validate_by_polling(logger=logger,
                               progress_cb=lambda: _get_num_of_events_per_day(host=host,
                                                                              start_time_epoch=start_time_epoch,
                                                                              end_time_epoch=end_time_epoch,
                                                                              data_source=data_source,
                                                                              is_daily=is_daily,
                                                                              context_type=context_type),
                               is_done_cb=_are_histograms_equal,
                               no_progress_timeout=timeout,
                               polling=polling_interval)


def _get_num_of_events_per_day(host,
                               start_time_epoch,
                               end_time_epoch,
                               data_source,
                               is_daily,
                               context_type):
    collection_name = _get_collection_name(context_type=context_type,
                                           data_source=data_source,
                                           is_daily=is_daily)
    try:
        mongo_sums = mongo_stats.get_sum_from_mongo(host=host,
                                                    collection_name=collection_name,
                                                    start_time_epoch=start_time_epoch,
                                                    end_time_epoch=end_time_epoch)
    except mongo_stats.MongoWarning, e:
        logger.warning(e)
        logger.warning('')
        return {}
    impala_sums = impala_stats.get_sum_from_impala(host=host,
                                                   data_source=data_source,
                                                   start_time_epoch=start_time_epoch,
                                                   end_time_epoch=end_time_epoch,
                                                   is_daily=is_daily)
    return _calc_dict_diff(impala_sums, mongo_sums)


def _are_histograms_equal(histograms_diff):
    if len(histograms_diff) > 0:
        logger.error('FAILED')
        for time, (impala_sum, mongo_sum) in histograms_diff.iteritems():
            logger.error('\t' + time_utils.timestamp_to_str(time) +
                         ': impala - ' + str(impala_sum) + ', mongo - ' + str(mongo_sum))
        return False
    else:
        logger.info('OK')
        return True


def block_until_everything_is_validated(host,
                                        start_time_epoch,
                                        end_time_epoch,
                                        wait_between_validations,
                                        max_delay,
                                        timeout,
                                        polling_interval,
                                        data_sources=None,
                                        logger=logger):
    last_validation_time = time.time()
    is_valid = False
    while not is_valid:
        is_valid = _validate_everything(host=host,
                                        start_time_epoch=start_time_epoch,
                                        end_time_epoch=end_time_epoch,
                                        timeout=timeout,
                                        polling_interval=polling_interval,
                                        data_sources=data_sources,
                                        logger=logger)
        if not is_valid:
            if 0 <= max_delay < time.time() - last_validation_time:
                log_and_send_mail('validation failed for more than ' + str(int(max_delay / (60 * 60))) + ' hours')
            logger.info('not valid yet - going to sleep for ' +
                        str(int(wait_between_validations / 60)) + ' minutes')
            time.sleep(wait_between_validations)
    return is_valid


def _validate_everything(host, start_time_epoch, end_time_epoch, timeout, polling_interval, data_sources, logger):
    logger.info('validating ' + time_utils.interval_to_str(start_time_epoch, end_time_epoch) + '...')
    is_valid = validate_all_buckets_synced(host=host,
                                           start_time_epoch=start_time_epoch,
                                           end_time_epoch=end_time_epoch)
    if is_valid:
        validate_no_missing_events(host=host,
                                   start_time_epoch=start_time_epoch,
                                   end_time_epoch=end_time_epoch,
                                   data_sources=data_sources,
                                   context_types=['normalized_username'],
                                   timeout=timeout,
                                   polling_interval=polling_interval)
    return is_valid
