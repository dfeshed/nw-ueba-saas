from datetime import timedelta

from presidio.builders.smart.output_operator_builder import OutputOperatorBuilder
from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.builders.smart.output_retention_operator_builder import OutputRetentionOperatorBuilder
from presidio.builders.smart.push_forwarder_task_builder import PushForwarderTaskBuilder
from presidio.builders.smart.user_score_operator_builder import UserScoreOperatorBuilder
from presidio.builders.smart_model.smart_model_accumulate_operator_builder import SmartModelAccumulateOperatorBuilder
from presidio.factories.indicator_dag_factory import IndicatorDagFactory
from presidio.factories.smart_model_dag_factory import SmartModelDagFactory
from presidio.operators.ade_manager.ade_manager_operator import AdeManagerOperator
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
        smart_operator = self._build_smart(root_dag_gap_sensor_operator, dag, smart_record_conf_name)
        self._build_output_operator(smart_record_conf_name, dag, smart_operator)
        self._build_ade_manager_operator(dag, root_dag_gap_sensor_operator)
        self._build_output_retention_operator(dag)
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

    def _build_output_operator(self, smart_record_conf_name, dag, smart_operator):

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

        hourly_output_operator = OutputOperatorBuilder(smart_record_conf_name).build(dag, task_sensor_service)

        task_sensor_service.add_task_short_circuit(hourly_output_operator, output_short_circuit_operator)

        # build user score
        user_score_operator = UserScoreOperatorBuilder(smart_record_conf_name).build(dag)
        # Create daily short circuit operator to wire the output processing and the user score recalculation
        daily_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='output_daily_short_circuit',
            dag=dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     FIX_DURATION_STRATEGY_DAILY,
                                                                     dag.schedule_interval) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 dag,
                                                 UserScoreOperatorBuilder.get_min_gap_from_dag_start_date_to_start_modeling(
                                                     PresidioDagBuilder.conf_reader),
                                                 kwargs['execution_date'],
                                                 dag.schedule_interval)
        )

        daily_short_circuit_operator >> user_score_operator
        self._push_forwarding(hourly_output_operator, daily_short_circuit_operator, dag)

        smart_operator >> output_short_circuit_operator

    def _push_forwarding(self, hourly_output_operator, daily_short_circuit_operator, smart_dag):
        default_args = smart_dag.default_args
        enable_output_forwarder = default_args.get("enable_output_forwarder")
        self.log.debug("enable_output_forwarder=%s ", enable_output_forwarder)
        if enable_output_forwarder == 'true':
            push_forwarding_task = PushForwarderTaskBuilder().build(smart_dag)
            hourly_output_operator >> push_forwarding_task >> daily_short_circuit_operator
        else:
            hourly_output_operator >> daily_short_circuit_operator

    def _build_ade_manager_operator(self, smart_dag, root_dag_gap_sensor_operator):
        """
        Create AdeManagerOperator in order to clean enriched data after all enriched data customer tasks finished to use it.
        Set daily_short_circuit in order to run AdeManagerOperator once a day.

        AdeManagerOperator instances do not run sequentially (AdeManagerOperator does not use sequential sensor)
         as a result of following assumption: AdeManagerOperator should run once a day (using daily_short_circuit),
         all instances will get skip status except the last instance, therefore sequential sensor is unnecessary.


        :param smart_dag: The smart DAG
        :type smart_dag: airflow.models.DAG
        :param root_dag_gap_sensor_operator: validate that all indicator dags finished to run
        """

        daily_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='ade_modeling_daily_short_circuit',
            dag=smart_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     FIX_DURATION_STRATEGY_DAILY,
                                                                     smart_dag.schedule_interval)
        )

        ade_manager_operator = AdeManagerOperator(
            command=AdeManagerOperator.enriched_ttl_cleanup_command,
            dag=smart_dag)

        daily_short_circuit_operator >> ade_manager_operator
        root_dag_gap_sensor_operator >> daily_short_circuit_operator

    def _build_output_retention_operator(self, dag):
        output_retention_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='output_retention_short_circuit',
            dag=dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     OutputRetentionOperatorBuilder.get_output_retention_interval_in_hours(
                                                                         PresidioDagBuilder.conf_reader),
                                                                     dag.schedule_interval) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 dag,
                                                 timedelta(
                                                     days=OutputRetentionOperatorBuilder.get_output_min_time_to_start_retention_in_days(
                                                         PresidioDagBuilder.conf_reader)),
                                                 kwargs['execution_date'],
                                                 dag.schedule_interval))

        output_retention = OutputRetentionOperatorBuilder().build(dag)
        output_retention_short_circuit_operator >> output_retention
