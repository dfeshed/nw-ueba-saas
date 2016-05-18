import os
import sys
import pymongo
import re

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from automatic_config.common.utils import mongo


def get_collection_names(host, collection_names_regex):
    mongo_db = mongo.get_db(host)
    return filter(lambda name: re.search(collection_names_regex, name) is not None,
                  mongo.get_all_collection_names(mongo_db))


def get_collections_time_boundary(host, collection_names_regex, is_start):
    time = sys.maxint if is_start else 0
    mongo_db = mongo.get_db(host)
    for collection_name in get_collection_names(host=host, collection_names_regex=collection_names_regex):
        collection = mongo_db[collection_name]
        field_name = 'startTime' if is_start else 'endTime'
        time = (min if is_start else max)(time, collection
                                          .find({}, [field_name])
                                          .sort(field_name, pymongo.ASCENDING if is_start else pymongo.DESCENDING)
                                          .limit(1)
                                          .next()[field_name])
    return time
