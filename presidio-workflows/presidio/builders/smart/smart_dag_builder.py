from presidio.builders.smart.output_dag_builder import OutputDagBuilder
from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.builders.retention.output.output_retention_dag_builder import OutputRetentionDagBuilder
from presidio.builders.smart_model.smart_model_dag_builder import SmartModelDagBuilder
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
   The "Smart" builder consists of smart, smart_model and ade_manager
    """

    def __init__(self):
        self._min_gap_from_dag_start_date_to_start_scoring = SmartModelDagBuilder.get_min_gap_from_dag_start_date_to_start_accumulating(
            SmartDagBuilder.conf_reader)

    def build(self, smart_dag):
        root_dag_gap_sensor_operator = self._build_root_dag_gap_sensor_operator(smart_dag)
        smart_record_conf_name = smart_dag.default_args.get("smart_conf_name")

        output_operator = self._get_output_operator(smart_record_conf_name, smart_dag)
        self._build_smart(root_dag_gap_sensor_operator, output_operator, smart_dag, smart_record_conf_name)
        self._build_ade_manager_operator(smart_dag, root_dag_gap_sensor_operator)
        self._get_output_retention_operator(smart_record_conf_name, smart_dag)
        self.remove_multi_point_group_container(smart_dag)
        return smart_dag

    def _get_output_operator(self, smart_record_conf_name, smart_dag):
        output_operator_id = '{}_output_operator'.format(smart_record_conf_name)
        return self._create_multi_point_group_connector(OutputDagBuilder(smart_record_conf_name), smart_dag,
                                                        output_operator_id, None, False)

    def _get_output_retention_operator(self, smart_record_conf_name, smart_dag):
        output_retention_operator_id = '{}_output_retention_operator'.format(smart_record_conf_name)
        return self._create_multi_point_group_connector(OutputRetentionDagBuilder(smart_record_conf_name), smart_dag,
                                                        output_retention_operator_id, None, False)

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

    def _build_smart(self, root_dag_gap_sensor_operator, output_operator, smart_dag, smart_record_conf_name):
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
        smart_operator >> output_operator

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
