import argparse
import itertools
import pymongo
from datetime import datetime
from dateutil.parser import parse
from impala.dbapi import connect

DATA_SOURCE_TO_IMPALA_TABLE = {
    'kerberos_logins': 'authenticationscores',
    'kerberos_tgt': 'kerberostgtscore'
}
HOST = 'tc-agent7'
impala_connection = connect(host=HOST, port=21050)
mongo_db = pymongo.MongoClient(HOST, 27017).fortscale


def get_collection_names():
    if pymongo.version_tuple[0] > 2 or (pymongo.version_tuple[0] == 2 and pymongo.version_tuple[1] > 7):
        names = mongo_db.collection_names()
    else:
        names = [e['name'] for e in mongo_db.command('listCollections')['cursor']['firstBatch']]
    return names


def get_mongo_collection_feature_name(collection):
    feature_names = filter(lambda feature_name: feature_name.endswith('_histogram'), collection.find_one()['aggregatedFeatures'])
    if len(feature_names) > 0:
        return 'aggregatedFeatures.' + feature_names[0] + '.value.totalCount'
    return None


def get_sum_from_mongo(collection_name):
    collection = mongo_db[collection_name]
    feature_name = get_mongo_collection_feature_name(collection)
    if feature_name is None:
        raise Exception(collection_name + ' does not have a histogram feature')
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
                    '$sum': '$' + feature_name
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


def get_collection_name(context_type, data_source, is_daily):
    return 'aggr_%s_%s_%s' % (context_type, data_source, 'daily' if is_daily else 'hourly')


def get_impala_table_name(data_source):
    cursor = impala_connection.cursor()
    cursor.execute('show tables')
    available_table_names = [res[0] for res in cursor.fetchall()]
    table_name_options = [data_source + 'score', data_source + 'scores', data_source + 'datares']
    if DATA_SOURCE_TO_IMPALA_TABLE.has_key(data_source):
        table_name_options.append(DATA_SOURCE_TO_IMPALA_TABLE[data_source])
    for table_name in table_name_options:
        if table_name in available_table_names:
            return table_name
    return None


def get_sum_from_impala(data_source, start_time_partition, end_time_partition, is_daily):
    table_name = get_impala_table_name(data_source)
    if table_name is None:
        raise Exception("Data source " + data_source + " does not have a mapping to an impala table. " +
                        "Please update the script's source code and run again")
    time_resolution = 60 * 60 * 24 if is_daily else 60 * 60
    cursor = impala_connection.cursor()
    cursor.execute('select floor(date_time_unix / ' + str(time_resolution) + ') * ' + str(time_resolution) +
                   ' as time_bucket, count(*) from ' + table_name +
                   ' where yearmonthday >= ' + start_time_partition +
                   ' and yearmonthday < ' + end_time_partition +
                   ' group by time_bucket')
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
    parser.add_argument('--data_sources',
                        nargs='+',
                        action='store',
                        dest='data_sources',
                        help='The data sources to validate',
                        default=[])
    parser.add_argument('--context_types',
                        nargs='+',
                        action='store',
                        dest='context_types',
                        choices=['normalized_username', 'source_machine', 'destination_machine', 'city', 'country'],
                        help='The types of aggregations to validate',
                        default=[])

    return parser


def greenify(s):
    return '\033[92m' + s + '\033[0m'


def redify(s):
    return '\033[91m' + s + '\033[0m'


if __name__ == '__main__':
    parser = create_parser()
    arguments = parser.parse_args(['--start_date', '23 march 2016',
                                   '--end_date', '27 march 2016',
                                   '--data_sources', 'kerberos_logins', 'ssh',
                                   '--context_types', 'normalized_username'])

    start_date_date = parse(arguments.start_date)
    end_date_date = parse(arguments.end_date)
    start_time_epoch = (start_date_date - datetime.utcfromtimestamp(0)).total_seconds()
    end_time_epoch = (end_date_date - datetime.utcfromtimestamp(0)).total_seconds()
    start_time_partition = date_to_partition(start_date_date)
    end_time_partition = date_to_partition(end_date_date)
    for data_source, context_type in itertools.product(arguments.data_sources, arguments.context_types):
        validated_something = False
        for is_daily in [True, False]:
            collection_name = get_collection_name(context_type=context_type,
                                                  data_source=data_source,
                                                  is_daily=is_daily)
            if not collection_name in get_collection_names():
                continue

            print 'Validating ' + collection_name + '...'
            validated_something = True
            impala_sums = get_sum_from_impala(data_source=data_source,
                                              start_time_partition=start_time_partition,
                                              end_time_partition=end_time_partition,
                                              is_daily=is_daily)
            mongo_sums = get_sum_from_mongo(collection_name=collection_name)
            diff = dict_diff(impala_sums, mongo_sums)
            if len(diff) > 0:
                print redify('Failed')
                for time, (impala_sum, mongo_sum) in diff.iteritems():
                    print '\t' + time_to_str(time) + ': impala - ' + str(impala_sum) + ', mongo - ' + str(mongo_sum)
            else:
                print greenify('OK')
            print
        if not validated_something:
            raise Exception('Collections ' + collection_name[:collection_name.rindex('_')] + '_<daily/hourly>' +
                            ' do not exist. Please make sure the data_source and context_type'
                            ' you provided are valid and run again.')
