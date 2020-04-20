from datetime import datetime, timedelta


def datetime_to_epoch(dt):
    """
    Convert datetime to epoch
    :param dt: date_time
    :type dt: datetime
    :return: float
    """
    epoch = datetime.utcfromtimestamp(0)
    return (dt - epoch).total_seconds()
