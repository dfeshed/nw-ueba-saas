from datetime import datetime, timedelta


class TimeService(object):
    def __init__(self):
        pass

    @staticmethod
    def round_time(dt, time_delta):
        """
        Round time down based on date_delta
        :param dt: date_time
        :type dt: datetime
        :param time_delta: e.g: timedelta(hours=1)
        :type time_delta: timedelta
        :return: datetime
        """
        round_time = TimeService.datetime_to_epoch(dt) // time_delta.total_seconds() * time_delta.total_seconds()
        return TimeService.epoch_to_datetime(round_time)

    @staticmethod
    def datetime_to_epoch(dt):
        """
        Convert datetime to epoch
        :param dt: date_time
        :type dt: datetime
        :return: float
        """
        epoch = datetime.utcfromtimestamp(0)
        return (dt - epoch).total_seconds()

    @staticmethod
    def epoch_to_datetime(epoch):
        """
        Convert epoch to datetime
        :param epoch: float
        :type epoch: float
        :return: datetime
        """
        return datetime.utcfromtimestamp(epoch)
