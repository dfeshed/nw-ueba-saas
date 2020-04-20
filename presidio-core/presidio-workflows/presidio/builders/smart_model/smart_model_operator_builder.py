from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.builders.smart_model.smart_model_accumulate_operator_builder import SmartModelAccumulateOperatorBuilder
from presidio.operators.model.smart_model_operator import SmartModelOperator


class SmartModelOperatorBuilder(PresidioDagBuilder):
    """
           The "SmartModelOperatorBuilder" is responsible for building the models according to smart_conf_name.
           """

    build_model_interval_conf_key = "components.ade.models.smart_records.build_model_interval_in_days"
    build_model_interval_default_value = 1
    min_data_time_range_for_building_models_conf_key = "components.ade.models.smart_records.min_data_time_range_for_building_models_in_days"
    min_data_time_range_for_building_models_default_value = 14

    @staticmethod
    def get_build_model_interval(conf_reader):
        return conf_reader.read_daily_timedelta(
            SmartModelOperatorBuilder.build_model_interval_conf_key,
            SmartModelOperatorBuilder.build_model_interval_default_value)

    @staticmethod
    def get_min_gap_from_dag_start_date_to_start_modeling(config_reader):
        return SmartModelAccumulateOperatorBuilder.get_min_gap_from_dag_start_date_to_start_accumulating(
            config_reader) + \
               config_reader.read_daily_timedelta(
                   SmartModelOperatorBuilder.min_data_time_range_for_building_models_conf_key,
                   SmartModelOperatorBuilder.min_data_time_range_for_building_models_default_value)

    def build(self, dag):
        """
        Build smart_model_operator.

        :param dag: The smart_model DAG to populate
        :type dag: airflow.models.DAG
        :return: smart_model_operator
        :rtype: presidio.operators.fixed_duration_jar_operator.FixedDurationJarOperator
        """

        smart_model_operator = SmartModelOperator(smart_events_conf=dag.default_args.get("smart_conf_name"),
                                                  command="process",
                                                  session_id=dag.dag_id,
                                                  dag=dag)
        return smart_model_operator
