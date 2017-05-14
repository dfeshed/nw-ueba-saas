from airflow import DAG
from presidio.operators.aggregation.feature_aggregations_operator import FeatureAggregationsOperator
from presidio.operators.aggregation.score_aggregations_operator import ScoreAggregationsOperator
from presidio.utils.date_time import fixed_duration_strategy_to_string


class AggregationsDagBuilder(object):
    """
    An "Aggregations DAG" builder - The "Aggregations DAG" consists of 2 tasks / operators
    (feature aggregations and score aggregations) and can be either an hourly DAG or a daily DAG.
    The builder accepts the DAG's attributes through the c'tor and has 2 methods:
    1. build_aggregations_dag - Builds and returns the DAG according to the given attributes.
    2. get_aggregations_dag_id - Returns the ID of the DAG. Note that if the DAG is a subDAG whose parent is
       [parent_dag_id], the return value will be [child_dag_id] (and not [parent_dag_id].[child_dag_id]).
    """

    def __init__(self, fixed_duration_strategy, data_source, schedule_interval, start_date, parent_dag_id=None):
        """
        C'tor.
        :param fixed_duration_strategy: The duration covered by the aggregations (e.g. hourly or daily)
        :type fixed_duration_strategy: timedelta
        :param data_source: The data source whose events should be aggregated
        :type data_source: string
        :param schedule_interval: The frequency of the DAG runs
        :type schedule_interval: timedelta
        :param start_date: The timestamp of the first DAG run
        :type start_date: datetime
        :param parent_dag_id: If the DAG is a subDAG - The ID of the parent DAG, otherwise - None
        :type parent_dag_id: string
        """

        self.fixed_duration_strategy = fixed_duration_strategy
        self.data_source = data_source
        self.schedule_interval = schedule_interval
        self.start_date = start_date
        self.parent_dag_id = parent_dag_id

    def build_aggregations_dag(self):
        """
        Builds the "Feature Aggregations" operator, the "Score Aggregations" operator and the DAG that contains them.
        :return: The built "Aggregations DAG"
        """

        if self.parent_dag_id is None:
            dag_id = self.get_aggregations_dag_id()
        else:
            dag_id = '{}.{}'.format(self.parent_dag_id, self.get_aggregations_dag_id())

        aggregations_dag = DAG(
            dag_id=dag_id,
            schedule_interval=self.schedule_interval,
            start_date=self.start_date
        )

        FeatureAggregationsOperator(
            fixed_duration_strategy=self.fixed_duration_strategy,
            data_source=self.data_source,
            dag=aggregations_dag
        )

        ScoreAggregationsOperator(
            fixed_duration_strategy=self.fixed_duration_strategy,
            data_source=self.data_source,
            dag=aggregations_dag
        )

        return aggregations_dag

    def get_aggregations_dag_id(self):
        """
        :return: The ID of the "Aggregations DAG" (does not include the parent DAG ID if it's a subDAG)
        """

        return '{}_{}_aggregations'.format(
            fixed_duration_strategy_to_string(self.fixed_duration_strategy),
            self.data_source
        )
