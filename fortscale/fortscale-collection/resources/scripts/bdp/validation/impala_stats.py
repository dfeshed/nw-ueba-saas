import datetime
import sys
from impala.dbapi import connect

from config import HOST

sys.path.append(__file__ + r'\..\..\..')
from automatic_config.common.utils import time_utils


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



def round_to_lower_day_boundary(time):
    rounded_date = datetime.datetime.utcfromtimestamp(time).date()
    return (rounded_date - datetime.date(1970, 1, 1)).total_seconds()


def get_sum_from_impala(data_source, start_time_epoch, end_time_epoch, is_daily):
    if is_daily:
        start_time_epoch = round_to_lower_day_boundary(start_time_epoch)
        end_time_epoch = round_to_lower_day_boundary(end_time_epoch)
    table_name = get_impala_table_name(data_source)
    if table_name is None:
        raise Exception("Data source " + data_source + " does not have a mapping to an impala table. " +
                        "Please update the script's source code and run again")
    time_resolution = 60 * 60 * 24 if is_daily else 60 * 60
    cursor = impala_connection.cursor()
    cursor.execute('select floor(date_time_unix / ' + str(time_resolution) + ') * ' + str(time_resolution) +
                   ' as time_bucket, count(*) from ' + table_name +
                   ' where yearmonthday >= ' + time_utils.time_to_impala_partition(start_time_epoch) +
                   ' and yearmonthday <= ' + time_utils.time_to_impala_partition(end_time_epoch - 1) +
                   ' and date_time_unix >= ' + str(start_time_epoch) +
                   ' and date_time_unix < ' + str(end_time_epoch) +
                   ' group by time_bucket')
    return dict(cursor )
