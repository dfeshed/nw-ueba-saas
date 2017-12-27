from presidio.builders.rerun_full_flow_dag_builder import RerunFullFlowDagBuilder

# TODO: get the property "is_remove_ca_tables" from the configuration or create 2 different dags
rerun_full_flow = RerunFullFlowDagBuilder.build(False)
