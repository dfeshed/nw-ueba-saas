from impala.dbapi import connect as cn

import time
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


def _get_boundary_event_time(connection, table, is_first, limit_start, limit_end):
    boundary_event_time = None
    partitions = get_partitions(connection=connection, table=table)
    for partition in partitions if is_first else reversed(partitions):
        c = connection.cursor()
        c.execute('select ' + ('min' if is_first else 'max') + '(date_time_unix) from ' +
                  table + ' where yearmonthday = ' + partition +
                  (' and date_time_unix >= ' + str(time_utils.get_epochtime(limit_start)) if limit_start else '') +
                  (' and date_time_unix < ' + str(time_utils.get_epochtime(limit_end)) if limit_end else ''))
        boundary_event_time = c.next()[0]
        c.close()
        if boundary_event_time is not None:
            break
    return boundary_event_time


def get_first_event_time(connection, table, limit_start=None, limit_end=None):
    return _get_boundary_event_time(connection=connection,
                                    table=table,
                                    is_first=True,
                                    limit_start=limit_start,
                                    limit_end=limit_end)


def get_last_event_time(connection, table, limit_start=None, limit_end=None):
    return _get_boundary_event_time(connection=connection,
                                    table=table,
                                    is_first=False,
                                    limit_start=limit_start,
                                    limit_end=limit_end)


def calc_count_per_time_bucket(host, table, time_granularity_minutes, start, end, timeout):
    if 24 * 60 % time_granularity_minutes != 0:
        raise Exception('time_granularity_minutes must divide a day to equally sized buckets')
    connection = connect(host=host)
    count_per_time_bucket = []
    start_time = time.time()
    for partition in get_partitions(connection=connection,
                                    table=table,
                                    start=start,
                                    end=end):
        count_per_time_bucket += _get_count_per_time_bucket(connection=connection,
                                                            table=table,
                                                            partition=partition,
                                                            time_granularity_minutes=time_granularity_minutes)
        if 0 <= timeout < time.time() - start_time:
            break
    return count_per_time_bucket


def _get_count_per_time_bucket(connection, table, partition, time_granularity_minutes):
    if 24 * 60 % time_granularity_minutes != 0:
        raise Exception('time_granularity_minutes must divide a day to equally sized buckets')
    c = connection.cursor()
    time_granularity_seconds = 60 * time_granularity_minutes
    c.execute('select floor(date_time_unix / ' + str(time_granularity_seconds) + ')' + ' * ' +
              str(time_granularity_seconds) + ' time_bucket, count(*) from ' + table +
              ' where yearmonthday = ' + partition + ' group by time_bucket')
    buckets = dict(((time_utils.get_epochtime(partition) + minute * 60) / (time_granularity_seconds) * (time_granularity_seconds), 0)
                   for minute in xrange(60 * 24))
    buckets.update(dict(c))
    c.close()
    return sorted(buckets.iteritems())
