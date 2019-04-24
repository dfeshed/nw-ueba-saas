
from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.model.raw_model_feature_aggregation_buckets_operator import RawModelFeatureAggregationBucketsOperator
from presidio.operators.model.raw_model_operator import RawModelOperator
from presidio.utils.airflow.schedule_interval_utils import get_schedule_interval
from presidio.utils.services.fixed_duration_strategy import FIX_DURATION_STRATEGY_DAILY, is_execution_date_valid


class RawModelDagBuilder(PresidioDagBuilder):
    """
        A "Raw Model DAG" builder - The "Raw Model DAG" consists of 2 tasks / operators
        (feature aggregation buckets and raw models).
        The builder accepts the schema through the c'tor, and the "build" method builds and
        returns the DAG according to this schema.
        """

    build_raw_model_interval_conf_key = "components.ade.models.enriched_records.build_model_interval_in_days"
    build_raw_model_interval_default_value = 1
    feature_aggregation_buckets_interval_conf_key = "components.ade.models.enriched_records.feature_aggregation_buckets_interval_in_days"
    feature_aggregation_buckets_interval_default_value = 1
    min_gap_from_dag_start_date_to_start_raw_modeling_conf_key = "components.ade.models.enriched_records.min_data_time_range_for_building_models_in_days"
    min_gap_from_dag_start_date_to_start_raw_modeling_default_value = 14

    def __init__(self, schema):
        """
        C'tor.
        :param schema: The schema we should work on
        :type schema: str
        """
        self.schema = schema
        self._build_raw_model_interval = PresidioDagBuilder.conf_reader.read_daily_timedelta(
            RawModelDagBuilder.build_raw_model_interval_conf_key,
            RawModelDagBuilder.build_raw_model_interval_default_value)
        self._feature_aggregation_buckets_interval = \
            RawModelDagBuilder.conf_reader.read_daily_timedelta(RawModelDagBuilder.feature_aggregation_buckets_interval_conf_key,
                                               RawModelDagBuilder.feature_aggregation_buckets_interval_default_value)
        self._min_gap_from_dag_start_date_to_start_raw_modeling = self.get_min_gap_from_dag_start_date_to_start_raw_modeling(
            RawModelDagBuilder.conf_reader)

    @staticmethod
    def get_min_gap_from_dag_start_date_to_start_raw_modeling(config_reader):
        return config_reader.read_daily_timedelta(
            RawModelDagBuilder.min_gap_from_dag_start_date_to_start_raw_modeling_conf_key,
            RawModelDagBuilder.min_gap_from_dag_start_date_to_start_raw_modeling_default_value)

    def build(self, model_dag):
        """
        Builds the "Feature Aggregation Buckets" operator, the "Raw Models" operator and wire the second as downstream of the first.
        :param model_dag: The DAG to which the operator flow should be added.
        :type model_dag: airflow.models.DAG
        :return: The model DAG, after the operator flow was added
        :rtype: airflow.models.DAG
        """

        feature_aggregation_buckets_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id=('feature_aggregation_buckets_short_circuit_{0}'.format(self.schema)),
            dag=model_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     self._feature_aggregation_buckets_interval,
                                                                     get_schedule_interval(model_dag))
        )

        # defining feature aggregation buckets operator
        raw_model_feature_aggregation_buckets_operator = RawModelFeatureAggregationBucketsOperator(
            fixed_duration_strategy=FIX_DURATION_STRATEGY_DAILY,
            command=PresidioDagBuilder.presidio_command,
            data_source=self.schema,
            dag=model_dag)


        raw_model_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='raw_model_short_circuit_{0}'.format(self.schema),
            dag=model_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     self._build_raw_model_interval,
                                                                     get_schedule_interval(model_dag)) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 model_dag,
                                                 self._min_gap_from_dag_start_date_to_start_raw_modeling,
                                                 kwargs['execution_date'],
                                                 get_schedule_interval(
                                                     model_dag)))

        # defining model operator
        raw_model_operator = RawModelOperator(data_source=self.schema,
                                              command="process",
                                              session_id=model_dag.dag_id,
                                              dag=model_dag)


        # defining the dependencies between the operators
        feature_aggregation_buckets_short_circuit_operator >> raw_model_feature_aggregation_buckets_operator
        raw_model_short_circuit_operator >> raw_model_operator
        raw_model_feature_aggregation_buckets_operator.set_downstream(raw_model_short_circuit_operator)

        return model_dag
