from datetime import timedelta
from presidio.utils.exceptions import UnsupportedFixedDurationStrategyError
from presidio.utils.airflow.services.time_service import TimeService


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


def is_last_hour_of_day(date_time):
    """
    Checks whether the given timestamp is of the last hour of the day.
    :param date_time: The timestamp to check
    :type date_time: datetime.datetime
    :return: True if the timestamp's hour is 23, false otherwise
    """

    return True if date_time.hour == 23 else False

