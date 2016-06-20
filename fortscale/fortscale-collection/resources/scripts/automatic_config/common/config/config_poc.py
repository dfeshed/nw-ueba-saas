# This file overrides basic properties for the setup of a POC.

order = 2

mongo_ip = 'localhost'
aggregated_feature_event_prevalance_stats_path = '/home/cloudera/fortscale/streaming/config/aggregated-feature_event-prevalance-stats.properties'
entity_events_path = {
    'overriding_path': '/home/cloudera/fortscale/config/asl/entity_events/overriding/entity_events.json',
    'jar_name': 'fortscale-aggregation-1.1.0-SNAPSHOT.jar',
    'path_in_jar': 'config/asl/entity_events.json'
}
interim_results_path = '/home/cloudera/automatic config interim results'
show_graphs = False