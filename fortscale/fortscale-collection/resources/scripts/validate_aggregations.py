import argparse
import pymongo
from datetime import datetime
from dateutil.parser import parse
from impala.dbapi import connect

DATA_SOURCE_TO_IMPALA_TABLE = {
    'kerberos_logins': 'authenticationscores',
    'kerberos_tgt': 'kerberostgtscore',
    'ssh': 'sshscores',
    'vpn': 'vpndatares',
    'vpnsession': 'vpnsessiondatares',
    'crmsf': 'crmsfscore',
    'oracle': 'oraclescore',
    'prnlog': 'prnlogscore',
    'gwame': 'gwamescore',
    'wame': 'wamescore',
    'ntlm': 'ntlmscore'
}
HOST = 'tc-agent7'


def get_collection_names():
    db = pymongo.MongoClient(HOST, 27017).fortscale
    if pymongo.version_tuple[0] > 2 or (pymongo.version_tuple[0] == 2 and pymongo.version_tuple[1] > 7):
        names = db.collection_names()
    else:
        names = [e['name'] for e in db.command('listCollections')['cursor']['firstBatch']]
    return names


def get_sum_from_mongo(collection_name, feature):
    if not feature.endswith('_histogram'):
        feature += '_histogram'
    collection = pymongo.MongoClient(HOST, 27017).fortscale[collection_name]
    complete_feature_name = 'aggregatedFeatures.' + feature + '.value.totalCount'
    if len(collection.find_one({}, [complete_feature_name])['aggregatedFeatures']) == 0:
        raise Exception('Collection does not contain the provided feature')
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
                    '$sum': '$' + complete_feature_name
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


def get_sum_from_impala(data_source, start_time_partition, end_time_partition, is_daily):
    time_resolution = 60 * 60 * 24 if is_daily else 60 * 60
    conn = connect(host=HOST, port=21050)
    cursor = conn.cursor()
    if not DATA_SOURCE_TO_IMPALA_TABLE.has_key(data_source):
        raise Exception("Data source does not have a mapping to an impala table. " +
                        "Please update the script's source code and run again")
    table_name = DATA_SOURCE_TO_IMPALA_TABLE[data_source]
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
    parser.add_argument('--data_source',
                        action='store',
                        dest='data_source',
                        help='The data source to validate',
                        required=True)
    parser.add_argument('--context_type',
                        action='store',
                        dest='context_type',
                        choices=['normalized_username', 'source_machine', 'destination_machine', 'city', 'country'],
                        help='The type of aggregation to validate',
                        required=True)
    parser.add_argument('--feature',
                        action='store',
                        dest='feature',
                        help="The aggregation's feature to validate. Only histogram features are supported",
                        required=True)

    return parser


if __name__ == '__main__':
    parser = create_parser()
    arguments = parser.parse_args(['--start_date', '23 march 2016',
                                   '--end_date', '27 march 2016',
                                   '--data_source', 'kerberos_logins',
                                   '--context_type', 'normalized_username',
                                   '--feature', 'source_machine_histogram'])

    start_date_date = parse(arguments.start_date)
    end_date_date = parse(arguments.end_date)
    start_time_epoch = (start_date_date - datetime.utcfromtimestamp(0)).total_seconds()
    end_time_epoch = (end_date_date - datetime.utcfromtimestamp(0)).total_seconds()
    start_time_partition = date_to_partition(start_date_date)
    end_time_partition = date_to_partition(end_date_date)
    validation_succeeded = None
    for is_daily in [True, False]:
        collection_name = get_collection_name(context_type=arguments.context_type,
                                              data_source=arguments.data_source,
                                              is_daily=is_daily)
        if not collection_name in get_collection_names():
            continue

        impala_sums = get_sum_from_impala(data_source=arguments.data_source,
                                          start_time_partition=start_time_partition,
                                          end_time_partition=end_time_partition,
                                          is_daily=is_daily)
        mongo_sums = get_sum_from_mongo(collection_name=collection_name,
                                        feature=arguments.feature)
        diff = dict_diff(impala_sums, mongo_sums)
        if len(diff) > 0:
            validation_succeeded = False
            print 'validation failed (' + ('daily' if is_daily else 'hourly') + '):'
            for time, (impala_sum, mongo_sum) in diff.iteritems():
                print '\t' + time_to_str(time) + ': impala - ' + str(impala_sum) + ', mongo - ' + str(mongo_sum)
        elif validation_succeeded is None:
            validation_succeeded = True
    if validation_succeeded is None:
        raise Exception('Collections ' + collection_name[:collection_name.rindex('_')] + '_<daily/hourly>' +
                        ' do not exist. Please make sure the data_source and context_type'
                        ' you provided are valid and run again.')
    if validation_succeeded:
        print 'everything is ok :)'
