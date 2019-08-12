"""
initiate presidio DAGs that are defined in configuration
should be used to create template-ed (same graph)DAGs for different execution dates, having different dag_id's
i.e. if i want to create historical load for two different dates: i can
"""

from airflow import DAG
from presidio.factories.indicator_dag_factory import IndicatorDagFactory
from presidio.factories.input_pre_processing_dag_factory import InputPreProcessingDagFactory
from presidio.factories.retention_dag_factory import RetentionDagFactory
from presidio.factories.root_dag_factory import RootDagFactory
from presidio.factories.model_dag_factory import ModelDagFactory
from presidio.factories.presidio_pool_factory import PresidioPoolFactory
from presidio.factories.smart_dag_factory import SmartDagFactory
from presidio.factories.smart_model_dag_factory import SmartModelDagFactory
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton

name_space = globals()
config_reader = ConfigServerConfigurationReaderSingleton().config_reader

# create pool
presidioPoolFactory = PresidioPoolFactory()
presidioPoolFactory.create(config_reader=config_reader)

# create and register dags
root_dag_factory = RootDagFactory()
root_dags = root_dag_factory.create_and_register_dags(conf_key=RootDagFactory.root_conf_key, name_space=name_space, config_reader=config_reader)
indicator_dag_factory = IndicatorDagFactory()
indicator_dags = indicator_dag_factory.create_and_register_dags(conf_key=IndicatorDagFactory.indicator_conf_key, name_space=name_space, config_reader=config_reader)
model_dag_factory = ModelDagFactory()
model_dags = model_dag_factory.create_and_register_dags(conf_key=ModelDagFactory.model_conf_key, name_space=name_space, config_reader=config_reader)
smart_dag_factory = SmartDagFactory()
smart_dags = smart_dag_factory.create_and_register_dags(conf_key=SmartDagFactory.smart_conf_key, name_space=name_space, config_reader=config_reader)
smart_model_dag_factory = SmartModelDagFactory()
smart_model_dags = smart_model_dag_factory.create_and_register_dags(conf_key=SmartModelDagFactory.smart_model_conf_key, name_space=name_space, config_reader=config_reader)
retention_dag_factory = RetentionDagFactory()
retention_dag = retention_dag_factory.create_and_register_dags(conf_key=RetentionDagFactory.retention_conf_key, name_space=name_space, config_reader=config_reader)
input_pre_processing_dag_factory = InputPreProcessingDagFactory()
input_pre_processing_dag = input_pre_processing_dag_factory.create_and_register_dags(conf_key=InputPreProcessingDagFactory.INPUT_PRE_PROCESSING_CONF_KEY, name_space=name_space, config_reader=config_reader)



# build dags
root_dag_factory.build_dags(root_dags)
indicator_dag_factory.build_dags(indicator_dags)
model_dag_factory.build_dags(model_dags)
smart_dag_factory.build_dags(smart_dags)
smart_model_dag_factory.build_dags(smart_model_dags)
retention_dag_factory.build_dags(retention_dag)
input_pre_processing_dag_factory.build_dags(input_pre_processing_dag)
