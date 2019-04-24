import logging

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.model.aggr_model_accumulate_aggregation_operator import AggrModelAccumulateAggregationsOperator
from presidio.operators.model.aggr_model_operator import AggrModelOperator
from presidio.utils.airflow.schedule_interval_utils import get_schedule_interval
from presidio.utils.services.fixed_duration_strategy import FIX_DURATION_STRATEGY_DAILY, is_execution_date_valid


class AggrModelDagBuilder(PresidioDagBuilder):
    """
         The "Aggr Model" builder consists of aggr_model_accumulate_aggregations_operator followed by aggr_model_operator
         The aggr_model_accumulate_aggregations_operator is responsible for building and accumulating the aggregation events
         The aggr_model_operator is responsible for building the models

        There are 2 parameters that define the aggregation events to be built, accumulated and modeled:
        - schema
        - fixed duration strategy - The duration covered by the aggregations (e.g. hourly or daily)
        
        returns the DAG according to the given schema and fixed duration strategy
        """

    build_aggr_model_interval_conf_key = "components.ade.models.feature_aggregation_records.build_model_interval_in_days"
    build_aggr_model_interval_default_value = 1
    feature_aggr_accumulate_interval_conf_key = "components.ade.models.feature_aggregation_records.accumulate_interval_in_days"
    feature_aggr_accumulate_interval_default_value = 1
    min_gap_from_dag_start_date_to_start_aggr_modeling_conf_key = "components.ade.models.feature_aggregation_records.min_data_time_range_for_building_models_in_days"
    min_gap_from_dag_start_date_to_start_aggr_modeling_default_value = 14

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
        self._build_aggr_model_interval = AggrModelDagBuilder.conf_reader.read_daily_timedelta(
            AggrModelDagBuilder.build_aggr_model_interval_conf_key,
            AggrModelDagBuilder.build_aggr_model_interval_default_value)
        self._accumulate_interval = AggrModelDagBuilder.conf_reader.read_daily_timedelta(
            AggrModelDagBuilder.feature_aggr_accumulate_interval_conf_key,
            AggrModelDagBuilder.feature_aggr_accumulate_interval_default_value)
        self._min_gap_from_dag_start_date_to_start_aggr_modeling = self.get_min_gap_from_dag_start_date_to_start_aggr_modeling(
            AggrModelDagBuilder.conf_reader)

    @staticmethod
    def get_min_gap_from_dag_start_date_to_start_aggr_modeling(config_reader):
        return config_reader.read_daily_timedelta(
            AggrModelDagBuilder.min_gap_from_dag_start_date_to_start_aggr_modeling_conf_key,
            AggrModelDagBuilder.min_gap_from_dag_start_date_to_start_aggr_modeling_default_value)

    def build(self, model_dag):
        """
        Builds the "Accumulate Aggregation Events" and  "Aggr Models" operators and wire the second as downstream of the first.
        :param model_dag: The DAG to which the operator flow should be added.
        :type model_dag: airflow.models.DAG
        :return: The smart DAG, after the operator flow was added
        :rtype: airflow.models.DAG
        """
        # defining the Accumulate operator
        aggr_model_accumulate_aggregations_operator = AggrModelAccumulateAggregationsOperator(
            fixed_duration_strategy=FIX_DURATION_STRATEGY_DAILY,
            feature_bucket_strategy=self.fixed_duration_strategy,
            command=PresidioDagBuilder.presidio_command,
            data_source=self.schema,
            dag=model_dag)

        aggr_accumulate_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='aggr_accumulate_short_circuit_{0}'.format(self.schema),
            dag=model_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     self._accumulate_interval,
                                                                     get_schedule_interval(model_dag))
        )

        # defining the model operator
        aggr_model_operator = AggrModelOperator(data_source=self.schema,
                                                command="process",
                                                session_id=model_dag.dag_id,
                                                dag=model_dag)

        aggr_model_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='aggr_model_short_circuit_{0}'.format(self.schema),
            dag=model_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     self._build_aggr_model_interval,
                                                                     get_schedule_interval(model_dag)) &
                                                                     PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                                         model_dag,
                                                                         self._min_gap_from_dag_start_date_to_start_aggr_modeling,
                                                                         kwargs['execution_date'],
                                                                         get_schedule_interval(model_dag)))

        # defining the dependencies between the operators
        aggr_accumulate_short_circuit_operator >> aggr_model_accumulate_aggregations_operator
        aggr_model_short_circuit_operator >> aggr_model_operator
        aggr_model_accumulate_aggregations_operator.set_downstream(aggr_model_short_circuit_operator)
