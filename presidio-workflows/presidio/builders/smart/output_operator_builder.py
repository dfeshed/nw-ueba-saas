from datetime import timedelta

from airflow import LoggingMixin

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton

OUTPUT_JVM_ARGS_CONFIG_PATH = 'components.output.jvm_args'


class OutputOperatorBuilder(LoggingMixin):
    """
    The "OutputOperatorBuilder" builds and returns hourly_output_operator according to the given attributes.
    """

    def __init__(self, smart_record_conf_name):
        """
        C'tor.
        :param smart_record_conf_name: smart_record_conf_name we should work on
        :type smart_record_conf_name: str
        """
        conf_reader = ConfigServerConfigurationReaderSingleton().config_reader
        self.smart_record_conf_name = smart_record_conf_name
        self.jvm_args = conf_reader.read(conf_key=OUTPUT_JVM_ARGS_CONFIG_PATH)

    def build(self, dag, task_sensor_service):
        """
        Builds hourly_output_operator.
        :param task_sensor_service: task_sensor_service
        :param dag: The DAG to which all relevant "output" operators should be added
        :type dag: airflow.models.DAG
        :return: hourly_output_operator
        :rtype: presidio.operators.fixed_duration_jar_operator.FixedDurationJarOperator
        """

        self.log.debug("populating the %s dag with output tasks", dag.dag_id)

        java_args = {
            'smart_record_conf_name': self.smart_record_conf_name,
        }

        hourly_output_operator = FixedDurationJarOperator(
            task_id='hourly_output_processor',
            fixed_duration_strategy=timedelta(hours=1),
            command=PresidioDagBuilder.presidio_command,
            jvm_args=self.jvm_args,
            dag=dag,
            java_args=java_args
        )

        task_sensor_service.add_task_sequential_sensor(hourly_output_operator)

        return hourly_output_operator
