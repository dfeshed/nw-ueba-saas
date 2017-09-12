import logging
from datetime import timedelta

from airflow.operators.python_operator import ShortCircuitOperator

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator
from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton
from presidio.utils.services.fixed_duration_strategy import is_execution_date_valid, FIX_DURATION_STRATEGY_DAILY

OUTPUT_JVM_ARGS_CONFIG_PATH = 'components.output.jvm_args'
OUTPUT_RUN_DAILY_COMMAND = 'recalculate-user-score'


class OutputDagBuilder(PresidioDagBuilder):
    """
    An "Output DAG" builder - The "Output DAG" consists of multiple tasks / operators one per data source.
    The builder accepts the DAG's attributes through the c'tor, and the "build" method builds and
    returns the DAG according to the given attributes.
    """

    conf_reader = ConfigServerConfigurationReaderSingleton().config_server_reader

    def __init__(self, data_sources):
        """
        C'tor.
        :param data_source: The data source whose events we should work on
        :type data_source: str
        """

        self.data_sources = data_sources
        self.jvm_args = OutputDagBuilder.conf_reader.read(conf_key=OUTPUT_JVM_ARGS_CONFIG_PATH)

    def build(self, output_dag):
        """
        Builds jar operators for each data source and adds them to the given DAG.
        :param output_dag: The DAG to which all relevant "output" operators should be added
        :type output_dag: airflow.models.DAG
        :return: The input DAG, after the "output" operators were added
        :rtype: airflow.models.DAG
        """

        logging.info("populating the output dag, dag_id=%s ", output_dag.dag_id)

        # Create jar operators
        hourly_output_operator = FixedDurationJarOperator(
            task_id='hourly_output_processor',
            fixed_duration_strategy=timedelta(hours=1),
            command=PresidioDagBuilder.presidio_command,
            jvm_args=self.jvm_args,
            dag=output_dag)

        user_score_operator = FixedDurationJarOperator(
            task_id='user_score_processor',
            fixed_duration_strategy=timedelta(days=1),
            command=OUTPUT_RUN_DAILY_COMMAND,
            jvm_args=self.jvm_args,
            dag=output_dag)

        task_sensor_service = TaskSensorService()

        # Create daily short circuit operator to wire the output processing and the user score recalculation
        daily_short_circuit_operator = ShortCircuitOperator(
            task_id='output_daily_short_circuit',
            dag=output_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     FIX_DURATION_STRATEGY_DAILY,
                                                                     output_dag.schedule_interval),
            provide_context=True
        )

        task_sensor_service.add_task_short_circuit(user_score_operator, daily_short_circuit_operator)

        #defining the dependencies between the operators
        hourly_output_operator >> daily_short_circuit_operator

        return output_dag
