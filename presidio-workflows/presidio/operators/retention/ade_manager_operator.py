from datetime import timedelta

from airflow.utils.decorators import apply_defaults

from presidio.utils.airflow.schedule_interval_utils import get_schedule_interval
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton
from presidio.utils.services.time_service import convert_to_utc, floor_time

from presidio.utils.airflow.context_wrapper import ContextWrapper

from presidio.utils.airflow.operators.spring_boot_jar_operator import SpringBootJarOperator


class AdeManagerOperator(SpringBootJarOperator):
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

        self.interval = get_schedule_interval(kwargs.get('dag'))
        kwargs['retry_callback'] = AdeManagerOperator.handle_retry
        retry_extra_params = {'schedule_interval': self.interval}

        super(AdeManagerOperator, self).__init__(command=self._retention_command,
                                                 retry_extra_params=retry_extra_params,
                                                 retry_java_args_method=AdeManagerOperator.add_java_args,
                                                 task_id=self.get_task_id(),
                                                 *args, **kwargs)

    def execute(self, context):
        """
        Add until_date to java_args
        """
        context_wrapper = ContextWrapper(context)
        execution_date = context_wrapper.get_execution_date()

        until_date = floor_time(execution_date + self.interval, time_delta=timedelta(days=1))

        java_args = {
            'until_date': convert_to_utc(until_date),
        }

        super(AdeManagerOperator, self).update_java_args(java_args)
        super(AdeManagerOperator, self).execute(context)

    @staticmethod
    def add_java_args(context):
        params = context['params']
        interval = params['retry_extra_params']['schedule_interval']
        context_wrapper = ContextWrapper(context)
        execution_date = context_wrapper.get_execution_date()

        end_date = execution_date + interval
        java_args = {
            'until_date': convert_to_utc(end_date)
        }

        java_args = ' '.join(
            SpringBootJarOperator.java_args_prefix + '%s %s' % (key, val) for (key, val) in java_args.iteritems()
        )
        return java_args


    def get_task_id(self):
        """
        :return: The task id
        """
        return 'ade_manager'

    @staticmethod
    def handle_retry(context):
        """
        The ade manager application does not need retries, as the "run" command itself manages the component retention.
        """
        pass
