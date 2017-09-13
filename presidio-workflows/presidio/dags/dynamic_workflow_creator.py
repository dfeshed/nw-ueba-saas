"""
initiate presidio DAGs that are defined in configuration
should be used to create template-ed (same graph)DAGs for different execution dates, having different dag_id's
i.e. if i want to create historical load for two different dates: i can
"""
from __future__ import generators

from presidio.builders.full_flow_dag_builder import FullFlowDagBuilder
from presidio.factories.presidio_dag_factory import PresidioDagFactory
from presidio.utils.airflow.dag.dag_factory import DagFactories
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton

PresidioDagFactory()

config_reader = ConfigServerConfigurationReaderSingleton().config_reader
dags = DagFactories.create_dags("PresidioDag", conf_reader=config_reader, name_space=globals(),
                                dag_builder=FullFlowDagBuilder())
