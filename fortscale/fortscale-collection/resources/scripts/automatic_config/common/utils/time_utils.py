import datetime
from dateutil.parser import parse


def interval_to_str(start_time, end_time):
    return timestamp_to_str(start_time) + ' -> ' + timestamp_to_str(end_time)

def timestamp_to_str(time):
    return str(datetime.datetime.fromtimestamp(time))

def string_to_epoch(time):
    return (parse(str(time)) - datetime.datetime.utcfromtimestamp(0)).total_seconds()
