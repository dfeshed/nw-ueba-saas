"""
initiate historical dags that are defined in variables
"""
from __future__ import generators

import pkg_resources

from presidio.utils.airflow.dag.dag_factory import DagsFactory
from presidio.utils.airflow.variable.variable_configuration_reader import VariableReader


DEFAULT_DAG_VARIABLES_FILE_PATH = pkg_resources.resource_filename('presidio',
                                                                  'resources/variables/dags/historical_workflow_creator.json')
variable_reader = VariableReader(default_value_file_path=DEFAULT_DAG_VARIABLES_FILE_PATH, var_key='presidio_dag_factory')
dags = DagsFactory.createDags("PresidioDag", conf_reader=variable_reader)

