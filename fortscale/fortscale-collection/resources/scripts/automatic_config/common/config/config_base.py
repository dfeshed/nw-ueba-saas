# This file contains all the configuration properties.

order = 1

import sys
import os

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from utils.mongo import get_collections_time_boundary


#mongo_ip = 'tc-agent7'
mongo_ip = '192.168.45.44'
aggregated_feature_event_prevalance_stats_path = r'C:\Users\yoelz\projects\fortscale-core\fortscale\fortscale-streaming\config\aggregated-feature_event-prevalance-stats.properties'
entity_events_path = {
    'overriding_path': r'C:\Users\yoelz\projects\fortscale-core\fortscale\fortscale-aggregation\src\main\resources\config\asl\entity_events.json',
    'jar_name': 'fortscale-aggregation-1.1.0-SNAPSHOT.jar',
    'path_in_jar': 'config/asl/entity_events.json'
}
interim_results_path = r'C:\Users\yoelz\projects\data\automatic_config\interim results'
START_TIME = None
END_TIME = None
NUM_OF_ALERTS_PER_DAY = 10
verbose = True
FIXED_W_DAILY = {
    'F': {
    },
    'P': {
    }
}
FIXED_W_HOURLY = {
    'F': {
    },
    'P': {
    }
}

BASE_ALPHA = 0.1
BASE_BETA = 0.001

dry = False

show_graphs = False


def get_start_time():
    return START_TIME or get_collections_time_boundary(host=mongo_ip,
                                                       collection_names_regex='^aggr_',
                                                       is_start=True)


def get_end_time():
    return END_TIME or get_collections_time_boundary(host=mongo_ip,
                                                     collection_names_regex='^aggr_',
                                                     is_start=False)
