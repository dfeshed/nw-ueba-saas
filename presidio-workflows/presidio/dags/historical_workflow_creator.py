"""
initiate historical dags that are defined in variables
"""
from __future__ import generators

import pkg_resources
from airflow import DAG

from presidio.utils.airflow.dag.dag_factory import DagsFactory
from presidio.utils.airflow.variable.variable_configuration_reader import VariableReader


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









        # def DagNameGen(n):


# types = DynamicDag.__subclasses__()
#     for i in range(n):
#         yield random.choice(types).__name__
# shapes = [DagsFactory.createDags(i)
#           for i in DagNameGen(7)]
# DagsFactory.addFactory("PresidioDag",PresidioDag)
DEFAULT_DAG_VARIABLES_FILE_PATH = pkg_resources.resource_filename('presidio',
                                                                  'resources/variables/dags/historical_workflow_creator.json')
dags = DagsFactory.createDags("PresidioDag", variable_reader=VariableReader(
    default_value_file_path=DEFAULT_DAG_VARIABLES_FILE_PATH, var_key='presidio_dag_factory'))
#
# variable_historical_workflow_creator = get_dag_variable()
# params = variable_historical_workflow_creator.get("historical_dags")
# create_historical_dags(params)
