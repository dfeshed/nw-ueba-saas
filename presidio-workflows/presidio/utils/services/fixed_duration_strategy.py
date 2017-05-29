from presidio.utils.services.time_service import floor_time, datetime_to_epoch
from datetime import timedelta
from presidio.utils.exceptions import UnsupportedFixedDurationStrategyError

FIX_DURATION_STRATEGY_HOURLY = timedelta(hours=1)
FIX_DURATION_STRATEGY_DAILY = timedelta(days=1)


def is_execution_date_valid(date_time, fixed_duration_strategy, interval):
    """
    If interval greater than fixed_duration the date_time is valid,
    Otherwise checks whether the given date_time is the last interval of fixed_duration_strategy
    
    e.g:
        valid date_time:
            date_time = datetime(2014, 11, 28, 13, 55, 0)
            interval = timedelta(minutes=5)
            fixed_duration = timedelta(days=1)           
        invalid date_time:
            date_time = datetime(2014, 11, 28, 13, 40, 0)
            interval = timedelta(minutes=5)
            fixed_duration = timedelta(days=1)
    
    :param date_time: The timestamp to check
    :type date_time: datetime.datetime
    :param fixed_duration_strategy: 
    :type fixed_duration_strategy: datetime.timedelta
    :param interval: 
    :type interval: datetime.timedelta
    :return: boolean
    """
    is_last = True
    interval = interval.total_seconds()

    if interval < fixed_duration_strategy.total_seconds():
        date = datetime_to_epoch(date_time)
        date_round = floor_time(date_time,
                                time_delta=fixed_duration_strategy)
        time = datetime_to_epoch(date_round) + fixed_duration_strategy.total_seconds() - interval

        is_last = date >= time

    return is_last


def fixed_duration_strategy_to_string(fixed_duration_strategy):
    """
    Returns a string representation of the given fixed_duration_strategy.
    If the strategy is not supported by Presidio, the function raises an error.
    :param fixed_duration_strategy: The duration that will be converted to a string
    :type fixed_duration_strategy: datetime.timedelta
    :return: The string representation
    """

    if fixed_duration_strategy == timedelta(hours=1):
        return 'hourly'
    elif fixed_duration_strategy == timedelta(days=1):
        return 'daily'
    else:
        raise UnsupportedFixedDurationStrategyError(fixed_duration_strategy)
