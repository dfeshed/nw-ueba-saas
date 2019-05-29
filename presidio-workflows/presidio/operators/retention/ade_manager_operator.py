from airflow.utils.decorators import apply_defaults

from presidio.utils.airflow.operators.retention.retention_operator import RetentionOperator
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton

class AdeManagerOperator(RetentionOperator):
    """
    Runs a "AdeManagerOperator" task (JAR).
    The jar cleanup all the enriched collections.
    """

    # Color configurations for the Airflow UI
    ui_color = '#1abc9c'
    ui_fgcolor = '#000000'

    RETENTION_COMMAND_CONFIG_PATH = 'retention.ade_manager.command'
    RETENTION_COMMAND_DEFAULT_VALUE = 'retention'

    @apply_defaults
    def __init__(self, *args, **kwargs):
        conf_reader = ConfigServerConfigurationReaderSingleton().config_reader
        self._retention_command = conf_reader.read(AdeManagerOperator.RETENTION_COMMAND_CONFIG_PATH,
                                                   AdeManagerOperator.RETENTION_COMMAND_DEFAULT_VALUE)

        super(AdeManagerOperator, self).__init__(command=self._retention_command,
                                                 task_id=self.get_task_id(),
                                                 *args, **kwargs)


    def get_task_id(self):
        """
        :return: The task id
        """
        return 'ade_manager'
