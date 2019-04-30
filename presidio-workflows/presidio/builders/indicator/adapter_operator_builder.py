from datetime import timedelta

from airflow import LoggingMixin

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator
from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton

presidio_extension = __import__('presidio_extension.builders.adapter.adapter_dag_builder_extension',
                                fromlist=['AdapterDagBuilderExtension'])
AdapterDagBuilderExtension = getattr(presidio_extension, 'AdapterDagBuilderExtension')

ADAPTER_JVM_ARGS_CONFIG_PATH = 'components.adapter.jvm_args'


class AdapterOperatorBuilder(LoggingMixin):
    """
    The "AdapterOperatorBuilder"  builds and returns adapter operator of the given schema.
    """

    def __init__(self, schema):
        """
        C'tor.
        :param schema: The schema we should work on
        :type schema: str
        """
        conf_reader = ConfigServerConfigurationReaderSingleton().config_reader
        self.schema = schema
        self.jvm_args = conf_reader.read(conf_key=ADAPTER_JVM_ARGS_CONFIG_PATH)

    def build(self, dag):
        """
        Builds adapter jar operators.
        :param dag: The DAG to which all relevant "adapter" operators should be added
        :type dag: airflow.models.DAG
        :return: The adapter_jar operator
        :rtype: presidio.operators.fixed_duration_jar_operator.FixedDurationJarOperator
        """
        self.log.debug("populating the %s dag with adapter tasks", dag.dag_id)

        task_sensor_service = TaskSensorService()

        java_args = {
            'schema': self.schema,
        }

        adapter_operator = FixedDurationJarOperator(
            task_id='adapter_{}'.format(self.schema),
            fixed_duration_strategy=timedelta(hours=1),
            command=PresidioDagBuilder.presidio_command,
            jvm_args=self.jvm_args,
            java_args=java_args,
            component='adapter',
            dag=dag)
        adapter_dag_extended = AdapterDagBuilderExtension()
        adapter_dag_extended.build(dag, self.schema, adapter_operator)
        task_sensor_service.add_task_sequential_sensor(adapter_operator)

        return adapter_operator
