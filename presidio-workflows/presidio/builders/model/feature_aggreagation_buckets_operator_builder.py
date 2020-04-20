from airflow import LoggingMixin

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.model.raw_model_feature_aggregation_buckets_operator import \
    RawModelFeatureAggregationBucketsOperator
from presidio.utils.services.fixed_duration_strategy import FIX_DURATION_STRATEGY_DAILY


class FeatureAggregationBucketsOperatorBuilder(LoggingMixin):
    """
        The "FeatureAggregationBucketsOperatorBuilder"  builder accepts the schema through the c'tor,
        and the "build" method builds and returns raw_model_feature_aggregation_buckets_operator according to this schema.
        """
    feature_aggregation_buckets_interval_conf_key = "components.ade.models.enriched_records.feature_aggregation_buckets_interval_in_days"
    feature_aggregation_buckets_interval_default_value = 1

    def __init__(self, schema):
        """
        C'tor.
        :param schema: The schema we should work on
        :type schema: str
        """
        self.schema = schema

    @staticmethod
    def get_feature_aggregation_buckets_interval(conf_reader):
        return conf_reader.read_daily_timedelta(
            FeatureAggregationBucketsOperatorBuilder.feature_aggregation_buckets_interval_conf_key,
            FeatureAggregationBucketsOperatorBuilder.feature_aggregation_buckets_interval_default_value)

    def build(self, dag):
        """
        Builds raw_model_feature_aggregation_buckets_operator.
        :param dag: The DAG to which the operator flow should be added.
        :type dag: airflow.models.DAG
        :return: raw_model_feature_aggregation_buckets_operator
        :rtype: presidio.operators.fixed_duration_jar_operator.FixedDurationJarOperator
        """
        raw_model_feature_aggregation_buckets_operator = RawModelFeatureAggregationBucketsOperator(
            fixed_duration_strategy=FIX_DURATION_STRATEGY_DAILY,
            command=PresidioDagBuilder.presidio_command,
            data_source=self.schema,
            dag=dag)

        return raw_model_feature_aggregation_buckets_operator
