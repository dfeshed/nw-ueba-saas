from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.builders.smart_model.smart_model_dag_builder import SmartModelDagBuilder
from presidio.operators.aggregation.feature_aggregations_operator import FeatureAggregationsOperator
from presidio.operators.aggregation.score_aggregations_operator import ScoreAggregationsOperator
from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService
from presidio.utils.airflow.schedule_interval_utils import get_schedule_interval
from presidio.utils.services.fixed_duration_strategy import is_execution_date_valid


class AggregationsDagBuilder(PresidioDagBuilder):
    """
    An "Aggregations DAG" builder - The "Aggregations DAG" consists of 2 tasks / operators
    (feature aggregations and score aggregations) and can be either an hourly DAG or a daily DAG.
    The builder accepts the DAG's attributes through the c'tor, and the "build" method builds and
    returns the DAG according to the given attributes.
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

        # currently we start scoring only when smart models start accumulating.
        # later we will probably run scoring according to the specific modeling configuration.
        self._min_gap_from_dag_start_date_to_start_scoring = SmartModelDagBuilder.get_min_gap_from_dag_start_date_to_start_accumulating(
            AggregationsDagBuilder.conf_reader)

    def build(self, indicator_dag):
        """
        Builds the "Feature Aggregations" operator, the "Score Aggregations" operator and adds them to the given DAG.
        :param indicator_dag: The DAG to which all relevant "Aggregations" operators should be added
        :type indicator_dag: airflow.models.DAG
        :return: The indicator DAG, after the "Aggregations" operators were added
        :rtype: airflow.models.DAG
        """
        self.log.debug("populating the %s dag with scoring tasks", indicator_dag.dag_id)

        task_sensor_service = TaskSensorService()
        hourly_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='ade_scoring_hourly_short_circuit',
            dag=indicator_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     self.fixed_duration,
                                                                     get_schedule_interval(indicator_dag)) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 indicator_dag,
                                                 self._min_gap_from_dag_start_date_to_start_scoring,
                                                 kwargs['execution_date'],
                                                 get_schedule_interval(
                                                     indicator_dag))
        )

        feature_aggregations_operator = FeatureAggregationsOperator(
            fixed_duration_strategy=self.fixed_duration,
            command=PresidioDagBuilder.presidio_command,
            data_source=self.schema,
            dag=indicator_dag)

        task_sensor_service.add_task_sequential_sensor(feature_aggregations_operator)
        task_sensor_service.add_task_short_circuit(feature_aggregations_operator, hourly_short_circuit_operator)

        score_aggregations_operator = ScoreAggregationsOperator(
            fixed_duration_strategy=self.fixed_duration,
            command=PresidioDagBuilder.presidio_command,
            data_source=self.schema,
            dag=indicator_dag)

        task_sensor_service.add_task_sequential_sensor(score_aggregations_operator)
        task_sensor_service.add_task_short_circuit(score_aggregations_operator, hourly_short_circuit_operator)

        return indicator_dag
