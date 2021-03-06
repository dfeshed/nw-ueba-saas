from datetime import timedelta

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.builders.smart.alert_retention_operator_builder import AlertRetentionOperatorBuilder
from presidio.builders.smart.entity_score_operator_builder import EntityScoreOperatorBuilder
from presidio.builders.smart_model.smart_model_accumulate_operator_builder import SmartModelAccumulateOperatorBuilder
from presidio.factories.indicator_dag_factory import IndicatorDagFactory
from presidio.factories.smart_model_dag_factory import SmartModelDagFactory
from presidio.operators.output.output_operator import OutputOperator
from presidio.operators.output.output_forwarder_operator import OutputForwarderOperator
from presidio.operators.smart.smart_events_operator import SmartEventsOperator
from presidio.utils.airflow.operators.sensor.root_dag_gap_sequential_sensor_operator import \
    RootDagGapSequentialSensorOperator
from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService
from presidio.utils.airflow.schedule_interval_utils import set_schedule_interval
from presidio.utils.services.fixed_duration_strategy import FIX_DURATION_STRATEGY_HOURLY, is_execution_date_valid, \
    FIX_DURATION_STRATEGY_DAILY


class SmartDagBuilder(PresidioDagBuilder):
    """
   The "Smart" builder consists of smart, output, ade_manager and output_retention
    """

    def __init__(self):
        # currently the output should start when smart scoring starts.
        self._min_gap_from_dag_start_date_to_start_scoring = SmartModelAccumulateOperatorBuilder.get_min_gap_from_dag_start_date_to_start_accumulating(
            PresidioDagBuilder.conf_reader)

    def build(self, dag):
        root_dag_gap_sensor_operator = self._build_root_dag_gap_sensor_operator(dag)

        smart_record_conf_name = dag.default_args.get("smart_conf_name")
        entity_type = dag.default_args.get("entity_type")
        smart_operator = self._build_smart(root_dag_gap_sensor_operator, dag, smart_record_conf_name)
        entity_score_operator = self._build_output_operator(smart_record_conf_name, entity_type, dag, smart_operator)
        self._build_alert_retention_operator(dag, entity_score_operator, entity_type)
        return dag

    def _build_root_dag_gap_sensor_operator(self, smart_dag):
        schemas = smart_dag.default_args.get("depends_on_schemas")
        dag_ids = []
        for schema in schemas:
            dag_id = IndicatorDagFactory.get_dag_id(schema)
            dag_ids.append(dag_id)

        root_dag_gap_sensor_operator = RootDagGapSequentialSensorOperator(dag=smart_dag,
                                                                          task_id='indicator_root_gap_sensor',
                                                                          dag_ids=dag_ids,
                                                                          poke_interval=5)
        return root_dag_gap_sensor_operator

    def _build_smart(self, root_dag_gap_sensor_operator, smart_dag, smart_record_conf_name):
        task_sensor_service = TaskSensorService()
        smart_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='ade_scoring_hourly_short_circuit',
            dag=smart_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     FIX_DURATION_STRATEGY_HOURLY,
                                                                     smart_dag.schedule_interval) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 smart_dag,
                                                 self._min_gap_from_dag_start_date_to_start_scoring,
                                                 kwargs['execution_date'],
                                                 smart_dag.schedule_interval)
        )

        smart_operator = SmartEventsOperator(
            command=SmartEventsOperator.liors_special_run_command,
            fixed_duration_strategy=FIX_DURATION_STRATEGY_HOURLY,
            smart_events_conf=smart_record_conf_name,
            dag=smart_dag,
        )
        task_sensor_service.add_task_sequential_sensor(smart_operator)
        task_sensor_service.add_task_short_circuit(smart_operator, smart_short_circuit_operator)

        root_dag_gap_sensor_operator >> smart_short_circuit_operator

        smart_model_dag_id = SmartModelDagFactory.get_dag_id(smart_record_conf_name)

        python_callable = lambda context, dag_run_obj: dag_run_obj if is_execution_date_valid(context['execution_date'],
                                                                                              FIX_DURATION_STRATEGY_DAILY,
                                                                                              smart_dag.schedule_interval) else None
        smart_model_trigger = self._create_expanded_trigger_dag_run_operator("smart_model_trigger",
                                                                             smart_model_dag_id, smart_dag,
                                                                             python_callable)

        set_schedule_interval(smart_model_dag_id, FIX_DURATION_STRATEGY_DAILY)
        smart_operator >> smart_model_trigger
        return smart_operator

    def _build_output_operator(self, smart_record_conf_name, entity_type, dag, smart_operator):

        self.log.debug("populating the %s dag with output tasks", dag.dag_id)

        # build hourly output processor
        task_sensor_service = TaskSensorService()
        # This operator validates that output run in intervals that are no less than hourly intervals and that the dag
        # start only after the defined gap.
        output_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='output_short_circuit',
            dag=dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     FIX_DURATION_STRATEGY_HOURLY,
                                                                     dag.schedule_interval) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 dag,
                                                 self._min_gap_from_dag_start_date_to_start_scoring,
                                                 kwargs['execution_date'],
                                                 dag.schedule_interval))

        hourly_output_operator = OutputOperator(
            fixed_duration_strategy=timedelta(hours=1),
            command=PresidioDagBuilder.presidio_command,
            smart_record_conf_name=smart_record_conf_name,
            entity_type=entity_type,
            dag=dag,
        )
        task_sensor_service.add_task_sequential_sensor(hourly_output_operator)
        task_sensor_service.add_task_short_circuit(hourly_output_operator, output_short_circuit_operator)

        # build entity score
        entity_score_operator = EntityScoreOperatorBuilder(smart_record_conf_name, entity_type).build(dag)
        # Create daily short circuit operator to wire the output processing and the entity score recalculation
        daily_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='output_daily_short_circuit',
            dag=dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     FIX_DURATION_STRATEGY_DAILY,
                                                                     dag.schedule_interval) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 dag,
                                                 EntityScoreOperatorBuilder.get_min_gap_from_dag_start_date_to_start_modeling(
                                                     PresidioDagBuilder.conf_reader),
                                                 kwargs['execution_date'],
                                                 dag.schedule_interval)
        )

        daily_short_circuit_operator >> entity_score_operator
        self._push_forwarding(hourly_output_operator, daily_short_circuit_operator, dag, entity_type)

        smart_operator >> output_short_circuit_operator

        return entity_score_operator

    def _push_forwarding(self, hourly_output_operator, daily_short_circuit_operator, dag, entity_type):
        self.log.debug("creating the forwarder task")

        default_args = dag.default_args
        enable_output_forwarder = default_args.get("enable_output_forwarder")
        self.log.debug("enable_output_forwarder=%s ", enable_output_forwarder)
        if enable_output_forwarder == 'true':
            push_forwarding_operator = OutputForwarderOperator(
                command=PresidioDagBuilder.presidio_command,
                entity_type=entity_type,
                run_clean_command_before_retry=False,
                dag=dag)

            output_forward_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
                task_id='output_forward_short_circuit',
                dag=dag,
                python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                         FIX_DURATION_STRATEGY_HOURLY,
                                                                         dag.schedule_interval) &
                                                 PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                     dag,
                                                     EntityScoreOperatorBuilder.get_min_gap_from_dag_start_date_to_start_modeling(
                                                         PresidioDagBuilder.conf_reader),
                                                     kwargs['execution_date'],
                                                     dag.schedule_interval)
            )

            hourly_output_operator >> output_forward_short_circuit_operator >> push_forwarding_operator >> daily_short_circuit_operator
        else:
            hourly_output_operator >> daily_short_circuit_operator

    def _build_alert_retention_operator(self, dag, entity_score_operator, entity_type):
        alert_retention_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='alert_retention_short_circuit',
            dag=dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     AlertRetentionOperatorBuilder.get_alert_retention_interval_in_hours(
                                                                         PresidioDagBuilder.conf_reader),
                                                                     dag.schedule_interval) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 dag,
                                                 timedelta(
                                                     days=AlertRetentionOperatorBuilder.get_alert_min_time_to_start_retention_in_days(
                                                         PresidioDagBuilder.conf_reader)),
                                                 kwargs['execution_date'],
                                                 dag.schedule_interval))

        alert_retention = AlertRetentionOperatorBuilder().build(dag, entity_type)

        entity_score_operator >> alert_retention_short_circuit_operator >> alert_retention
