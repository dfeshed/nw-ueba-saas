from datetime import timedelta
from airflow.utils.decorators import apply_defaults
from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator
from presidio.utils.services.fixed_duration_strategy import fixed_duration_strategy_to_string


class AggrModelAccumulateAggregationsOperator(FixedDurationJarOperator):
    """
    Runs a accumulate aggregations task (a JAR file) for aggregation events model building, using a bash command.
    The c'tor accepts the task arguments that are constant throughout the
    operator runs (e.g. the fixed duration strategy and the data source).
    Other arguments, such as the start date and the end date, are evaluated before every run.
    """

    @apply_defaults
    def __init__(self, fixed_duration_strategy, feature_bucket_strategy, command, data_source, task_id=None, *args, **kwargs):
        """
        C'tor.
        :param fixed_duration_strategy: The duration covered by the feature bucket aggregations (currently only daily)
        :type fixed_duration_strategy: timedelta
        :param data_source: The data source whose events should be aggregated
        :type data_source: string
        :param task_id: The task ID of this operator - If None, the ID is generated automatically
        :type task_id: string
        """

        self.data_source = data_source
        self.task_id = task_id or '{}_{}_{}'.format(
            fixed_duration_strategy_to_string(fixed_duration_strategy),
            self.data_source,
            self.get_task_name()
        )

        java_args = {
            'schema': self.data_source,
            'feature_bucket_strategy': feature_bucket_strategy.total_seconds(),
        }

        print('agg operator. commad=', command)
        print('agg operator. kwargs=', kwargs)
        super(AggrModelAccumulateAggregationsOperator, self).__init__(
            task_id=self.task_id,
            fixed_duration_strategy=fixed_duration_strategy,
            command=command,
            java_args=java_args,
            *args,
            **kwargs
        )

    def get_task_name(self):
        """
        :return: The task name
        """
        return 'aggr_model_accumulate_aggregations'

