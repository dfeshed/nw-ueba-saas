from datetime import timedelta

from airflow import LoggingMixin

from presidio.builders.smart_model.smart_model_operator_builder import SmartModelOperatorBuilder
from presidio.operators.output.entity_score_operator import EntityScoreOperator
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton

OUTPUT_RUN_DAILY_COMMAND = 'recalculate-entity-score'


class EntityScoreOperatorBuilder(LoggingMixin):
    """
    The "EntityScoreOperatorBuilder" builds and returns entity_score_operator according to the
    given attributes (smart_record_conf_name).
    """

    conf_reader = ConfigServerConfigurationReaderSingleton().config_reader

    def __init__(self, smart_record_conf_name, entity_type):
        """
        C'tor.
        :param smart_record_conf_name: smart_record_conf_name we should work on
        :type smart_record_conf_name: str
        :param entity_type: The entity type to process the entity score.
        :type entity_type: String
        """
        self.smart_record_conf_name = smart_record_conf_name
        self.entity_type = entity_type

    @staticmethod
    def get_min_gap_from_dag_start_date_to_start_modeling(conf_reader):
        return SmartModelOperatorBuilder.get_min_gap_from_dag_start_date_to_start_modeling(conf_reader);

    def build(self, dag):
        """
        Builds entity_score_operator.
        :param smart_dag: The DAG to which all relevant "output" operators should be added
        :type smart_dag: airflow.models.DAG
        :return: The smart DAG, after the "output" operators were added
        :rtype: presidio.operators.fixed_duration_jar_operator.FixedDurationJarOperator
        """

        self.log.debug("populating the %s dag with output tasks", dag.dag_id)

        entity_score_operator = EntityScoreOperator(
            fixed_duration_strategy=timedelta(days=1),
            command=OUTPUT_RUN_DAILY_COMMAND,
            smart_record_conf_name=self.smart_record_conf_name,
            entity_type=self.entity_type,
            run_clean_command_before_retry=False,
            dag=dag)

        return entity_score_operator
