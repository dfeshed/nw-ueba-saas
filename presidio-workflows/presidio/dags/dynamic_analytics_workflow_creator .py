"""
initiate presidio DAGs that are defined in configuration
should be used to create template-ed (same graph)DAGs for different execution dates, having different dag_id's
i.e. if i want to create historical load for two different dates: i can
"""

from airflow import DAG
from presidio.factories.indicator_dag_factory import IndicatorDagFactory
from presidio.factories.root_dag_factory import RootDagFactory
from presidio.factories.model_dag_factory import ModelDagFactory
from presidio.factories.presidio_pool_factory import PresidioPoolFactory
from presidio.factories.smart_dag_factory import SmartDagFactory
from presidio.factories.smart_model_dag_factory import SmartModelDagFactory
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton

name_space = globals()
config_reader = ConfigServerConfigurationReaderSingleton().config_reader

# create and register dags
rootDagFactory = RootDagFactory()
root_dags = rootDagFactory.create_and_register_dags(conf_key=RootDagFactory.root_conf_key, name_space=name_space, config_reader=config_reader)
indicatorDagFactory = IndicatorDagFactory()
indicator_dags = indicatorDagFactory.create_and_register_dags(conf_key=IndicatorDagFactory.indicator_conf_key, name_space=name_space, config_reader=config_reader)
modelDagFactory = ModelDagFactory()
model_dags = modelDagFactory.create_and_register_dags(conf_key=ModelDagFactory.model_conf_key, name_space=name_space, config_reader=config_reader)
smartDagFactory = SmartDagFactory()
smart_dags = smartDagFactory.create_and_register_dags(conf_key=SmartDagFactory.smart_conf_key, name_space=name_space, config_reader=config_reader)
smartModelDagFactory = SmartModelDagFactory()
smart_model_dags = smartModelDagFactory.create_and_register_dags(conf_key=SmartModelDagFactory.smart_model_conf_key, name_space=name_space, config_reader=config_reader)

# build dags
rootDagFactory.build_dags(root_dags)
indicatorDagFactory.build_dags(indicator_dags)
modelDagFactory.build_dags(model_dags)
smartDagFactory.build_dags(smart_dags)
smartModelDagFactory.build_dags(smart_model_dags)

presidioPoolFactory = PresidioPoolFactory()
presidioPoolFactory.create(config_reader=config_reader)
