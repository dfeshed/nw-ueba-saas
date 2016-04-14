import pymongo

from config import HOST

mongo_db = pymongo.MongoClient(HOST, 27017).fortscale


def get_collection_data_source(collection_name):
    return mongo_db[collection_name].find_one()['dataSources'][0]


def get_collection_context_type(collection_name):
    return mongo_db[collection_name].find_one()['contextFieldNames'][0]


def get_all_context_types():
    return set(get_collection_context_type(collection_name)
               for collection_name in get_all_collection_names())


def get_all_collection_names():
    if pymongo.version_tuple[0] > 2 or (pymongo.version_tuple[0] == 2 and pymongo.version_tuple[1] > 7):
        names = mongo_db.collection_names()
    else:
        names = [e['name'] for e in mongo_db.command('listCollections')['cursor']['firstBatch']]
    return filter(lambda name: name.startswith('aggr_') and
                               (name.endswith('_daily') or name.endswith('_hourly')), names)


def get_mongo_collection_feature_name(collection):
    feature_names = filter(lambda feature_name: feature_name.endswith('_histogram'), collection.find_one()['aggregatedFeatures'])
    if len(feature_names) > 0:
        return 'aggregatedFeatures.' + feature_names[0] + '.value.totalCount'
    return None


def get_sum_from_mongo(collection_name, start_time_epoch, end_time_epoch):
    collection = mongo_db[collection_name]
    if collection.find_one() is None:
        raise Exception(collection_name + ' does not exist')
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
