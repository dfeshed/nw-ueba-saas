from datetime import timedelta

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator
from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton

INPUT_JVM_ARGS_CONFIG_PATH = 'components.input.jvm_args'


class InputDagBuilder(PresidioDagBuilder):
    """
    An "Input DAG" builder - The "Input DAG" consists of multiple tasks / operators of given schema.
    The builder accepts the DAG's attributes through the c'tor, and the "build" method builds and
    returns the DAG according to the given attributes.
    """

    conf_reader = ConfigServerConfigurationReaderSingleton().config_reader

    def __init__(self, schema):
        """
        C'tor.
        :param schema: The schema we should work on
        :type schema: str
        """

        self.schema = schema
        self.jvm_args = InputDagBuilder.conf_reader.read(conf_key=INPUT_JVM_ARGS_CONFIG_PATH)

    def build(self, indicator_dag):
        """
        Builds input operators for schema and adds them to the given DAG.
        :param indicator_dag: The DAG to which all relevant "input" operators should be added
        :type indicator_dag: airflow.models.DAG
        :return: The indicator DAG, after the "input" operators were added
        :rtype: airflow.models.DAG
        """

        self.log.debug("populating the %s dag with input tasks", indicator_dag.dag_id)

        task_sensor_service = TaskSensorService()

        java_args = {
            'schema': self.schema
        }

        # Create jar operator for each data source
        input_fixed_duration_jar_operator = FixedDurationJarOperator(
            task_id='input_{}'.format(self.schema),
            fixed_duration_strategy=timedelta(hours=1),
            command=PresidioDagBuilder.presidio_command,
            jvm_args=self.jvm_args,
            java_args=java_args,
            dag=indicator_dag)

        task_sensor_service.add_task_sequential_sensor(input_fixed_duration_jar_operator)

        return indicator_dag
