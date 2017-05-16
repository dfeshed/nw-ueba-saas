"""
initiate historical dags that are defined in variables
"""
from __future__ import generators

import pkg_resources
from airflow.operators.dummy_operator import DummyOperator

from presidio.builder.presidio_dag_builder import PresidioDagBuilder
from presidio.utils.airflow.dag.dag_factory import DagsFactory
from presidio.utils.airflow.variable.variable_configuration_reader import VariableReader

class DummyPresidioDagBuilder(PresidioDagBuilder):
    def build(self, dag):
        DummyOperator(task_id='dummy', dag=dag)


DEFAULT_DAG_VARIABLES_FILE_PATH = pkg_resources.resource_filename('presidio',
                                                                  'resources/variables/dags/historical_workflow_creator.json')
variable_reader = VariableReader(default_value_file_path=DEFAULT_DAG_VARIABLES_FILE_PATH, var_key='presidio_dag_factory')
# todo, use the real builder instead
dags = DagsFactory.createDags("PresidioDag", conf_reader=variable_reader,name_space=globals(),dag_builder=DummyPresidioDagBuilder())

