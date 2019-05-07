from datetime import timedelta

from airflow import LoggingMixin

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.adapter.adapter_operator import AdapterOperator
from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService

presidio_extension = __import__('presidio_extension.builders.adapter.adapter_dag_builder_extension',
                                fromlist=['AdapterDagBuilderExtension'])
AdapterDagBuilderExtension = getattr(presidio_extension, 'AdapterDagBuilderExtension')


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
        self.schema = schema

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
        adapter_operator = AdapterOperator(
            fixed_duration_strategy=timedelta(hours=1),
            command=PresidioDagBuilder.presidio_command,
            schema=self.schema,
            dag=dag)

        adapter_dag_extended = AdapterDagBuilderExtension()
        adapter_dag_extended.build(dag, self.schema, adapter_operator)
        task_sensor_service.add_task_sequential_sensor(adapter_operator)

        return adapter_operator
