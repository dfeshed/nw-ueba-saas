from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.aggregation.feature_aggregations_operator import FeatureAggregationsOperator
from presidio.operators.aggregation.score_aggregations_operator import ScoreAggregationsOperator
from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService


class AggregationsDagBuilder(PresidioDagBuilder):
    """
    An "Aggregations DAG" builder - The "Aggregations DAG" consists of 2 tasks / operators
    (feature aggregations and score aggregations) and can be either an hourly DAG or a daily DAG.
    The builder accepts the DAG's attributes through the c'tor, and the "build" method builds and
    returns the DAG according to the given attributes.
    """

    def __init__(self, fixed_duration_strategy, data_source, condition=None, **kwargs):
        """
        C'tor.
        :param fixed_duration_strategy: The duration covered by the aggregations (e.g. hourly or daily)
        :type fixed_duration_strategy: datetime.timedelta
        :param data_source: The data source whose events should be aggregated
        :type data_source: str
        """
        self.fixed_duration_strategy = fixed_duration_strategy
        self.data_source = data_source
        self.condition = condition

    def build(self, aggregations_dag):
        """
        Builds the "Feature Aggregations" operator, the "Score Aggregations" operator and adds them to the given DAG.
        :param aggregations_dag: The DAG to which all relevant "Aggregations" operators should be added
        :type aggregations_dag: airflow.models.DAG
        :return: The input DAG, after the "Aggregations" operators were added
        :rtype: airflow.models.DAG
        """

        task_sensor_service = TaskSensorService()

        feature_aggregations_operator = FeatureAggregationsOperator(
            fixed_duration_strategy=self.fixed_duration_strategy,
            command=PresidioDagBuilder.presidio_command,
            data_source=self.data_source,
            dag=aggregations_dag,
            condition=self.condition
        )

        score_aggregations_operator = ScoreAggregationsOperator(
            fixed_duration_strategy=self.fixed_duration_strategy,
            command=PresidioDagBuilder.presidio_command,
            data_source=self.data_source,
            dag=aggregations_dag,
            condition=self.condition
        )

        task_sensor_service.add_task_sequential_sensor(feature_aggregations_operator)
        task_sensor_service.add_task_sequential_sensor(score_aggregations_operator)
        return aggregations_dag
