import datetime
from dateutil.parser import parse


def interval_to_str(start_time, end_time):
    return timestamp_to_str(start_time) + ' -> ' + timestamp_to_str(end_time)


def timestamp_to_str(time):
    if type(time) != datetime.datetime:
        time = datetime.datetime.utcfromtimestamp(time)
    return str(time)


def string_to_epoch(time):
    return (parse(str(time)) - datetime.datetime.utcfromtimestamp(0)).total_seconds()


def time_to_impala_partition(time):
    if type(time) != datetime.datetime:
        time = datetime.datetime.utcfromtimestamp(time)
    return ''.join([str(time.year), '%02d' % time.month, '%02d' % time.day])
