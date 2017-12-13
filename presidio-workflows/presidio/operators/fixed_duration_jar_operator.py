import logging

from airflow.utils.decorators import apply_defaults

from presidio.utils.airflow.context_wrapper import ContextWrapper
from presidio.utils.airflow.operators.spring_boot_jar_operator import SpringBootJarOperator
from presidio.utils.services.fixed_duration_strategy import is_execution_date_valid
from presidio.utils.services.time_service import convert_to_utc
from presidio.utils.services.time_service import floor_time


class FixedDurationJarOperator(SpringBootJarOperator):
    """
    
    The FixedDurationJarOperator creates java_args and updates the JarOperator.
    java_args contain start_date, end_date and fixed_duration_strategy.
    
    FixedDurationJarOperator executes the task only if interval greater then fixed_duration
    or execution_date is the last interval of fixed_duration.
    
    
     :param fixed_duration_strategy: duration (e.g. hourly or daily)
     :type fixed_duration_strategy: datetime.timedelta
    """

    @apply_defaults
    def __init__(self, fixed_duration_strategy, command, java_args={}, *args, **kwargs):
        self.interval = kwargs.get('dag').schedule_interval
        self.fixed_duration_strategy = fixed_duration_strategy
        self.add_fixed_duration_strategy(java_args)
        retry_extra_params = {}
        retry_extra_params['fixed_duration_strategy'] = fixed_duration_strategy
        retry_extra_params['schedule_interval'] = self.interval

        super(FixedDurationJarOperator, self).__init__(java_args=java_args, command=command, retry_extra_params=retry_extra_params,
                                                       retry_java_args_method=FixedDurationJarOperator.add_java_args, *args, **kwargs)

    def add_fixed_duration_strategy(self, java_args):
        java_args.update({'fixed_duration_strategy': self.fixed_duration_strategy.total_seconds()})

    def execute(self, context):
        """
   
        Checks if execution_date is last interval of fixed duration, then creates java args, otherwise skip the task. 
        java args include start_date, end_date and fixed_duration_strategy
           
        :raise InvalidExecutionDateError - Raise error if the execution_date is not the last interval of fixed duration.
        """
        context_wrapper = ContextWrapper(context)
        execution_date = context_wrapper.get_execution_date()
        if not is_execution_date_valid(execution_date, self.fixed_duration_strategy,
                                       self.interval):
            # Create short_circuit_operator in order to skip the task before it executes.
            # e.g: execution_date = datetime(2014, 11, 28, 13, 50, 0)
            # interval = timedelta(minutes=5)
            # fixed_duration = timedelta(days=1)
            logging.error(
                'Create short_circuit_operator in order to skip the task.')
            raise InvalidExecutionDateError(execution_date, self.fixed_duration_strategy)

        start_date = floor_time(execution_date, time_delta=self.fixed_duration_strategy)
        end_date = floor_time(execution_date + self.interval,
                              time_delta=self.fixed_duration_strategy)
        utc_start_date = convert_to_utc(start_date)
        utc_end_date = convert_to_utc(end_date)
        java_args = {
            'start_date': utc_start_date,
            'end_date': utc_end_date
        }
        super(FixedDurationJarOperator, self).update_java_args(java_args)
        super(FixedDurationJarOperator, self).execute(context)

    @staticmethod
    def add_java_args(context):
        params = context['params']
        fixed_duration_strategy = params['retry_extra_params']['fixed_duration_strategy']
        interval = params['retry_extra_params']['schedule_interval']
        context_wrapper = ContextWrapper(context)
        execution_date = context_wrapper.get_execution_date()

        if not is_execution_date_valid(execution_date, fixed_duration_strategy,
                                       interval):
            logging.error(
                'Create short_circuit_operator in order to skip the task.')
            raise InvalidExecutionDateError(execution_date, fixed_duration_strategy)

        start_date = floor_time(execution_date, time_delta=fixed_duration_strategy)
        end_date = floor_time(execution_date + interval,
                              time_delta=fixed_duration_strategy)
        utc_start_date = convert_to_utc(start_date)
        utc_end_date = convert_to_utc(end_date)
        java_args = {
            'start_date': utc_start_date,
            'end_date': utc_end_date
        }
        java_args = ' '.join(SpringBootJarOperator.java_args_prefix + '%s %s' % (key, val) for (key, val) in java_args.iteritems())
        return java_args


class InvalidExecutionDateError(ValueError):
    """
    Raise Error if the execution_date is not the last interval of fixed duration.
    """

    def __init__(self, execution_date, fixed_duration):
        message = 'The execution date {} is not the last interval of fixed duration {}.'.format(execution_date,
                                                                                                fixed_duration)
        super(InvalidExecutionDateError, self).__init__(message)
