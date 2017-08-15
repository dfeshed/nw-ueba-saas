from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.model.aggr_model_accumulate_aggregation_operator import AggrModelAccumulateAggregationsOperator
from presidio.operators.model.aggr_model_operator import AggrModelOperator
from presidio.utils.services.fixed_duration_strategy import FIX_DURATION_STRATEGY_DAILY





class AggrModelDagBuilder(PresidioDagBuilder):
    """
        An "Aggr Model DAG" builder - 
        The "Aggr Model DAG" consists of one sub dag followed by operator
        The sub dag responsible for building and accumulating the aggregation events data
        The operator is respobsible for building the models
        Accumulating the data will happen once a day whereas the models might be built once a day or less (i.e. once 2 days)
        
        There are 2 parameters that define the aggregation events to be built, accumulated and modeled:
        - data source
        - fixed duration strategy - The duration covered by the aggregations (e.g. hourly or daily)
        
        returns the DAG according to the given data source and fixed duration strategy
        """

    def __init__(self, data_source, fixed_duration_strategy):
        """
        C'tor.
        :param data_source: The data source from which the aggregation events are being built accumulated and finnaly modeled.
        :type data_source: str
        :param fixed_duration_strategy: The duration covered by the aggregations (e.g. hourly or daily)
        :type fixed_duration_strategy: datetime.timedelta
        """

        self._data_source = data_source
        self._fixed_duration_strategy = fixed_duration_strategy

    def build(self, aggr_model_dag):
        """
        Builds the "Accumulate Aggregation Events" sub dag, the "Aggr Models" operator and wire the second as downstream of the first.
        :param aggr_model_dag: The DAG to which the operator flow should be added.
        :type aggregations_dag: airflow.models.DAG
        :return: The input DAG, after the operator flow was added
        :rtype: airflow.models.DAG
        """
        aggr_model_accumulate_aggregations_operator = AggrModelAccumulateAggregationsOperator(
            fixed_duration_strategy=FIX_DURATION_STRATEGY_DAILY,
            feature_bucket_strategy=self._fixed_duration_strategy,
            command=PresidioDagBuilder.presidio_command,
            data_source=self._data_source,
            dag=aggr_model_dag)
        aggr_model_operator = AggrModelOperator(data_source=self._data_source,
                                              command="process",
                                              session_id=aggr_model_dag.dag_id.split('.', 1)[0],
                                              dag=aggr_model_dag)

        aggr_model_accumulate_aggregations_operator.set_downstream(aggr_model_operator)

        return aggr_model_dag