# This file contains all the configuration properties.

order = 1

#mongo_ip = 'tc-agent7'
mongo_ip = '192.168.45.44'
aggregated_feature_event_prevalance_stats_path = r'C:\Users\yoelz\projects\fortscale-core\fortscale\fortscale-streaming\config\aggregated-feature_event-prevalance-stats.properties'
entity_events_path = r'C:\Users\yoelz\projects\fortscale-core\fortscale\fortscale-aggregation\src\main\resources\config\asl\entity_events.json'
interim_results_path = r'C:\Users\yoelz\projects\fortscale-core\fortscale\fortscale-collection\resources\scripts\automatic_config\notebooks\automatic config interim results'
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

show_graphs = False
