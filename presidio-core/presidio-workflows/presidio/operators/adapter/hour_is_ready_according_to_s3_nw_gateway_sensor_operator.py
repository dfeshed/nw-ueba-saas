from datetime import timedelta

from airflow.utils.decorators import apply_defaults

from presidio.utils.airflow.context_wrapper import ContextWrapper
from presidio.utils.airflow.operators.spring_boot_jar_operator import SpringBootJarOperator
from presidio.utils.services.time_service import convert_to_utc


class HourIsReadyAccordingToS3NWGatewaySensorOperator(SpringBootJarOperator):

    ui_color = '#e0d576'  # yellow
    ui_fgcolor = '#000000'  # black

    @apply_defaults
    def __init__(self, command, schema, timeout, time_to_sleep_in_seconds, *args, **kwargs):

        self.log.debug('hour_is_ready_s3 operator init kwargs=%s', str(kwargs))

        java_args = {
            'schema': schema,
            'timeout': timeout,
            "time_to_sleep": time_to_sleep_in_seconds
        }

        self.log.debug('hour_is_ready_s3 operator. command=%s', command)
        super(HourIsReadyAccordingToS3NWGatewaySensorOperator, self).__init__(
            command=command,
            java_args=java_args,
            *args,
            **kwargs
        )

    def execute(self, context):
        """
        Add end_date to java_args
        """
        context_wrapper = ContextWrapper(context)
        execution_date = context_wrapper.get_execution_date()

        end_date = execution_date + + timedelta(hours=1)

        java_args = {
            'end_date': convert_to_utc(end_date)
        }

        super(HourIsReadyAccordingToS3NWGatewaySensorOperator, self).update_java_args(java_args)
        super(HourIsReadyAccordingToS3NWGatewaySensorOperator, self).execute(context)
