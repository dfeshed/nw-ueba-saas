from presidio.builders.model.aggr_model_operator_builder import AggrModelOperatorBuilder
from presidio.builders.model.raw_model_operator_builder import RawModelOperatorBuilder
from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.model.smart_model_accumulate_operator import SmartModelAccumulateOperator
from presidio.operators.model.smart_model_operator import SmartModelOperator
from presidio.utils.airflow.schedule_interval_utils import get_schedule_interval
from presidio.utils.services.fixed_duration_strategy import FIX_DURATION_STRATEGY_DAILY, is_execution_date_valid
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton


class SmartModelAccumulateOperatorBuilder(PresidioDagBuilder):
    """
           The "SmartModelAccumulateOperatorBuilder" responsible for accumulating the smart events.
           returns smart_model_accumulate_operator according of the given smart_conf_name
           """

    accumulate_interval_conf_key = "components.ade.models.smart_records.accumulate_interval_in_days"
    accumulate_interval_default_value = 1

    @staticmethod
    def get_accumulate_interval(config_reader):
        return config_reader.read_daily_timedelta(
            SmartModelAccumulateOperatorBuilder.accumulate_interval_conf_key,
            SmartModelAccumulateOperatorBuilder.accumulate_interval_default_value)

    @staticmethod
    def get_min_gap_from_dag_start_date_to_start_accumulating(conf_reader):
        raw_model_min_gap_from_dag_start_date_to_start_modeling = RawModelOperatorBuilder.get_min_gap_from_dag_start_date_to_start_raw_modeling(
            conf_reader)
        aggr_model_min_gap_from_dag_start_date_to_start_modeling = AggrModelOperatorBuilder.get_min_gap_from_dag_start_date_to_start_aggr_modeling(
            conf_reader)
        return max(raw_model_min_gap_from_dag_start_date_to_start_modeling,
                   aggr_model_min_gap_from_dag_start_date_to_start_modeling)

    def build(self, dag):
        """
       Build smart_model_accumulate_operator.

        :param dag: The smart_model DAG to populate
        :type dag: airflow.models.DAG
        :return: smart_model_accumulate_operator
        :rtype: presidio.operators.fixed_duration_jar_operator.FixedDurationJarOperator
        """

        smart_model_accumulate_operator = SmartModelAccumulateOperator(
            fixed_duration_strategy=FIX_DURATION_STRATEGY_DAILY,
            command=PresidioDagBuilder.presidio_command,
            smart_events_conf=dag.default_args.get("smart_conf_name"),
            dag=dag
        )

        return smart_model_accumulate_operator
