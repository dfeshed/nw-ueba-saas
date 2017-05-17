from airflow.operators.bash_operator import BashOperator
from abc import ABCMeta, abstractmethod
from airflow.utils.decorators import apply_defaults
from presidio.utils.date_time import fixed_duration_strategy_to_string


class AggregationsOperator(BashOperator):
    """
    Runs an aggregations task (a JAR file) using a bash command.
    The c'tor accepts the task arguments that are constant throughout the
    operator runs (e.g. the fixed duration strategy and the data source).
    Other arguments, such as the start date and the end date, are evaluated before every run.
    This is an abstract class and should not be instantiated - The inheritors should override
    methods that provide task specific information (such as the task name and the JAR file path).
    Currently the known inheritors are FeatureAggregationsOperator and ScoreAggregationsOperator.
    """

    __metaclass__ = ABCMeta

    _BASH_COMMAND = ' '.join([
        'java -jar {{params.jar_file_path}}',
        'start_date={{execution_date.isoformat()}}',
        'end_date={{(execution_date + params.fixed_duration_strategy).isoformat()}}',
        'data_source={{params.data_source}}'
    ])

    @apply_defaults
    def __init__(self, fixed_duration_strategy, data_source, task_id=None, *args, **kwargs):
        """
        C'tor.
        :param fixed_duration_strategy: The duration covered by the aggregations (e.g. hourly or daily)
        :type fixed_duration_strategy: timedelta
        :param data_source: The data source whose events should be aggregated
        :type data_source: string
        :param task_id: The task ID of this operator - If None, the ID is generated automatically
        :type task_id: string
        """

        self.fixed_duration_strategy = fixed_duration_strategy
        self.data_source = data_source
        self.task_id = task_id or '{}_{}_{}'.format(
            fixed_duration_strategy_to_string(self.fixed_duration_strategy),
            self.data_source,
            self.get_task_name()
        )

        # Create a dictionary that will be used when resolving the bash command
        params = {
            'jar_file_path': self.get_jar_file_path(),
            'fixed_duration_strategy': self.fixed_duration_strategy,
            'data_source': self.data_source
        }

        # Merge the new params above with the old params and overwrite existing keys
        kwargs['params'] = kwargs['params'] or {}
        kwargs['params'].update(params)

        super(AggregationsOperator, self).__init__(
            task_id=self.task_id,
            bash_command=AggregationsOperator._BASH_COMMAND,
            *args,
            **kwargs
        )

    @abstractmethod
    def get_task_name(self):
        """
        :return: The task name (e.g. "Feature Aggregations" or "Score Aggregations")
        """
        pass

    @abstractmethod
    def get_jar_file_path(self):
        """
        :return: The full path to the JAR file that will be executed
        """
        pass
