import pytz
from datetime import datetime, timedelta


def floor_time(dt, time_delta):
    """
    Round time down based on date_delta
    :param dt: date_time
    :type dt: datetime
    :param time_delta: e.g: timedelta(hours=1)
    :type time_delta: timedelta
    :return: datetime
    """
    time = datetime_to_epoch(dt) // time_delta.total_seconds() * time_delta.total_seconds()
    return epoch_to_datetime(time)


def datetime_to_epoch(dt):
    """
    Convert datetime to epoch
    :param dt: date_time
    :type dt: datetime
    :return: float
    """
    epoch = datetime.utcfromtimestamp(0)
    return (dt - epoch).total_seconds()


def epoch_to_datetime(epoch):
    """
    Convert epoch to datetime
    :param epoch: float
    :type epoch: float
    :return: datetime
    """
    return datetime.utcfromtimestamp(epoch)


def convert_to_utc(dt):
    """
    Convert datetime to utc format 2017-06-06T10:10:10.00Z
    :param dt: date_time
    :type dt: datetime
    :return: float
    """
    if (dt.tzname() is None) | (dt.tzinfo == pytz.utc):
        return dt.strftime('%Y-%m-%dT%H:%M:%SZ')
    else:
        raise Exception('We support only UTC time zone')
