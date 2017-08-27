from datetime import datetime, timedelta

from airflow import DAG

from presidio.builders.ade.anomaly_detection_engine_dag_builder import AnomalyDetectionEngineDagBuilder

default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'start_date': datetime(2015, 6, 1),
    'email': ['airflow@airflow.com'],
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 0,
    'retry_delay': timedelta(minutes=5),
    # 'queue': 'bash_queue',
    # 'pool': 'backfill',
    # 'priority_weight': 10,
    # 'end_date': datetime(2016, 1, 1),
}
dag = DAG('anomaly_detection_engine_dag_example', start_date=datetime(2015, 7, 1, 23), schedule_interval=timedelta(minutes=60), default_args=default_args)
#dag_builder = AnomalyDetectionEngineDagBuilder(['dlpfile','dlpmail'],['hourly_smart','hourly_exfiltration'],['daily_smart'])
dag_builder = AnomalyDetectionEngineDagBuilder(['FILE','ACTIVE_DIRECTORY','AUTHENTICATION'],['userId_hourly'],[])
dag_builder.build(dag)