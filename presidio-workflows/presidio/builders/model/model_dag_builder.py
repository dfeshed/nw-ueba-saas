from presidio.builders.model.accumulate_aggregations_operator_builder import AccumulateAggregationsOperatorBuilder
from presidio.builders.model.aggr_model_operator_builder import AggrModelOperatorBuilder
from presidio.builders.model.feature_aggreagation_buckets_operator_builder import \
    FeatureAggregationBucketsOperatorBuilder
from presidio.builders.model.raw_model_operator_builder import RawModelOperatorBuilder
from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.utils.airflow.schedule_interval_utils import get_schedule_interval
from presidio.utils.services.fixed_duration_strategy import \
    FIX_DURATION_STRATEGY_HOURLY, is_execution_date_valid


class ModelDagBuilder(PresidioDagBuilder):
    """
         An "Model DAG" builder  creates  "Aggr Model" and "Raw Model"  operators, links them to the DAG and
        configures the dependencies between them.
         """

    def build(self, dag):
        """
        Builds the aggr model and raw model:
        :param dag: The model DAG to populate
        :type dag: airflow.models.DAG
        :return: The given model DAG, after it has been populated
        :rtype: airflow.models.DAG
        """

        schema = dag.default_args['schema']
        self._build_raw_model_operator(schema, dag)
        self._build_aggr_model_operator(schema, dag)
        return dag

    def _build_raw_model_operator(self, schema, dag):
        feature_aggregation_buckets_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id=('feature_aggregation_buckets_short_circuit_{0}'.format(schema)),
            dag=dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     FeatureAggregationBucketsOperatorBuilder.get_feature_aggregation_buckets_interval(PresidioDagBuilder.conf_reader),
                                                                     get_schedule_interval(dag))
        )

        feature_aggregation_buckets_operator = FeatureAggregationBucketsOperatorBuilder(schema).build(dag)

        raw_model_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='raw_model_short_circuit_{0}'.format(schema),
            dag=dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     RawModelOperatorBuilder.get_build_raw_model_interval(PresidioDagBuilder.conf_reader),
                                                                     get_schedule_interval(dag)) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 dag,
                                                 RawModelOperatorBuilder.get_min_gap_from_dag_start_date_to_start_raw_modeling(PresidioDagBuilder.conf_reader),
                                                 kwargs['execution_date'],
                                                 get_schedule_interval(
                                                     dag)))

        raw_model_operator = RawModelOperatorBuilder(schema).build(dag)

        feature_aggregation_buckets_short_circuit_operator >> feature_aggregation_buckets_operator >> raw_model_short_circuit_operator >> raw_model_operator

    def _build_aggr_model_operator(self, schema, dag):
        acc_aggregation_operator = AccumulateAggregationsOperatorBuilder(schema, FIX_DURATION_STRATEGY_HOURLY).build(dag)

        aggr_accumulate_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='aggr_accumulate_short_circuit_{0}'.format(schema),
            dag=dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     AccumulateAggregationsOperatorBuilder.get_accumulate_interval(PresidioDagBuilder.conf_reader),
                                                                     get_schedule_interval(dag))
        )

        aggr_model_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='aggr_model_short_circuit_{0}'.format(schema),
            dag=dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     AggrModelOperatorBuilder.get_aggr_model_interval(PresidioDagBuilder.conf_reader),
                                                                     get_schedule_interval(dag)) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 dag,
                                                 AggrModelOperatorBuilder.get_min_gap_from_dag_start_date_to_start_aggr_modeling(PresidioDagBuilder.conf_reader),
                                                 kwargs['execution_date'],
                                                 get_schedule_interval(dag)))

        aggr_model_operator = AggrModelOperatorBuilder(schema).build(dag)

        aggr_accumulate_short_circuit_operator >> acc_aggregation_operator >> aggr_model_short_circuit_operator >> aggr_model_operator
