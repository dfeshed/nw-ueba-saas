from datetime import timedelta

from presidio.builders.indicator.adapter_operator_builder import AdapterOperatorBuilder
from presidio.builders.indicator.feature_aggregations_operator_builder import FeatureAggregationsOperatorBuilder
from presidio.builders.indicator.input_operator_builder import InputOperatorBuilder
from presidio.builders.indicator.score_aggregations_operator_builder import ScoreAggregationsOperatorBuilder
from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.builders.indicator.input_retention_operator_builder import InputRetentionOperatorBuilder
from presidio.builders.smart_model.smart_model_accumulate_operator_builder import SmartModelAccumulateOperatorBuilder
from presidio.factories.model_dag_factory import ModelDagFactory
from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService
from presidio.utils.airflow.schedule_interval_utils import get_schedule_interval, set_schedule_interval
from presidio.utils.services.fixed_duration_strategy import is_execution_date_valid, FIX_DURATION_STRATEGY_DAILY, \
    FIX_DURATION_STRATEGY_HOURLY


class IndicatorDagBuilder(PresidioDagBuilder):

    def __init__(self):
        # currently we start scoring only when smart models start accumulating.
        # later we will probably run scoring according to the specific modeling configuration.
        self._min_gap_from_dag_start_date_to_start_scoring = SmartModelAccumulateOperatorBuilder.get_min_gap_from_dag_start_date_to_start_accumulating(PresidioDagBuilder.conf_reader)

    def build(self, dag):
        """
        Receives an indicator DAG, creates the adapter, input and scoring operators, links them to the DAG and
        configures the dependencies between them.
        :param dag: The indicator DAG to populate
        :type dag: airflow.models.DAG
        :return: The given indicator DAG, after it has been populated
        :rtype: airflow.models.DAG
        """

        task_sensor_service = TaskSensorService()
        schema = dag.default_args.get('schema')

        adapter_operator = AdapterOperatorBuilder(schema).build(dag)
        input_operator = InputOperatorBuilder(schema).build(dag)
        feature_aggregations_operator = FeatureAggregationsOperatorBuilder(schema, FIX_DURATION_STRATEGY_HOURLY).build(
            dag, task_sensor_service)
        score_aggregations_operator = ScoreAggregationsOperatorBuilder(schema, FIX_DURATION_STRATEGY_HOURLY).build(dag,
                                                                                                                   task_sensor_service)

        hourly_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='ade_scoring_hourly_short_circuit',
            dag=dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     self.fixed_duration,
                                                                     get_schedule_interval(dag)) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 dag,
                                                 self._min_gap_from_dag_start_date_to_start_scoring,
                                                 kwargs['execution_date'],
                                                 get_schedule_interval(
                                                     dag))
        )

        adapter_operator >> input_operator >> hourly_short_circuit_operator
        task_sensor_service.add_task_short_circuit(feature_aggregations_operator, hourly_short_circuit_operator)
        task_sensor_service.add_task_short_circuit(score_aggregations_operator, hourly_short_circuit_operator)

        model_trigger = self._build_model_trigger_operator(dag, schema)
        feature_aggregations_operator >> model_trigger
        score_aggregations_operator >> model_trigger

        self._build_input_retention(dag, schema)

        return dag

    def _build_model_trigger_operator(self, dag, schema):
        model_dag_id = ModelDagFactory.get_dag_id(schema)
        python_callable = lambda context, dag_run_obj: dag_run_obj if is_execution_date_valid(context['execution_date'],
                                                                                              FIX_DURATION_STRATEGY_DAILY,
                                                                                              get_schedule_interval(
                                                                                                  dag)) else None
        model_trigger = self._create_expanded_trigger_dag_run_operator('{0}_{1}'.format(schema, 'model_trigger_dagrun'),
                                                                       model_dag_id, dag, python_callable)

        set_schedule_interval(model_dag_id, FIX_DURATION_STRATEGY_DAILY)
        return model_trigger

    def _build_input_retention(self, dag, schema):
        input_retention_operator = InputRetentionOperatorBuilder(schema).build(dag)
        input_retention_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='input_retention_short_circuit',
            dag=dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     InputRetentionOperatorBuilder.get_input_retention_interval_in_hours(PresidioDagBuilder.conf_reader),
                                                                     get_schedule_interval(dag)) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 dag,
                                                 timedelta(
                                                     days=InputRetentionOperatorBuilder.get_input_min_time_to_start_retention_in_days(PresidioDagBuilder.conf_reader)),
                                                 kwargs['execution_date'],
                                                 get_schedule_interval(dag)))
        input_retention_short_circuit_operator >> input_retention_operator
