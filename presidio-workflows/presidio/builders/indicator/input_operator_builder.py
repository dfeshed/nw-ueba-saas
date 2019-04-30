from datetime import timedelta

from airflow import LoggingMixin

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator
from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton

INPUT_JVM_ARGS_CONFIG_PATH = 'components.input.jvm_args'


class InputOperatorBuilder(LoggingMixin):
    """
    The "InputOperatorBuilder" builds and returns the input_operator according to the given schema.
    """

    def __init__(self, schema):
        """
        C'tor.
        :param schema: The schema we should work on
        :type schema: str
        """

        conf_reader = ConfigServerConfigurationReaderSingleton().config_reader
        self.schema = schema
        self.jvm_args = conf_reader.read(conf_key=INPUT_JVM_ARGS_CONFIG_PATH)

    def build(self, dag):
        """
        Builds input operator.
        :param dag: The DAG to which all relevant "input" operators should be added
        :type dag: airflow.models.DAG
        :return: The input operator
        :rtype: presidio.operators.fixed_duration_jar_operator.FixedDurationJarOperator
        """

        self.log.debug("populating the %s dag with input tasks", dag.dag_id)

        task_sensor_service = TaskSensorService()

        java_args = {
            'schema': self.schema
        }

        input_operator = FixedDurationJarOperator(
            task_id='input_{}'.format(self.schema),
            fixed_duration_strategy=timedelta(hours=1),
            command=PresidioDagBuilder.presidio_command,
            jvm_args=self.jvm_args,
            java_args=java_args,
            dag=dag)

        task_sensor_service.add_task_sequential_sensor(input_operator)

        return input_operator
