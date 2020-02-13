from airflow.utils.decorators import apply_defaults

from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator


class HourIsReadyS3Operator(FixedDurationJarOperator):

    ui_color = '#e0d576'  # yellow
    ui_fgcolor = '#000000'  # black

    @apply_defaults
    def __init__(self, command, schema, *args, **kwargs):

        self.log.debug('hour_is_ready_s3 operator init kwargs=%s', str(kwargs))

        java_args = {
            'schema': schema,
        }

        self.log.debug('hour_is_ready_s3 operator. command=%s', command)
        super(HourIsReadyS3Operator, self).__init__(
            command=command,
            java_args=java_args,
            *args,
            **kwargs
        )
