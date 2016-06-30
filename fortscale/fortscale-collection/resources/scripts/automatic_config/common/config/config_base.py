# This file contains all the configuration properties.

order = 1

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

F_REDUCER_TO_MIN_POSITIVE_SCORE = {
    # uncomment the following line in order to make sure number_of_failed_kerberos_logins_hourly always gets score 0 for values <= 2
    # 'number_of_failed_kerberos_logins_hourly': 3
}

dry = False

show_graphs = False
