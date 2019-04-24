from datetime import timedelta

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.builders.smart.push_forwarder_task_builder import PushForwarderTaskBuilder
from presidio.builders.smart_model.smart_model_dag_builder import SmartModelDagBuilder
from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator
from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton
from presidio.utils.services.fixed_duration_strategy import is_execution_date_valid, FIX_DURATION_STRATEGY_DAILY, \
    FIX_DURATION_STRATEGY_HOURLY

OUTPUT_JVM_ARGS_CONFIG_PATH = 'components.output.jvm_args'
OUTPUT_RUN_DAILY_COMMAND = 'recalculate-user-score'


class OutputDagBuilder(PresidioDagBuilder):
    """
    An "Output DAG" builder - The "Output DAG" consists of multiple tasks / operators.
    The builder accepts the DAG's attributes through the c'tor, and the "build" method builds and
    returns the DAG according to the given attributes.
    """

    conf_reader = ConfigServerConfigurationReaderSingleton().config_reader

    def __init__(self, smart_record_conf_name):
        """
        C'tor.
        :param smart_record_conf_name: smart_record_conf_name we should work on
        :type smart_record_conf_name: str
        """

        self.smart_record_conf_name = smart_record_conf_name
        self.jvm_args = OutputDagBuilder.conf_reader.read(conf_key=OUTPUT_JVM_ARGS_CONFIG_PATH)

        # currently the output should start when smart scoring starts.
        self._min_gap_from_dag_start_date_to_start_scoring = SmartModelDagBuilder.get_min_gap_from_dag_start_date_to_start_accumulating(
            OutputDagBuilder.conf_reader)

        # the daily output job should start when there are smarts in the system (after start modeling)
        self._min_gap_from_dag_start_date_to_start_modeling = SmartModelDagBuilder.get_min_gap_from_dag_start_date_to_start_modeling(
            OutputDagBuilder.conf_reader)

    def build(self, smart_dag):
        """
        Builds output jar operators and adds them to the given DAG.
        :param smart_dag: The DAG to which all relevant "output" operators should be added
        :type smart_dag: airflow.models.DAG
        :return: The smart DAG, after the "output" operators were added
        :rtype: airflow.models.DAG
        """

        self.log.debug("populating the %s dag with output tasks", smart_dag.dag_id)

        task_sensor_service = TaskSensorService()

        # This operator validates that output run in intervals that are no less than hourly intervals and that the dag
        # start only after the defined gap.
        output_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='output_short_circuit',
            dag=smart_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     FIX_DURATION_STRATEGY_HOURLY,
                                                                     smart_dag.schedule_interval) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 smart_dag,
                                                 self._min_gap_from_dag_start_date_to_start_scoring,
                                                 kwargs['execution_date'],
                                                 smart_dag.schedule_interval))

        java_args = {
            'smart_record_conf_name': self.smart_record_conf_name,
        }

        # Create output jar operators
        hourly_output_operator = FixedDurationJarOperator(
            task_id='hourly_output_processor',
            fixed_duration_strategy=timedelta(hours=1),
            command=PresidioDagBuilder.presidio_command,
            jvm_args=self.jvm_args,
            dag=smart_dag,
            java_args=java_args
        )

        task_sensor_service.add_task_sequential_sensor(hourly_output_operator)
        task_sensor_service.add_task_short_circuit(hourly_output_operator, output_short_circuit_operator)

        # Create daily short circuit operator to wire the output processing and the user score recalculation
        daily_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='output_daily_short_circuit',
            dag=smart_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     FIX_DURATION_STRATEGY_DAILY,
                                                                     smart_dag.schedule_interval) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 smart_dag,
                                                 self._min_gap_from_dag_start_date_to_start_modeling,
                                                 kwargs['execution_date'],
                                                 smart_dag.schedule_interval)
        )

        user_score_operator = FixedDurationJarOperator(
            task_id='user_score_processor',
            fixed_duration_strategy=timedelta(days=1),
            command=OUTPUT_RUN_DAILY_COMMAND,
            jvm_args=self.jvm_args,
            dag=smart_dag,
            java_args=java_args
        )

        daily_short_circuit_operator >> user_score_operator
        self._push_forwarding(hourly_output_operator, daily_short_circuit_operator, smart_dag)

        return smart_dag

    def _push_forwarding(self, hourly_output_operator, daily_short_circuit_operator, smart_dag):
        default_args = smart_dag.default_args
        enable_output_forwarder = default_args.get("enable_output_forwarder")
        self.log.debug("enable_output_forwarder=%s ", enable_output_forwarder)
        if enable_output_forwarder == 'true':
            push_forwarding_task = PushForwarderTaskBuilder().build(smart_dag)
            hourly_output_operator >> push_forwarding_task >> daily_short_circuit_operator
        else:
            hourly_output_operator >> daily_short_circuit_operator
