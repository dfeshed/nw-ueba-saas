import logging
from datetime import datetime, timedelta
from presidio.utils.services.time_service import floor_time

DEFAULT_DATE = datetime(2014, 11, 28, 10, 10, 2)


def test_hourly_round_time():
    """
    Test hourly round_time method
    :return: 
    """
    logging.info('Test hourly round time method')
    assert floor_time(DEFAULT_DATE, timedelta(hours=1)) == datetime(2014, 11, 28, 10, 0, 0)


def test_daily_round_time():
    """
    Test daily round_time method
    :return: 
    """
    logging.info('Test daily round time method')
    assert floor_time(DEFAULT_DATE, timedelta(days=1)) == datetime(2014, 11, 28, 0, 0, 0)


def test_weekly_round_time():
    """
    Test weekly round_time method
    :return: 
    """
    logging.info('Test weekly round time method')
    assert floor_time(DEFAULT_DATE, timedelta(weeks=1)) == datetime(2014, 11, 27, 0, 0, 0)
