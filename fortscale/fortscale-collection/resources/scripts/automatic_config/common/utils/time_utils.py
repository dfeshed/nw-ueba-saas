import datetime
from dateutil.parser import parse


def interval_to_str(start_time, end_time):
    return timestamp_to_str(start_time) + ' -> ' + timestamp_to_str(end_time)


def timestamp_to_str(time):
    if type(time) != datetime.datetime:
        time = datetime.datetime.utcfromtimestamp(time)
    return str(time)


def get_timedelta_total_seconds(time):
    return (time.microseconds + (time.seconds + time.days * 24. * 3600) * 10 ** 6) / (10 ** 6)


def time_to_epoch(time):
    if type(time) == int and 20000101 < time < 99991230:  # TODO: fix before the year 10000
        time = str(time)  # we're dealing with yearmonthday
    if type(time) == str:
        time = parse(time)
    return get_timedelta_total_seconds(time - datetime.datetime.utcfromtimestamp(0))


def time_to_impala_partition(time):
    if type(time) != datetime.datetime:
        time = datetime.datetime.utcfromtimestamp(time)
    return ''.join([str(time.year), '%02d' % time.month, '%02d' % time.day])
