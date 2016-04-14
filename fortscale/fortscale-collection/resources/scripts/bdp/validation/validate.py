import itertools
import sys

import impala_stats
import mongo_stats

sys.path.append(__file__ + r'\..\..\..')
from automatic_config.common.utils import time_utils


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


def _greenify(s):
    return '\033[92m' + s + '\033[0m'


def _redify(s):
    return '\033[91m' + s + '\033[0m'


def _yellowify(s):
    return '\033[93m' + s + '\033[0m'


def validate(start_time_epoch, end_time_epoch, data_sources, context_types, verbose=False):
    for data_source, context_type in itertools.product(data_sources, context_types):
        for is_daily in [True, False]:
            collection_name = _get_collection_name(context_type=context_type,
                                                   data_source=data_source,
                                                   is_daily=is_daily)
            if verbose:
                print 'Validating ' + collection_name + '...'
            try:
                mongo_sums = mongo_stats.get_sum_from_mongo(collection_name=collection_name,
                                                            start_time_epoch=start_time_epoch,
                                                            end_time_epoch=end_time_epoch)
            except Exception, e:
                if verbose:
                    print _yellowify(e.message)
                    print
                continue
            impala_sums = impala_stats.get_sum_from_impala(data_source=data_source,
                                                           start_time_partition=(time_utils.time_to_impala_partition(start_time_epoch)),
                                                           end_time_partition=(time_utils.time_to_impala_partition(end_time_epoch)),
                                                           is_daily=is_daily)
            diff = _calc_dict_diff(impala_sums, mongo_sums)
            if verbose:
                if len(diff) > 0:
                    print _redify('FAILED')
                    for time, (impala_sum, mongo_sum) in diff.iteritems():
                        print '\t' + time_utils.timestamp_to_str(time) + ': impala - ' + str(impala_sum) + ', mongo - ' + str(mongo_sum)
                else:
                    print _greenify('OK')
                print
            if len(diff) > 0:
                return False
    return True
