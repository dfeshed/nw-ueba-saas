"""
initiate presidio DAGs that are defined in configuration
should be used to create template-ed (same graph)DAGs for different execution dates, having different dag_id's
i.e. if i want to create historical load for two different dates: i can
"""

from airflow import DAG
from presidio.factories.indicator_dag_factory import IndicatorDagFactory
from presidio.factories.initial_dag_factory import InitialDagFactory
from presidio.factories.model_dag_factory import ModelDagFactory
from presidio.factories.presidio_pool_factory import PresidioPoolFactory
from presidio.factories.smart_dag_factory import SmartDagFactory
from presidio.factories.smart_model_dag_factory import SmartModelDagFactory
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton

name_space = globals()
config_reader = ConfigServerConfigurationReaderSingleton().config_reader

initialDagFactory = InitialDagFactory()
dags = initialDagFactory.create_and_register_dags(conf_key=InitialDagFactory.initial_conf_key, name_space=name_space, config_reader=config_reader)

indicatorDagFactory = IndicatorDagFactory()
dags = indicatorDagFactory.create_and_register_dags(conf_key=IndicatorDagFactory.indicator_conf_key, name_space=name_space, config_reader=config_reader)

modelDagFactory = ModelDagFactory()
dags = modelDagFactory.create_and_register_dags(conf_key=ModelDagFactory.model_conf_key, name_space=name_space, config_reader=config_reader)

smartDagFactory = SmartDagFactory()
dags = smartDagFactory.create_and_register_dags(conf_key=SmartDagFactory.smart_conf_key, name_space=name_space, config_reader=config_reader)

smartModelDagFactory = SmartModelDagFactory()
dags = smartModelDagFactory.create_and_register_dags(conf_key=SmartModelDagFactory.smart_model_conf_key, name_space=name_space, config_reader=config_reader)

presidioPoolFactory = PresidioPoolFactory()
presidioPoolFactory.create(config_reader=config_reader)


