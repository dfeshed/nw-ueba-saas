from airflow.models import DAG
from datetime import datetime
from presidio.builders.rerun_analytics_flow_dag_builder import RerunAnalyticsFlowDagBuilder

# collector is not used on Netwitness deployment
# rerun_full_flow_dag = DAG(dag_id="rerun_full_flow", schedule_interval=None, start_date=datetime(2015, 6, 1))
# rerun_full_flow = RerunFullFlowDagBuilder.build(rerun_full_flow_dag, False)
reset_presidio_flow_dag = DAG(dag_id="reset_presidio", schedule_interval=None, start_date=datetime(2015, 6, 1))
reset_presidio = RerunAnalyticsFlowDagBuilder.build(reset_presidio_flow_dag, False)
