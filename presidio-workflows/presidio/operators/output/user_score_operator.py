from presidio.operators.output.abstract_output_operator import AbstractOutputOperator


class UserScoreOperator(AbstractOutputOperator):
    """
    Runs an user score task (a JAR file) using a bash command.
    The c'tor accepts the task arguments that are constant throughout the
    operator runs (e.g. the fixed duration strategy and the schema).
    Other arguments, such as the start date and the end date, are evaluated before every run.
    """

    def get_task_name(self):
        """
        :return: The task name
        """
        return 'user_score_processor'
