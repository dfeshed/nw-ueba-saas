import os
import sys
import pymongo

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils.mongo import get_all_aggr_collection_names

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from automatic_config.common.utils import mongo


def get_aggr_collections_boundary(host, is_start):
    time = sys.maxint if is_start else 0
    for collection_name in get_all_aggr_collection_names(host=host):
        collection = mongo.get_db(host)[collection_name]
        field_name = 'startTime' if is_start else 'endTime'
        time = (min if is_start else max)(time, collection
                                          .find({}, [field_name])
                                          .sort(field_name, pymongo.ASCENDING if is_start else pymongo.DESCENDING)
                                          .limit(1)
                                          .next()[field_name])
    return time
