from datetime import datetime, timedelta

from airflow import DAG
from airflow.operators.subdag_operator import SubDagOperator

from presidio.builders.presidio_core_dag_builder import PresidioCoreDagBuilder
from presidio.utils.airflow.operators.sensor.root_dag_gap_sensor_operator import RootDagGapSensorOperator

def get_presidio_core_sub_dag_operator(dag, data_sources):
    presidio_core_dag_id = 'presidio_core_dag'

    presidio_core_dag = DAG(
        dag_id='{}.{}'.format(dag.dag_id, presidio_core_dag_id),
        schedule_interval=dag.schedule_interval,
        start_date=dag.start_date,
        default_args=dag.default_args
    )

    return SubDagOperator(
        subdag=PresidioCoreDagBuilder(data_sources).build(presidio_core_dag),
        task_id=presidio_core_dag_id,
        dag=dag,
        retries=4,
        retry_delay=timedelta(seconds=5),
        retry_exponential_backoff=True,
        max_retry_delay=timedelta(minutes=10)
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
    'daily_smart_events_confs':[],
    'hourly_smart_events_confs':['userId_hourly'],
    # 'queue': 'bash_queue',
    # 'pool': 'backfill',
    # 'priority_weight': 10,
    # 'end_date': datetime(2016, 1, 1),
}
presidio_core_dag_example = DAG('presidio_core_dag_example', start_date=datetime(2017, 8, 17, 0), schedule_interval=timedelta(minutes=60), default_args=default_args)

root_dag_gap_sensor_operator = RootDagGapSensorOperator(dag=presidio_core_dag_example, task_id='presidio_core_dag_example_root_gap_sensor', external_dag_id=presidio_core_dag_example.dag_id,
                                       execution_delta=timedelta(days=1),
                                       poke_interval=5)
presidio_core_sub_dag_operator = get_presidio_core_sub_dag_operator(presidio_core_dag_example,['FILE','ACTIVE_DIRECTORY','AUTHENTICATION'])

root_dag_gap_sensor_operator >> presidio_core_sub_dag_operator