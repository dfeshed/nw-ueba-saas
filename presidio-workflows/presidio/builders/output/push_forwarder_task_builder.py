from datetime import timedelta

from airflow.utils.log.logging_mixin import LoggingMixin
from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton

FORWARDER_JVM_ARGS_CONFIG_PATH = 'components.output_forwarder.jvm_args'


class PushForwarderTaskBuilder(LoggingMixin):

    conf_reader = ConfigServerConfigurationReaderSingleton().config_reader

    def __init__(self, condition):

        config_reader = ConfigServerConfigurationReaderSingleton().config_reader

        self.jvm_args = config_reader.read(conf_key=FORWARDER_JVM_ARGS_CONFIG_PATH)
        self.condition = condition

    def build(self, presidio_core_dag):
        """
        Builds jar operators for each data source and adds them to the given DAG.
        :param presidio_core_dag: The DAG to which the task should be added
        :type presidio_core_dag: airflow.models.DAG
        :return: The created forwarder task
        :rtype: FixedDurationJarOperator
        """

        self.log.debug("creating the forwarder task")

        # Create jar operator
        data_forwarding_operator = FixedDurationJarOperator(
            task_id='output_forwarding_task',
            fixed_duration_strategy=timedelta(hours=1),
            command=PresidioDagBuilder.presidio_command,
            jvm_args=self.jvm_args,
            java_args={'smart_record_conf_name': 'userId_hourly'},
            dag=presidio_core_dag,
            condition=self.condition)

        return data_forwarding_operator
