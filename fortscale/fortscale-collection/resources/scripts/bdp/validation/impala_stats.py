import datetime
import os
import sys
from impala.dbapi import connect

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from automatic_config.common.utils import time_utils


_DATA_SOURCE_TO_IMPALA_TABLE = {
    'kerberos_logins': 'authenticationscores',
    'kerberos_tgt': 'kerberostgtscore',
    'vpn_session': 'vpnsessiondatares',
}


def _get_impala_table_name(connection, data_source):
    available_table_names = _get_all_impala_table_names(connection)
    table_name_options = [data_source + 'score', data_source + 'scores', data_source + 'datares']
    if _DATA_SOURCE_TO_IMPALA_TABLE.has_key(data_source):
        table_name_options.append(_DATA_SOURCE_TO_IMPALA_TABLE[data_source])
    for table_name in table_name_options:
        if table_name in available_table_names:
            return table_name
    return None


def _get_all_impala_table_names(connection):
    cursor = connection.cursor()
    cursor.execute('show tables')
    res = [res[0] for res in cursor.fetchall()]

    global _get_all_impala_table_names
    _get_all_impala_table_names = lambda connection: res
    return _get_all_impala_table_names(connection)


def _round_to_lower_day_boundary(time):
    rounded_date = datetime.datetime.utcfromtimestamp(time).date()
    return time_utils.get_timedelta_total_seconds(rounded_date - datetime.date(1970, 1, 1))


def get_sum_from_impala(host, data_source, start_time_epoch, end_time_epoch, is_daily):
    connection = connect(host=host, port=21050)
    if is_daily:
        start_time_epoch = _round_to_lower_day_boundary(start_time_epoch)
        end_time_epoch = _round_to_lower_day_boundary(end_time_epoch)
    table_name = _get_impala_table_name(connection=connection, data_source=data_source)
    if table_name is None:
        raise Exception("Data source " + data_source + " does not have a mapping to an impala table. " +
                        "Please update the script's source code and run again")
    time_resolution = 60 * 60 * 24 if is_daily else 60 * 60
    cursor = connection.cursor()
    cursor.execute('select floor(date_time_unix / ' + str(time_resolution) + ') * ' + str(time_resolution) +
                   ' as time_bucket, count(*) from ' + table_name +
                   ' where yearmonthday >= ' + time_utils.time_to_impala_partition(start_time_epoch) +
                   ' and yearmonthday <= ' + time_utils.time_to_impala_partition(end_time_epoch - 1) +
                   ' and date_time_unix >= ' + str(int(start_time_epoch)) +
                   ' and date_time_unix < ' + str(int(end_time_epoch)) +
                   ' group by time_bucket')
    return dict(cursor )
