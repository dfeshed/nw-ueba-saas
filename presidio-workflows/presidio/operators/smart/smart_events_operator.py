from airflow.utils.decorators import apply_defaults

from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator


class SmartEventsOperator(FixedDurationJarOperator):

    # todo: replace with run
    liors_special_run_command = "process"

    """
    Runs the "Smart Events" task (JAR). The task:
    1. Groups together in a smart event the configured aggregation events from the time interval, per context.
    2. Calculates the smart value of each event, according to its aggregation events.
    3. Scores the smart value of each event.
    """

    # Color configurations for the Airflow UI
    ui_color = '#8e44ad'
    ui_fgcolor = '#ffffff'

    @apply_defaults
    def __init__(self, fixed_duration_strategy, smart_events_conf, task_id=None, *args, **kwargs):
        """
        C'tor.
        :param fixed_duration_strategy: The duration covered by the smart events (e.g. hourly or daily)
        :type fixed_duration_strategy: timedelta
        :param smart_events_conf: The name of the configuration defining the smart events
        :type smart_events_conf: string
        :param task_id: The task ID of this operator - If None, the ID is generated automatically
        :type task_id: string
        """
        java_args = {
            'smart_record_conf_name': smart_events_conf
        }

        super(SmartEventsOperator, self).__init__(
            # The smart_events_conf usually contains the fixed_duration_strategy and the
            # phrase "smart_events" in its name, so it can serve as the task_id as well
            task_id=task_id or smart_events_conf,
            fixed_duration_strategy=fixed_duration_strategy,
            jvm_args={},
            java_args=java_args,
            *args,
            **kwargs
        )
    def add_fixed_duration_strategy(self, java_args):
        pass
