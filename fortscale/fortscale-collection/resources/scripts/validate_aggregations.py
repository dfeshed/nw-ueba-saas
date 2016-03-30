import argparse
import pymongo
from datetime import datetime
from dateutil.parser import parse
from impala.dbapi import connect

DATA_SOURCE_TO_IMPALA_TABLE = {
    'kerberos_logins': 'authenticationscores'
}
HOST = 'tc-agent7'


def get_sum_from_mongo(context_type, data_source, feature, is_daily):
    collection = pymongo.MongoClient(HOST, 27017).fortscale['aggr_%s_%s_%s' % (context_type,
                                                                               data_source,
                                                                               'daily' if is_daily else 'hourly')]
    query_res = (collection.aggregate([
        {
            '$match': {
                'startTime': {
                    '$gte': start_time_epoch,
                    '$lt': end_time_epoch
                }
            }
        },
        {
            '$group': {
                '_id': '$startTime',
                'sum': {
                    '$sum': '$aggregatedFeatures.' + feature + '_histogram.value.totalCount'
                }
            }
        },
        {
            '$project': {
                'sum': 1,
                'startTime': '$_id'
            }
        }
    ]))
    return dict((entry['startTime'], int(entry['sum'])) for entry in query_res)


def get_sum_from_impala(data_source, start_time_partition, end_time_partition, is_daily):
    time_resolution = 60 * 60 * 24 if is_daily else 60 * 60
    conn = connect(host=HOST, port=21050)
    cursor = conn.cursor()
    cursor.execute('select floor(date_time_unix / ' + str(time_resolution) + ') * ' + str(time_resolution) +
                   ' as time_bucket, count(*) from ' + DATA_SOURCE_TO_IMPALA_TABLE[data_source] +
                   ' where yearmonthday >= ' + start_time_partition +
                   ' and yearmonthday < ' + end_time_partition +
                   ' group by time_bucket' )
    return dict(cursor)


def dict_diff(first, second):
    diff = {}
    for key in first.keys():
        if (not second.has_key(key)):
            diff[key] = (first[key], 0)
        elif (first[key] != second[key]):
            diff[key] = (first[key], second[key])
    for key in second.keys():
        if (not first.has_key(key)):
            diff[key] = (0, second[key])
    return diff


def time_to_str(time):
    return str(datetime.fromtimestamp(time))


def date_to_partition(date):
    return ''.join([str(date.year), '%02d' % date.month, '%02d' % date.day])


def create_parser():
    parser = argparse.ArgumentParser()
    parser.add_argument('--start_date',
                        action='store',
                        dest='start_date',
                        help='The start date from which to make the validation',
                        required=True)
    parser.add_argument('--end_date',
                        action='store',
                        dest='end_date',
                        help='The end date from which to make the validation',
                        required=True)
    parser.add_argument('--data_source',
                        action='store',
                        dest='data_source',
                        help='The data source to validate',
                        required=True)
    parser.add_argument('--context_type',
                        action='store',
                        dest='context_type',
                        choices=['normalized_username', 'source_machine', 'destination_machine'],
                        help='The type of aggregation to validate',
                        required=True)
    parser.add_argument('--feature',
                        action='store',
                        dest='feature',
                        help="The aggregation's feature to validate",
                        required=True)

    return parser


if __name__ == '__main__':
    parser = create_parser()
    arguments = parser.parse_args(['--start_date', '23 march 2016',
                                   '--end_date', '27 march 2016',
                                   '--data_source', 'kerberos_logins',
                                   '--context_type', 'normalized_username',
                                   '--feature', 'source_machine'])

    start_date_date = parse(arguments.start_date)
    end_date_date = parse(arguments.end_date)
    start_time_epoch = (start_date_date - datetime.utcfromtimestamp(0)).total_seconds()
    end_time_epoch = (end_date_date - datetime.utcfromtimestamp(0)).total_seconds()
    start_time_partition = date_to_partition(start_date_date)
    end_time_partition = date_to_partition(end_date_date)
    failed = False
    for is_daily in [True, False]:
        impala_sums = get_sum_from_impala(data_source=arguments.data_source,
                                          start_time_partition=start_time_partition,
                                          end_time_partition=end_time_partition,
                                          is_daily=is_daily)
        mongo_sums = get_sum_from_mongo(context_type=arguments.context_type,
                                        data_source=arguments.data_source,
                                        feature=arguments.feature,
                                        is_daily=is_daily)
        diff = dict_diff(impala_sums, mongo_sums)
        if len(diff) > 0:
            failed = True
            print 'validation failed (' + ('daily' if is_daily else 'hourly') + '):'
            for time, (impala_sum, mongo_sum) in diff.iteritems():
                print '\t' + time_to_str(time) + ': impala - ' + str(impala_sum) + ', mongo - ' + str(mongo_sum)
    if not failed:
        print 'everything is ok :)'
