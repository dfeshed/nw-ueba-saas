from presidio.utils.airflow.services.time_service import TimeService


def is_last_interval_of_fixed_duration(date_time, fixed_duration_strategy, interval):
    """
    Checks whether the given date_time is the last interval of fixed_duration_strategy
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
        date = TimeService.datetime_to_epoch(date_time)
        date_round = TimeService.round_time(date_time,
                                            time_delta=fixed_duration_strategy)
        time = TimeService.datetime_to_epoch(date_round) + fixed_duration_strategy.total_seconds() - interval

        is_last = date >= time

    return is_last
