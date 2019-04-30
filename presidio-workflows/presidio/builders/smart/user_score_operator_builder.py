from datetime import timedelta

from airflow import LoggingMixin

from presidio.builders.smart_model.smart_model_operator_builder import SmartModelOperatorBuilder
from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton

OUTPUT_JVM_ARGS_CONFIG_PATH = 'components.output.jvm_args'
OUTPUT_RUN_DAILY_COMMAND = 'recalculate-user-score'


class UserScoreOperatorBuilder(LoggingMixin):
    """
    The "UserScoreOperatorBuilder" builds and returns user_score_operator according to the
    given attributes (smart_record_conf_name).
    """

    conf_reader = ConfigServerConfigurationReaderSingleton().config_reader

    def __init__(self, smart_record_conf_name):
        """
        C'tor.
        :param smart_record_conf_name: smart_record_conf_name we should work on
        :type smart_record_conf_name: str
        """

        self.smart_record_conf_name = smart_record_conf_name
        self.jvm_args = UserScoreOperatorBuilder.conf_reader.read(conf_key=OUTPUT_JVM_ARGS_CONFIG_PATH)

    @staticmethod
    def get_min_gap_from_dag_start_date_to_start_modeling(conf_reader):
        return SmartModelOperatorBuilder.get_min_gap_from_dag_start_date_to_start_modeling(conf_reader);

    def build(self, smart_dag):
        """
        Builds user_score_operator.
        :param smart_dag: The DAG to which all relevant "output" operators should be added
        :type smart_dag: airflow.models.DAG
        :return: The smart DAG, after the "output" operators were added
        :rtype: presidio.operators.fixed_duration_jar_operator.FixedDurationJarOperator
        """

        self.log.debug("populating the %s dag with output tasks", smart_dag.dag_id)

        java_args = {
            'smart_record_conf_name': self.smart_record_conf_name,
        }

        user_score_operator = FixedDurationJarOperator(
            task_id='user_score_processor',
            fixed_duration_strategy=timedelta(days=1),
            command=OUTPUT_RUN_DAILY_COMMAND,
            jvm_args=self.jvm_args,
            dag=smart_dag,
            java_args=java_args
        )

        return user_score_operator
