import re
import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from automatic_config.common.utils import mongo


def get_collections_size(host, collection_names_regex, find_query={}):
    mongo_db = mongo.get_db(host)
    collection_names = filter(lambda collection_name: re.sesarch(collection_names_regex, collection_name) is not None,
                              mongo.get_all_collection_names(mongo_db))
    return sum(mongo_db[collection_name].find(find_query).count() for collection_name in collection_names)
