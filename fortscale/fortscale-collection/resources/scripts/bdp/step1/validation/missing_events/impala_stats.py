import os
import sys
from impala.dbapi import connect

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..']))
from utils.data_sources import data_source_to_enriched_tables, data_source_to_score_tables


def get_num_of_enriched_events(host, data_source):
    connection = connect(host=host, port=21050)
    cursor = connection.cursor()
    where_clause = {
        'vpn': 'where status != "CLOSED"',
        'vpn_session': 'where status = "CLOSED"'
    }.get(data_source, '')
    cursor.execute('select count(*) from ' + data_source_to_enriched_tables[data_source] + ' ' + where_clause)
    res = cursor.next()[0]
    cursor.close()
    return res


def get_num_of_scored_events(host, data_source):
    connection = connect(host=host, port=21050)
    cursor = connection.cursor()
    cursor.execute('select count(*) from ' + data_source_to_score_tables[data_source])
    res = cursor.next()[0]
    cursor.close()
    return res
