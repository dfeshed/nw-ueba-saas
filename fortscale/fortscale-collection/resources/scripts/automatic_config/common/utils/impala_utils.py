from impala.dbapi import connect as cn

import time_utils


def connect(host):
    return cn(host=host, port=21050 if host != 'upload' else 31050)


def get_partitions(connection, table, start=None, end=None):
    c = connection.cursor()
    c.execute('show partitions ' + table)
    partitions = [p[0] for p in c if p[0] != 'Total' and
                  (start is None or time_utils.get_impala_partition(start) <= p[0]) and
                  (end is None or p[0] < time_utils.get_impala_partition(end))]
    c.close()
    return partitions


def _get_boundary_event_time(connection, table, is_first):
    boundary_event_time = None
    partitions = get_partitions(connection=connection, table=table)
    for partition in partitions if is_first else reversed(partitions):
        c = connection.cursor()
        c.execute('select ' + ('min' if is_first else 'max') + '(date_time_unix) from ' +
                  table + ' where yearmonthday=' + partition)
        boundary_event_time = c.next()[0]
        c.close()
        if boundary_event_time is not None:
            break
    return boundary_event_time


def get_first_event_time(connection, table):
    return _get_boundary_event_time(connection=connection, table=table, is_first=True)


def get_last_event_time(connection, table):
    return _get_boundary_event_time(connection=connection, table=table, is_first=False)
