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


def iter_collections(host, collection_names_regex):
    mongo_db = mongo.get_db(host)
    return (mongo_db[collection_name]
            for collection_name in get_collection_names(host=host, collection_names_regex=collection_names_regex))


def get_collections_time_boundary(host, collection_names_regex, is_start):
    time = sys.maxint if is_start else 0
    mongo_db = mongo.get_db(host)
    for collection_name in get_collection_names(host=host, collection_names_regex=collection_names_regex):
        collection = mongo_db[collection_name]
        sample = collection.find_one()
        field_name_options = ['startTime', 'start_time_unix'] if is_start else ['endTime', 'end_time_unix']
        for field_name in field_name_options:
            if field_name in sample:
                break
        else:
            raise Exception('collection ' + collection_name + ' does not have any of these fields: ' +
                            ', '.join(field_name_options) +
                            '. If it has some other field that should be used - please update this script.')
        time = (min if is_start else max)(time, collection
                                          .find({}, [field_name])
                                          .sort(field_name, pymongo.ASCENDING if is_start else pymongo.DESCENDING)
                                          .limit(1)
                                          .next()[field_name])
    return time


def get_collections_size(host, collection_names_regex, find_query={}):
    mongo_db = mongo.get_db(host)
    return sum(mongo_db[collection_name].find(find_query).count()
               for collection_name in get_collection_names(host=host, collection_names_regex=collection_names_regex))
