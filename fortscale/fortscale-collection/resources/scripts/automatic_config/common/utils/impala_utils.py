from impala.dbapi import connect as cn


def connect(host):
    return cn(host=host, port=21050 if host != 'upload' else 31050)


def get_partitions(connection, table):
    c = connection.cursor()
    c.execute('show partitions ' + table)
    partitions = [p[0] for p in c if p[0] != 'Total']
    c.close()
    return partitions


def get_last_event_time(connection, table):
    for partition in reversed(get_partitions(connection=connection, table=table)):
        c = connection.cursor()
        c.execute('select max(date_time_unix) from ' + table + ' where yearmonthday=' + partition)
        last_event_time = c.next()[0]
        c.close()
        if last_event_time is not None:
            break
    return last_event_time
