from datetime import timedelta, datetime
from airflow.operators.python_operator import ShortCircuitOperator

from presidio.builders.presidio_dag_builder import PresidioDagBuilder

from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator
from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton
from presidio.utils.services.fixed_duration_strategy import is_execution_date_valid, FIX_DURATION_STRATEGY_DAILY

USER_SCORE_RECALCULATION_JAM_ARGS_CONFIG_PATH = 'components.user_score_recalculation.jvm_args'


class RecalculateUserScoreDagBuilder(PresidioDagBuilder):
    """
    "Recalculate user score DAG" builder -
    The "Recalculate user score DAG" consists of one sub dag followed by operator
    The sub dag responsible for recalculating the user score
    The DAG will recalculate the user score after day is over using logical time.

    The time period which the user score will be calculate for will be taken from the configuration server
    """

    conf_reader = ConfigServerConfigurationReaderSingleton().config_server_reader

    def __init__(self):
        """
        C'tor.
        """
        self._user_score_interval = timedelta(days=1)
        self._jvm_args = RecalculateUserScoreDagBuilder.conf_reader.read(
            conf_key=USER_SCORE_RECALCULATION_JAM_ARGS_CONFIG_PATH)

    def build(self, user_score_dag):
        """
        Builds the "User Score" sub dag.
        :param user_score_dag: The DAG to which the operator flow should be added.
        :return: The input DAG, after the operator flow was added
        :rtype: airflow.models.DAG
        """
        task_sensor_service = TaskSensorService()

        # defining the User Score operator
        user_score_operator = FixedDurationJarOperator(
            task_id='user_score_recalculation_{}'.format(datetime.now()),
            fixed_duration_strategy=FIX_DURATION_STRATEGY_DAILY,
            command=PresidioDagBuilder.presidio_command,
            jvm_args=self._jvm_args,
            dag=user_score_dag)
        user_score_short_circuit_operator = ShortCircuitOperator(
            task_id='user_score_short_circuit',
            dag=user_score_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     self._user_score_interval,
                                                                     user_score_dag.schedule_interval),
            provide_context=True
        )
        task_sensor_service.add_task_short_circuit(user_score_operator, user_score_short_circuit_operator)

        return user_score_dag
