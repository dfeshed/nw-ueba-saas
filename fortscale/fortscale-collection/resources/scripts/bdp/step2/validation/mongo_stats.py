import os
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from automatic_config.common.utils import mongo


def _get_distinct_from_aggr_collections(host, field_name):
    db = mongo.get_db(host)
    res = set()
    for collection_name in mongo.get_collection_names(host=host, collection_names_regex='^aggr_'):
        a = db[collection_name].find_one()
        if a is not None:
            res.add(a[field_name][0])
    return res


def get_all_context_types(host):
    return _get_distinct_from_aggr_collections(host=host, field_name='contextFieldNames')


def _get_mongo_collection_feature_name(collection):
    feature_names = filter(lambda feature_name: feature_name.endswith('_histogram'), collection.find_one()['aggregatedFeatures'])
    if len(feature_names) > 0:
        return 'aggregatedFeatures.' + feature_names[0] + '.value.totalCount'
    return None


class MongoWarning(Warning):
    def __init__(self, message):
        super(MongoWarning, self).__init__(message)


def get_sum_from_mongo(host, collection_name, start_time_epoch, end_time_epoch):
    collection = mongo.get_db(host)[collection_name]
    if collection.find_one() is None:
        raise MongoWarning(collection_name + ' does not exist')
    feature_name = _get_mongo_collection_feature_name(collection)
    if feature_name is None:
        raise MongoWarning(collection_name + ' does not have any histogram feature')
    query_res = mongo.aggregate(collection, [
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
    ])
    return dict((entry['startTime'], int(entry['sum'])) for entry in query_res)


def all_buckets_synced(host, start_time_epoch, end_time_epoch, use_start_time):
    return mongo.get_db(host).FeatureBucketMetadata.find_one({
        'isSynced': False,
        'startTime' if use_start_time else 'endTime': {
            '$gte': start_time_epoch,
            '$lt': end_time_epoch
        }
    }) is None
