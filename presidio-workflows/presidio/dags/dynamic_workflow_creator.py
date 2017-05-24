"""
initiate presidio DAGs that are defined in configuration
should be used to create template-ed (same graph)DAGs for different execution dates, having different dag_id's
i.e. if i want to create historical load for two different dates: i can 
"""
from __future__ import generators

import pkg_resources

from presidio.builders.full_flow_dag_builder import FullFlowDagBuilder
from presidio.factories.presidio_dag_factory import PresidioDagFactory
from presidio.utils.airflow.dag.dag_factory import DagFactories
from presidio.utils.airflow.variable.variable_configuration_reader import VariableReader



PresidioDagFactory()

DEFAULT_DAG_VARIABLES_FILE_PATH = pkg_resources.resource_filename('presidio',
                                                                  'resources/variables/dags/dynamic_workflow_creator.json')
variable_reader = VariableReader(default_value_file_path=DEFAULT_DAG_VARIABLES_FILE_PATH,
                                 var_key='presidio_dag_factory')
dags = DagFactories.create_dags("PresidioDag", conf_reader=variable_reader, name_space=globals(),
                                dag_builder=FullFlowDagBuilder(["dlpfile"]))

