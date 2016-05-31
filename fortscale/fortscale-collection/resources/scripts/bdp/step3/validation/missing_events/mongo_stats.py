import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..']))
from automatic_config.common.utils import mongo


def count_aggregated_collection(host, collection_name, start_time_epoch, end_time_epoch):
    collection = mongo.get_db(host)[collection_name]
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
                '_id': None,
                'sum': {
                    '$sum': 1
                }
            }
        }
    ])
    return query_res[0]['sum']
