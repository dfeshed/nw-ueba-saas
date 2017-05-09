"""
initiate historical dags that are defined in variables
"""
import json

import dateutil.parser
import pkg_resources
from airflow import DAG
from airflow.models import Variable
from airflow.operators.dagrun_operator import TriggerDagRunOperator


def get_default_variable():
    """
    reads default variable configured in resourced json
    :return: json dictionary containing default variable value
    """
    DEFAULT_DAG_VARIABLES_FILE_PATH = pkg_resources.resource_filename('presidio',
                                                                      'resources/variables/dags/historical_workflow_creator.json')
    with open(DEFAULT_DAG_VARIABLES_FILE_PATH) as default_dag_variables_file:
        default_dag_variables = json.load(default_dag_variables_file)
    return default_dag_variables


def get_dag_variable():
    """
    :return: Try to get the value of the variable "variable_scheduler_dag_params" from airflow's variables key-value store
    if not exist, default value is returned 
    """
    return Variable.get("historical_workflow_creator", default_var=get_default_variable(),
                        deserialize_json=True)


def create_presidio_main_dag(dag_id):
    """
    :param dag_id
    :type dag_id: str
    :return: instance of Presidio's main dag containing given    
    """
    dag = DAG(dag_id=dag_id)
    globals()[dag_id] = dag
    return dag


def conditionally_trigger(context, dag_run_obj):
    """This function decides whether or not to Trigger the remote DAG"""
    # todo add conditions if needed
    return dag_run_obj


def create_historical_dags(historical_dags_params):
    """
    iterates over historical dags params and initiates dags for them
    """
    for historical_dag_params in historical_dags_params:
        args = historical_dag_params.get("args")
        historical_dag_id = historical_dag_params.get("dag_id")
        interval = historical_dag_params.get("schedule_interval")
        start_date = dateutil.parser.parse(historical_dag_params.get("start_date"))
        #     todo: add all relvant params
        historical_dag = DAG(historical_dag_id)
        presidio_main_dag_id = "{}.presidio_main".format(historical_dag_id)
        presidio_main_dag = create_presidio_main_dag(dag_id=presidio_main_dag_id)
        presidio_main_dag_run_operator = TriggerDagRunOperator(task_id=presidio_main_dag_id,
                                                               start_date=start_date,
                                                               trigger_dag_id=presidio_main_dag_id, dag=historical_dag,
                                                               python_callable=conditionally_trigger)
        globals()[historical_dag_id] = historical_dag


variable_historical_workflow_creator = get_dag_variable()
params = variable_historical_workflow_creator.get("historical_dags")
create_historical_dags(params)