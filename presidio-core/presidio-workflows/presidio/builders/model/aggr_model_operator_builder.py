from airflow import LoggingMixin

from presidio.operators.model.aggr_model_operator import AggrModelOperator


class AggrModelOperatorBuilder(LoggingMixin):
    """
        The "AAggrModelOperatorBuilder" builds and return aggr_model_operator according to given schema.
        """
    build_aggr_model_interval_conf_key = "components.ade.models.feature_aggregation_records.build_model_interval_in_days"
    build_aggr_model_interval_default_value = 1
    min_gap_from_dag_start_date_to_start_aggr_modeling_conf_key = "components.ade.models.feature_aggregation_records.min_data_time_range_for_building_models_in_days"
    min_gap_from_dag_start_date_to_start_aggr_modeling_default_value = 14

    def __init__(self, schema):
        """
        C'tor.
        :param schema: The schema we should work on
        :type schema: str
        """
        self.schema = schema

    @staticmethod
    def get_aggr_model_interval(conf_reader):
        return conf_reader.read_daily_timedelta(
            AggrModelOperatorBuilder.build_aggr_model_interval_conf_key,
            AggrModelOperatorBuilder.build_aggr_model_interval_default_value)

    @staticmethod
    def get_min_gap_from_dag_start_date_to_start_aggr_modeling(conf_reader):
        return conf_reader.read_daily_timedelta(
            AggrModelOperatorBuilder.min_gap_from_dag_start_date_to_start_aggr_modeling_conf_key,
            AggrModelOperatorBuilder.min_gap_from_dag_start_date_to_start_aggr_modeling_default_value)

    def build(self, dag):
        """
        Builds the aggr_model_operator.
        :param dag: The DAG to which the operator flow should be added.
        :type dag: airflow.models.DAG
        :return: aggr_model_operator
        :rtype: presidio.operators.fixed_duration_jar_operator.FixedDurationJarOperator
        """

        aggr_model_operator = AggrModelOperator(data_source=self.schema,
                                                command="process",
                                                session_id=dag.dag_id,
                                                dag=dag)

        return aggr_model_operator
