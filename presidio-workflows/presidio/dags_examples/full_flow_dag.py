from datetime import datetime, timedelta

from airflow import DAG
from presidio.builders.full_flow_dag_builder import FullFlowDagBuilder



default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'start_date': datetime(2015, 6, 1),
    'email': ['airflow@airflow.com'],
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 0,
    'retry_delay': timedelta(minutes=5),
    'data_sources':"FILE,ACTIVE_DIRECTORY,AUTHENTICATION",
    'daily_smart_events_confs':[],
    'hourly_smart_events_confs':['userId_hourly'],
    # 'queue': 'bash_queue',
    # 'pool': 'backfill',
    # 'priority_weight': 10,
    # 'end_date': datetime(2016, 1, 1),
}
full_flow_dag = DAG(dag_id='full_flow_dag', start_date=datetime(2017, 8, 17, 0), schedule_interval=timedelta(minutes=60),default_args=default_args)
builder = FullFlowDagBuilder()
builder.build(full_flow_dag)
