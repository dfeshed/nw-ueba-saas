from airflow import LoggingMixin

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.aggregation.feature_aggregations_operator import FeatureAggregationsOperator


class FeatureAggregationsOperatorBuilder(LoggingMixin):
    """
    The "FeatureAggregationsOperatorBuilder" builds and returns the feature_aggregations_operator
     according to the given attributes (schema, fixed_duration).
    """

    def __init__(self, schema, fixed_duration, **kwargs):
        """
        C'tor.
        :param fixed_duration_strategy: The duration covered by the aggregations (e.g. hourly or daily)
        :type fixed_duration_strategy: datetime.timedelta
        :param schema: The schema we should work on
        :type schema: str
        """
        self.schema = schema
        self.fixed_duration = fixed_duration

    def build(self, dag, task_sensor_service):
        """
        Builds the "Feature Aggregations" operator.
        :param dag: The DAG to which all relevant "FeatureAggregations" operators should be added
        :type dag: airflow.models.DAG
        :return: feature_aggregations_operator
        :rtype: presidio.operators.fixed_duration_jar_operator.FixedDurationJarOperator
        """
        self.log.debug("populating the %s dag with scoring tasks", dag.dag_id)

        feature_aggregations_operator = FeatureAggregationsOperator(
            fixed_duration_strategy=self.fixed_duration,
            command=PresidioDagBuilder.presidio_command,
            data_source=self.schema,
            dag=dag)

        task_sensor_service.add_task_sequential_sensor(feature_aggregations_operator)

        return feature_aggregations_operator
