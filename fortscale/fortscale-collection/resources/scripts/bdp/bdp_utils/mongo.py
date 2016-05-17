import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from automatic_config.common.utils import mongo


def get_all_aggr_collection_names(host):
    return filter(lambda name: name.startswith('aggr_') and (name.endswith('_daily') or name.endswith('_hourly')),
                  mongo.get_all_collection_names(mongo.get_db(host)))
