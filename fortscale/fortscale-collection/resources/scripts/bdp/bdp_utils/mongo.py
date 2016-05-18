import os
import sys
import pymongo

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from automatic_config.common.utils import mongo


def get_all_aggr_collection_names(host):
    return filter(lambda name: name.startswith('aggr_') and (name.endswith('_daily') or name.endswith('_hourly')),
                  mongo.get_all_collection_names(mongo.get_db(host)))


def get_collections_time_boundary(host, collection_names, is_start):
    time = sys.maxint if is_start else 0
    for collection_name in collection_names:
        collection = mongo.get_db(host)[collection_name]
        field_name = 'startTime' if is_start else 'endTime'
        time = (min if is_start else max)(time, collection
                                          .find({}, [field_name])
                                          .sort(field_name, pymongo.ASCENDING if is_start else pymongo.DESCENDING)
                                          .limit(1)
                                          .next()[field_name])
    return time
