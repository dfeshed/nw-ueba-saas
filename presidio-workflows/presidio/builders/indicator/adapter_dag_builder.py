from datetime import timedelta

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator
from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService
presidio_extension = __import__('presidio_extension.builders.adapter.adapter_dag_builder_extension', fromlist=['AdapterDagBuilderExtension'])
AdapterDagBuilderExtension = getattr(presidio_extension, 'AdapterDagBuilderExtension')

ADAPTER_JVM_ARGS_CONFIG_PATH = 'components.adapter.jvm_args'


class AdapterDagBuilder(PresidioDagBuilder):
    """
    A "Adapter DAG" builder - The "Adapter DAG" consists of multiple tasks / operators of given schema.
    The builder accepts the DAG's attributes through the c'tor, and the "build" method builds and
    returns the DAG according to the given attributes.
    """

    def __init__(self, schema):
        """
        C'tor.
        :param schema: The schema we should work on
        :type schema: str
        """
        self.schema = schema
        self.jvm_args = AdapterDagBuilder.conf_reader.read(conf_key=ADAPTER_JVM_ARGS_CONFIG_PATH)

    def build(self, indicator_dag):
        """
        Builds adapter jar operators for schema and adds them to the given DAG.
        :param indicator_dag: The DAG to which all relevant "adapter" operators should be added
        :type indicator_dag: airflow.models.DAG
        :return: The indicator DAG, after the "adapter" operators were added
        :rtype: airflow.models.DAG
        """
        self.log.debug("populating the %s dag with adapter tasks", indicator_dag.dag_id)

        task_sensor_service = TaskSensorService()

        java_args = {
            'schema': self.schema,
        }

        # Create jar operator for each data source
        adapter_jar_operator = FixedDurationJarOperator(
            task_id='adapter_{}'.format(self.schema),
            fixed_duration_strategy=timedelta(hours=1),
            command=PresidioDagBuilder.presidio_command,
            jvm_args=self.jvm_args,
            java_args=java_args,
            component='adapter',
            dag=indicator_dag)
        adapter_dag_extended = AdapterDagBuilderExtension()
        adapter_dag_extended.build(indicator_dag, self.schema, adapter_jar_operator)
        task_sensor_service.add_task_sequential_sensor(adapter_jar_operator)

        return indicator_dag


