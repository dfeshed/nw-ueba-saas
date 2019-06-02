from airflow.utils.decorators import apply_defaults

from presidio.utils.airflow.operators.retention.retention_operator import RetentionOperator


class AdeManagerOperator(RetentionOperator):
    """
    Runs a "AdeManagerOperator" task (JAR).
    The jar cleanup all the enriched collections.
    """

    # Color configurations for the Airflow UI
    ui_color = '#1abc9c'
    ui_fgcolor = '#000000'

    @apply_defaults
    def __init__(self, command, *args, **kwargs):
        super(AdeManagerOperator, self).__init__(command=command,
                                                 task_id=self.get_task_id(),
                                                 *args, **kwargs)

    def get_task_id(self):
        """
        :return: The task id
        """
        return 'ade_manager'
