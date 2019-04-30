from airflow import LoggingMixin

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.model.aggr_model_accumulate_aggregation_operator import AggrModelAccumulateAggregationsOperator
from presidio.utils.services.fixed_duration_strategy import FIX_DURATION_STRATEGY_DAILY, is_execution_date_valid


class AccumulateAggregationsOperatorBuilder(LoggingMixin):
    """
        The "AccumulateAggregationsOperatorBuilder" builds and return aggr_model_accumulate_aggregations_operator.
        There are 2 parameters that define the aggregation events to be accumulated:
        - schema
        - fixed duration strategy - The duration covered by the aggregations (e.g. hourly or daily)
        """

    feature_aggr_accumulate_interval_conf_key = "components.ade.models.feature_aggregation_records.accumulate_interval_in_days"
    feature_aggr_accumulate_interval_default_value = 1

    def __init__(self, schema, fixed_duration_strategy):
        """
        C'tor.
        :param schema: The schema we should work on
        :type schema: str
        :param fixed_duration_strategy: The duration covered by the aggregations (e.g. hourly or daily)
        :type fixed_duration_strategy: datetime.timedelta
        """

        self.schema = schema
        self.fixed_duration_strategy = fixed_duration_strategy

    @staticmethod
    def get_accumulate_interval(conf_reader):
        return conf_reader.read_daily_timedelta(
            AccumulateAggregationsOperatorBuilder.feature_aggr_accumulate_interval_conf_key,
            AccumulateAggregationsOperatorBuilder.feature_aggr_accumulate_interval_default_value)

    def build(self, dag):
        """
        Builds aggr_model_accumulate_aggregations_operator.
        :param dag: The DAG to which the operator flow should be added.
        :type dag: airflow.models.DAG
        :return: aggr_model_accumulate_aggregations_operator
        :rtype: presidio.operators.fixed_duration_jar_operator.FixedDurationJarOperator
        """
        aggr_model_accumulate_aggregations_operator = AggrModelAccumulateAggregationsOperator(
            fixed_duration_strategy=FIX_DURATION_STRATEGY_DAILY,
            feature_bucket_strategy=self.fixed_duration_strategy,
            command=PresidioDagBuilder.presidio_command,
            data_source=self.schema,
            dag=dag)

        return aggr_model_accumulate_aggregations_operator
