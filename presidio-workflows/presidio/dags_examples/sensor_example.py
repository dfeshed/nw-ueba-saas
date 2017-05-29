"""
Example dag for using TaskSensorService
"""
from airflow import DAG
from airflow.operators.bash_operator import BashOperator
from datetime import datetime, timedelta

from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService

default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'start_date': datetime(2015, 6, 1),
    'email': ['airflow@airflow.com'],
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
    # 'queue': 'bash_queue',
    # 'pool': 'backfill',
    # 'priority_weight': 10,
    # 'end_date': datetime(2016, 1, 1),
}
dag = DAG('sensor_example', default_args=default_args)

taskSensorService = TaskSensorService()

# t1, t2 and t3 are examples of tasks created by instantiating operators
t1 = BashOperator(
    task_id='print_date',
    bash_command='date',
    dag=dag)
taskSensorService.add_task_sequential_sensor(t1)

t2 = BashOperator(
    task_id='sleep',
    bash_command='sleep 5',
    retries=3,
    dag=dag)
taskSensorService.add_task_sequential_sensor(t2)

templated_command = """
    {% for i in range(5) %}
        echo "{{ ds }}"
        echo "{{ macros.ds_add(ds, 7)}}"
        echo "{{ params.my_param }}"
    {% endfor %}
"""

t3 = BashOperator(
    task_id='templated',
    bash_command=templated_command,
    params={'my_param': 'Parameter I passed in'},
    dag=dag)
taskSensorService.add_task_sequential_sensor(t3)



t2.set_upstream(t1)
t3.set_upstream(t1)
taskSensorService.add_task_gap_sensor(t3,t2,timedelta(days=2),poke_interval=1)