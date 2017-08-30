import datetime
from airflow.utils.decorators import apply_defaults

from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator
from presidio.utils.services.fixed_duration_strategy import fixed_duration_strategy_to_string


class UserScoreOperator(FixedDurationJarOperator):
    """
    Runs the user score task (a JAR file) using a bash command.
    The operator arguments, such as the start date and the end date, are evaluated before every run.
    This is an abstract class and should not be instantiated - The inheritors should override
    """

    @apply_defaults
    def __init__(self, command, jvm_args, task_id=None, *args, **kwargs):
        """
        C'tor.
        :param task_id: The task ID of this operator - If None, the ID is generated automatically
        :type task_id: string
        """

        print('user score operator init kwargs=', kwargs)

        self.task_id = task_id or '{}_{}'.format(
            self.get_task_name(),
            datetime.datetime.now()
        )

        print('agg operator. commad=', command)
        print('agg operator. kwargs=', kwargs)
        super(UserScoreOperator, self).__init__(
            task_id=self.task_id,
            command=command,
            *args,
            **kwargs
        )

