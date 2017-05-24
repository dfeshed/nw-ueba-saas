from presidio.utils.airflow.operators.jar_operator import JarOperator
from airflow.utils.decorators import apply_defaults
from presidio.utils.airflow.services.time_service import TimeService


class PresidioJarOperator(JarOperator):
    """
    
    The PresidioJarOperator creates java_args and updates the JarOperator.
    java_args contain start_date, end_date and fixed_duration_strategy.
    PresidioJarOperator validates fixed duration strategy and start_date.
    
    
     :param fixed_duration_strategy: duration (e.g. hourly or daily)
     :type fixed_duration_strategy: datetime.timedelta
    """

    @apply_defaults
    def __init__(self, fixed_duration_strategy, java_args={}, *args, **kwargs):
        self.interval = kwargs.get('dag').schedule_interval
        self.fixed_duration_strategy = fixed_duration_strategy
        java_args.update({'fixed_duration_strategy': fixed_duration_strategy.total_seconds()})
        super(PresidioJarOperator, self).__init__(java_args=java_args, *args, **kwargs)

    def pre_execute(self, context):
        """
        
        Create and validate java args, 
        java args include start_date, end_date and fixed_duration_strategy
        """
        start_date = TimeService.round_time(context['execution_date'], time_delta=self.fixed_duration_strategy)
        start_date = TimeService.datetime_to_epoch(start_date)
        end_date = start_date + self.interval.total_seconds()
        fixed_duration_strategy = self.fixed_duration_strategy.total_seconds()

        fixed_duration_valid = self.validate_fixed_duration(fixed_duration_strategy)
        start_date_valid = self.validate_start_date(start_date, fixed_duration_strategy)

        if fixed_duration_valid and start_date_valid:
            java_args = {
                'start_date': TimeService.epoch_to_datetime(start_date).isoformat(),
                'end_date': TimeService.epoch_to_datetime(end_date).isoformat()
            }
            super(PresidioJarOperator, self).update_java_args(java_args)

    def validate_fixed_duration(self, fixed_duration_strategy):
        """
        
        Validate that fixed duration adjust to the interval
        :param fixed_duration_strategy: 
        :raise UnValidFixedDurationStrategyError
        :return: boolean
        """
        interval = self.interval.total_seconds()
        valid = interval % fixed_duration_strategy == 0

        if not valid:
            raise InValidFixedDurationStrategyError(fixed_duration_strategy, interval)

        return valid

    @staticmethod
    def validate_start_date(start_date, fixed_duration_strategy):
        """
        
        Validate that the start_date rounded to the beginning of fixed_duration_strategy
        :param start_date: 
        :param fixed_duration_strategy: 
        :raise UnValidStartDateError
        :return: boolean
        """
        valid = start_date % fixed_duration_strategy == 0

        if not valid:
            raise InValidStartDateError(start_date, fixed_duration_strategy)

        return valid


class InValidFixedDurationStrategyError(ValueError):
    def __init__(self, fixed_duration, interval):
        message = 'Fixed duration strategy {} does not adjust to the interval {}.'.format(fixed_duration, interval)
        super(InValidFixedDurationStrategyError, self).__init__(message)


class InValidStartDateError(ValueError):
    def __init__(self, fixed_duration, start_date):
        message = 'Start date {} does not adjust to the fixed duration strategy {}.'.format(start_date, fixed_duration)
        super(InValidStartDateError, self).__init__(message)
