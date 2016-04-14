import pymongo


def _get_db(host):
    return pymongo.MongoClient(host, 27017).fortscale


def get_collection_data_source(host, collection_name):
    return _get_db(host)[collection_name].find_one()['dataSources'][0]


def _get_collection_context_type(mongo_db, collection_name):
    return mongo_db[collection_name].find_one()['contextFieldNames'][0]


def get_all_context_types(host):
    return set(_get_collection_context_type(mongo_db=_get_db(host),
                                            collection_name=collection_name)
               for collection_name in get_all_collection_names(host=host))


def get_all_collection_names(host):
    mongo_db = _get_db(host)
    if pymongo.version_tuple[0] > 2 or (pymongo.version_tuple[0] == 2 and pymongo.version_tuple[1] > 7):
        names = mongo_db.collection_names()
    else:
        names = [e['name'] for e in mongo_db.command('listCollections')['cursor']['firstBatch']]
    return filter(lambda name: name.startswith('aggr_') and
                               (name.endswith('_daily') or name.endswith('_hourly')), names)


def _get_mongo_collection_feature_name(collection):
    feature_names = filter(lambda feature_name: feature_name.endswith('_histogram'), collection.find_one()['aggregatedFeatures'])
    if len(feature_names) > 0:
        return 'aggregatedFeatures.' + feature_names[0] + '.value.totalCount'
    return None


def get_sum_from_mongo(host, collection_name, start_time_epoch, end_time_epoch):
    collection = _get_db(host)[collection_name]
    if collection.find_one() is None:
        raise Exception(collection_name + ' does not exist')
    feature_name = _get_mongo_collection_feature_name(collection)
    if feature_name is None:
        raise Exception(collection_name + ' does not have any histogram feature')
    query_res = (collection.aggregate([
        {
            '$match': {
                'endTime': {
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


def all_buckets_synced(host, start_time_epoch, end_time_epoch):
    return _get_db(host).FeatureBucketMetadata.find_one({
        'isSynced': False,
        'endTime': {
            '$gte': start_time_epoch,
            '$lt': end_time_epoch
        }
    }) is None
