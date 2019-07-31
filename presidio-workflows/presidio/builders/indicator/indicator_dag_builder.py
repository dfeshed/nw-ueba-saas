from datetime import timedelta

from presidio.builders.indicator.adapter_operator_builder import AdapterOperatorBuilder
from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.builders.smart_model.smart_model_accumulate_operator_builder import SmartModelAccumulateOperatorBuilder
from presidio.factories.input_pre_processing_dag_factory import InputPreProcessorDagFactory
from presidio.factories.model_dag_factory import ModelDagFactory
from presidio.operators.aggregation.feature_aggregations_operator import FeatureAggregationsOperator
from presidio.operators.aggregation.score_aggregations_operator import ScoreAggregationsOperator
from presidio.operators.input.input_operator import InputOperator
from presidio.utils.airflow.operators.sensor.root_dag_gap_sequential_sensor_operator import \
    RootDagGapSequentialSensorOperator
from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService
from presidio.utils.airflow.schedule_interval_utils import get_schedule_interval, set_schedule_interval
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton
from presidio.utils.services.fixed_duration_strategy import is_execution_date_valid, FIX_DURATION_STRATEGY_DAILY, \
    FIX_DURATION_STRATEGY_HOURLY, is_execution_date_valid_first_interval


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
        self.log.debug("populating the %s dag with input tasks", dag.dag_id)
        schema = dag.default_args.get('schema')

        adapter_operator = AdapterOperatorBuilder(schema).build(dag)

        input_task_sensor_service = TaskSensorService()
        input_operator = InputOperator(
            fixed_duration_strategy=timedelta(hours=1),
            command=PresidioDagBuilder.presidio_command,
            schema=schema,
            dag=dag)
        input_task_sensor_service.add_task_sequential_sensor(input_operator)

        self.log.debug("populating the %s dag with scoring tasks", dag.dag_id)
        scoring_task_sensor_service = TaskSensorService()
        feature_aggregations_operator = FeatureAggregationsOperator(
            fixed_duration_strategy=FIX_DURATION_STRATEGY_HOURLY,
            command=PresidioDagBuilder.presidio_command,
            data_source=schema,
            dag=dag)
        scoring_task_sensor_service.add_task_sequential_sensor(feature_aggregations_operator)

        score_aggregations_operator = ScoreAggregationsOperator(
            fixed_duration_strategy=FIX_DURATION_STRATEGY_HOURLY,
            command=PresidioDagBuilder.presidio_command,
            data_source=schema,
            dag=dag)
        scoring_task_sensor_service.add_task_sequential_sensor(score_aggregations_operator)

        hourly_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='ade_scoring_hourly_short_circuit',
            dag=dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     FIX_DURATION_STRATEGY_HOURLY,
                                                                     get_schedule_interval(dag)) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 dag,
                                                 self._min_gap_from_dag_start_date_to_start_scoring,
                                                 kwargs['execution_date'],
                                                 get_schedule_interval(
                                                     dag))
        )

        if InputPreProcessorDagFactory.get_dag_id(schema) in self.get_list_schemas_for_input_pre_processing():
            input_pre_processor_trigger = self._build_input_pre_processing_trigger_operator(dag, schema)
            input_operator >> input_pre_processor_trigger

            input_pre_processor_gap_sensor = RootDagGapSequentialSensorOperator(dag=dag,
                                                                                task_id='input_pre_processor_gap_sensor_{0}'.format(schema),
                                                                                dag_ids=[InputPreProcessorDagFactory.get_dag_id(schema)],
                                                                                interval=timedelta(hours=1),
                                                                                start_time=dag.start_date,
                                                                                fixed_duration_strategy=FIX_DURATION_STRATEGY_DAILY,
                                                                                poke_interval=5)

            input_pre_processor_gap_sensor >> input_operator

        adapter_operator >> input_operator >> hourly_short_circuit_operator
        scoring_task_sensor_service.add_task_short_circuit(feature_aggregations_operator, hourly_short_circuit_operator)
        scoring_task_sensor_service.add_task_short_circuit(score_aggregations_operator, hourly_short_circuit_operator)

        model_trigger = self._build_model_trigger_operator(dag, schema)
        feature_aggregations_operator >> model_trigger
        score_aggregations_operator >> model_trigger

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

    def _build_input_pre_processing_trigger_operator(self, dag, schema):
        input_pre_processing_dag_id = InputPreProcessorDagFactory.get_dag_id(schema)

        python_callable = lambda context, dag_run_obj: dag_run_obj if is_execution_date_valid(context['execution_date'],
                                                                                              FIX_DURATION_STRATEGY_DAILY,
                                                                                              get_schedule_interval(
                                                                                                  dag)) else None
        input_pre_processor_trigger = self._create_expanded_trigger_dag_run_operator("{0}_input_pre_processor_trigger_dagrun".format(schema),
                                                                                     input_pre_processing_dag_id, dag, python_callable)

        return input_pre_processor_trigger

    @staticmethod
    def get_list_schemas_for_input_pre_processing():
        return InputPreProcessorDagFactory.get_registered_dag_ids()

