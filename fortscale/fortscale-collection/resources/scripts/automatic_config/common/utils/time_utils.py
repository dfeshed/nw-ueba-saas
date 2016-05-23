import datetime
import numbers
from dateutil.parser import parse


def interval_to_str(start_time, end_time):
    return timestamp_to_str(start_time) + ' -> ' + timestamp_to_str(end_time)


def timestamp_to_str(time):
    if type(time) != datetime.datetime:
        time = datetime.datetime.utcfromtimestamp(time)
    return str(time)


def get_timedelta_total_seconds(timedelta):
    return timedelta.seconds + timedelta.days * 24 * 3600


def get_epochtime(time):
    if type(time) == str and time.isdigit():
        time = long(time)
    if isinstance(time, numbers.Number):
        if 20000101 < time < 99991230:  # TODO: fix before the year 10000
            time = str(time)  # we're dealing with yearmonthday
        else:
            return time  # already is epoch time
    if type(time) in [str, unicode]:
        time = parse(time)
    return get_timedelta_total_seconds(time - datetime.datetime.utcfromtimestamp(0))


def get_datetime(time):
    return datetime.datetime.utcfromtimestamp(get_epochtime(time))


def get_impala_partition(time):
    time = get_epochtime(time)
    time = datetime.datetime.utcfromtimestamp(time)
    return ''.join([str(time.year), '%02d' % time.month, '%02d' % time.day])


def get_impala_partitions(start, end):
    start = get_datetime(start)
    end = get_datetime(end)
    partitions = []
    while start < end:
        partitions.append(get_impala_partition(start))
        start += datetime.timedelta(days=1)
    return partitions
