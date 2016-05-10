import os
import sys
from impala.dbapi import connect

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..']))
from utils.data_sources import data_source_to_enriched_tables, data_source_to_score_tables
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from automatic_config.common.utils import time_utils


def _connect(host):
    return connect(host=host, port=21050 if host != 'upload' else 31050)


def _create_interval_where_clause(partition, start, end):
    return 'where date_time_unix >= ' + str(time_utils.get_epoch(start)) + \
           ' and date_time_unix < ' + str(time_utils.get_epoch(end)) + \
           ' and yearmonthday=' + partition


def _get_num_of_events(events_counter, start, end):
    return sum(events_counter(partition)
               for partition in time_utils.get_impala_partitions(start, end))


def get_num_of_enriched_events(host, data_source, start, end):
    connection = _connect(host)

    def counter(partition):
        cursor = connection.cursor()
        where_clause = {
            'vpn': 'and status != "CLOSED"',
            'vpn_session': 'and status = "CLOSED"'
        }.get(data_source, '')
        cursor.execute('select count(*) from ' + data_source_to_enriched_tables[data_source] +
                       ' ' + _create_interval_where_clause(partition, start, end) +
                       ' ' + where_clause)
        res = cursor.next()[0]
        cursor.close()
        return res
    return _get_num_of_events(counter, start, end)


def get_num_of_scored_events(host, data_source, start, end):
    connection = _connect(host)

    def counter(partition):
        cursor = connection.cursor()
        cursor.execute('select count(*) from ' + data_source_to_score_tables[data_source] +
                       ' ' + _create_interval_where_clause(partition, start, end))
        res = cursor.next()[0]
        cursor.close()
        return res
    return _get_num_of_events(counter, start, end)
