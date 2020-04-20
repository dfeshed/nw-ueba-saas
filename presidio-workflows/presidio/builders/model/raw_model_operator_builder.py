from airflow import LoggingMixin

from presidio.operators.model.raw_model_operator import RawModelOperator


class RawModelOperatorBuilder(LoggingMixin):
    """
        The "RawModelOperatorBuilder" builds and returns raw_model_operator according to this schema.
        """

    build_raw_model_interval_conf_key = "components.ade.models.enriched_records.build_model_interval_in_days"
    build_raw_model_interval_default_value = 1
    min_gap_from_dag_start_date_to_start_raw_modeling_conf_key = "components.ade.models.enriched_records.min_data_time_range_for_building_models_in_days"
    min_gap_from_dag_start_date_to_start_raw_modeling_default_value = 14

    def __init__(self, schema):
        """
        C'tor.
        :param schema: The schema we should work on
        :type schema: str
        """
        self.schema = schema

    @staticmethod
    def get_build_raw_model_interval(conf_reader):
        return conf_reader.read_daily_timedelta(
            RawModelOperatorBuilder.build_raw_model_interval_conf_key,
            RawModelOperatorBuilder.build_raw_model_interval_default_value)

    @staticmethod
    def get_min_gap_from_dag_start_date_to_start_raw_modeling(conf_reader):
        return conf_reader.read_daily_timedelta(
            RawModelOperatorBuilder.min_gap_from_dag_start_date_to_start_raw_modeling_conf_key,
            RawModelOperatorBuilder.min_gap_from_dag_start_date_to_start_raw_modeling_default_value)

    def build(self, dag):
        """
        Builds the "Raw model" operator.
        :param dag: The DAG to which the operator flow should be added.
        :type dag: airflow.models.DAG
        :return: raw_model_operator
        :rtype: presidio.operators.fixed_duration_jar_operator.FixedDurationJarOperator
        """

        raw_model_operator = RawModelOperator(data_source=self.schema,
                                              command="process",
                                              session_id=dag.dag_id,
                                              dag=dag)

        return raw_model_operator
