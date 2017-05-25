from presidio.utils.airflow.operators.jar_operator import JarOperator
from presidio.utils.airflow.services.time_service import TimeService
import logging
from airflow.models import TaskInstance
from airflow.utils.state import State
from airflow.utils.decorators import apply_defaults
from airflow import settings
from datetime import datetime
from airflow.exceptions import AirflowSkipException
from presidio.utils.airflow.services.fixed_duration_strategy import is_last_interval_of_fixed_duration


class FixedDurationOperator(JarOperator):
    """
    
    The FixedDurationOperator creates java_args and updates the JarOperator.
    java_args contain start_date, end_date and fixed_duration_strategy.
    
    FixedDurationOperator executes the task only if interval greater then fixed_duration
    or execution_date is the last interval of fixed_duration.
    
    
     :param fixed_duration_strategy: duration (e.g. hourly or daily)
     :type fixed_duration_strategy: datetime.timedelta
    """

    @apply_defaults
    def __init__(self, fixed_duration_strategy, java_args={}, *args, **kwargs):
        self.interval = kwargs.get('dag').schedule_interval
        self.fixed_duration_strategy = fixed_duration_strategy
        java_args.update({'fixed_duration_strategy': fixed_duration_strategy.total_seconds()})
        super(FixedDurationOperator, self).__init__(java_args=java_args, *args, **kwargs)

    def execute(self, context):
        """
        
        Checks if execution_date is last interval of fixed duration, then creates java args, otherwise skip the task. 
        java args include start_date, end_date and fixed_duration_strategy
        """
        if not is_last_interval_of_fixed_duration(context['execution_date'], self.fixed_duration_strategy,
                                                  self.interval):
            FixedDurationOperator.skip_task(context)
        else:
            start_date = TimeService.round_time(context['execution_date'], time_delta=self.fixed_duration_strategy)
            end_date = self.get_end_date_value(start_date)
            java_args = {
                'start_date': start_date.isoformat(),
                'end_date': end_date.isoformat()
            }
            super(FixedDurationOperator, self).update_java_args(java_args)
            super(FixedDurationOperator, self).execute(context)

    def get_end_date_value(self, start_date):
        """
        
        Compute end_date value according to interval and fixed_duration_strategy
        :param start_date: 
        :type start_date: datetime.datetime
        :return: datetime.datetime
        """
        start_date = TimeService.datetime_to_epoch(start_date)
        interval = self.interval.total_seconds()
        fixed_duration_strategy = self.fixed_duration_strategy.total_seconds()

        if interval < fixed_duration_strategy:
            end_date = start_date + fixed_duration_strategy
        else:
            end_date = start_date + self.interval.total_seconds()

        return TimeService.epoch_to_datetime(end_date)

    @staticmethod
    def skip_task(context):
        """
        Skipped the task
        :param context: 
        :return: 
        """
        logging.info('Skipping task...')
        session = settings.Session()
        task = context['task']
        ti = TaskInstance(
            task, execution_date=context['ti'].execution_date)
        ti.state = State.SKIPPED
        ti.start_date = datetime.now()
        ti.end_date = datetime.now()
        session.merge(ti)
        session.commit()
        session.close()
        logging.info("Done.")
        raise AirflowSkipException
