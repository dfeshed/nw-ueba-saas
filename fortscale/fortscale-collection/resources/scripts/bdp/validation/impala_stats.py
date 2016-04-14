from impala.dbapi import connect

from config import HOST

impala_connection = connect(host=HOST, port=21050)
DATA_SOURCE_TO_IMPALA_TABLE = {
    'kerberos_logins': 'authenticationscores',
    'kerberos_tgt': 'kerberostgtscore',
    'vpn_session': 'vpnsessiondatares',
}


def get_impala_table_name(data_source):
    available_table_names = get_all_impala_table_names()
    table_name_options = [data_source + 'score', data_source + 'scores', data_source + 'datares']
    if DATA_SOURCE_TO_IMPALA_TABLE.has_key(data_source):
        table_name_options.append(DATA_SOURCE_TO_IMPALA_TABLE[data_source])
    for table_name in table_name_options:
        if table_name in available_table_names:
            return table_name
    return None


def get_all_impala_table_names():
    cursor = impala_connection.cursor()
    cursor.execute('show tables')
    res = [res[0] for res in cursor.fetchall()]

    global get_all_impala_table_names
    get_all_impala_table_names = lambda: res
    return get_all_impala_table_names()


def get_sum_from_impala(data_source, start_time_partition, end_time_partition, is_daily):
    table_name = get_impala_table_name(data_source)
    if table_name is None:
        raise Exception("Data source " + data_source + " does not have a mapping to an impala table. " +
                        "Please update the script's source code and run again")
    time_resolution = 60 * 60 * 24 if is_daily else 60 * 60
    cursor = impala_connection.cursor()
    cursor.execute('select floor(date_time_unix / ' + str(time_resolution) + ') * ' + str(time_resolution) +
                   ' as time_bucket, count(*) from ' + table_name +
                   ' where yearmonthday >= ' + start_time_partition +
                   ' and yearmonthday < ' + end_time_partition +
                   ' group by time_bucket')
    return dict(cursor)
