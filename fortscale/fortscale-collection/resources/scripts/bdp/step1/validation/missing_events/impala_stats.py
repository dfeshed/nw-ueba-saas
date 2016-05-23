import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils.data_sources import data_source_to_enriched_tables, data_source_to_score_tables
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..']))
from automatic_config.common.utils import time_utils, impala_utils


def _create_interval_where_clause(start, end):
    return 'where date_time_unix >= ' + str(time_utils.get_epoch(start)) + \
           ' and date_time_unix < ' + str(time_utils.get_epoch(end)) + \
           ' and yearmonthday >= ' + time_utils.get_impala_partition(start) + \
           ' and yearmonthday < ' + time_utils.get_impala_partition(end)


def _get_num_of_events(events_counter, start, end):
    return sum(events_counter(partition)
               for partition in time_utils.get_impala_partitions(start, end))


def get_num_of_enriched_events(host, data_source, start, end):
    connection = impala_utils.connect(host)
    cursor = connection.cursor()
    where_clause = {
        'vpn': 'and status != "CLOSED"',
        'vpn_session': 'and status = "CLOSED"'
    }.get(data_source, '')
    cursor.execute('select count(*) from ' + data_source_to_enriched_tables[data_source] +
                   ' ' + _create_interval_where_clause(start, end) +
                   ' ' + where_clause)
    res = cursor.next()[0]
    cursor.close()
    return res


def get_num_of_scored_events(host, data_source, start, end):
    connection = impala_utils.connect(host)
    cursor = connection.cursor()
    cursor.execute('select count(*) from ' + data_source_to_score_tables[data_source] +
                   ' ' + _create_interval_where_clause(start, end))
    res = cursor.next()[0]
    cursor.close()
    return res
