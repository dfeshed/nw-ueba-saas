from datetime import datetime, timedelta

from airflow import DAG
from airflow.operators.subdag_operator import SubDagOperator

from presidio.builders.ade.anomaly_detection_engine_scoring_dag_builder import AnomalyDetectionEngineScoringDagBuilder
from presidio.builders.ade.anomaly_detection_engine_modeling_dag_builder import AnomalyDetectionEngineModelingDagBuilder





def get_ade_scoring_sub_dag_operator(data_sources, hourly_smart_events_confs, presidio_core_dag):
    ade_scoring_dag_id = 'ade_scoring_dag'

    ade_scoring_dag = DAG(
        dag_id='{}.{}'.format(presidio_core_dag.dag_id, ade_scoring_dag_id),
        schedule_interval=presidio_core_dag.schedule_interval,
        start_date=presidio_core_dag.start_date,
        default_args=presidio_core_dag.default_args
    )

    return SubDagOperator(
        subdag=AnomalyDetectionEngineScoringDagBuilder(data_sources, hourly_smart_events_confs,
                                                       []).build(ade_scoring_dag),
        task_id=ade_scoring_dag_id,
        dag=presidio_core_dag
    )

def get_ade_modeling_sub_dag_operator(data_sources, hourly_smart_events_confs, presidio_core_dag):
    ade_modeling_dag_id = 'ade_modeling_dag'

    ade_modeling_dag = DAG(
        dag_id='{}.{}'.format(presidio_core_dag.dag_id, ade_modeling_dag_id),
        schedule_interval=presidio_core_dag.schedule_interval,
        start_date=presidio_core_dag.start_date,
        default_args=presidio_core_dag.default_args
    )

    return SubDagOperator(
        subdag=AnomalyDetectionEngineModelingDagBuilder(data_sources, hourly_smart_events_confs,
                                                       []).build(ade_modeling_dag),
        task_id=ade_modeling_dag_id,
        dag=presidio_core_dag
    )

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
dag = DAG('anomaly_detection_engine_dag_example', start_date=datetime(2017, 9, 5, 0), schedule_interval=timedelta(minutes=60), default_args=default_args)
#dag_builder = AnomalyDetectionEngineDagBuilder(['dlpfile','dlpmail'],['hourly_smart','hourly_exfiltration'],['daily_smart'])
data_sources=['FILE','ACTIVE_DIRECTORY','AUTHENTICATION']
hourly_smart_events_confs = ['userId_hourly']
ade_scoring_sub_dag_operator = get_ade_scoring_sub_dag_operator(data_sources, hourly_smart_events_confs, dag)
ade_modeling_sub_dag_operator = get_ade_modeling_sub_dag_operator(data_sources, hourly_smart_events_confs, dag)
ade_scoring_sub_dag_operator >> ade_modeling_sub_dag_operator