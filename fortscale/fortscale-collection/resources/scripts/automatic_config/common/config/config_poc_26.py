# This file overrides basic properties for the setup of a POC of versions later than 2.6.

order = 3

aggregated_feature_event_prevalance_stats_path = {
    'overriding_path': '/home/cloudera/fortscale/config/asl/scorers/aggregation-events',
    'jar_name': 'fortscale-ml-1.1.0-SNAPSHOT.jar',
    'path_in_jar': 'config/asl/scorers/aggregation-events'
}

aggregated_feature_event_prevalance_stats_additional_path = '/home/cloudera/fortscale/config/asl/scorers/aggregation-events/additional'