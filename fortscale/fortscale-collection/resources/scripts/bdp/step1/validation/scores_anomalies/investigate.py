from impala.dbapi import connect
import os
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils.data_sources import data_source_to_score_tables
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..']))
from automatic_config.common.utils import time_utils


def get_num_of_positive_scores(connection, table_name, score_field_name, partition):
    cursor = connection.cursor()
    cursor.execute('select count(*) from ' + table_name +
                   ' where yearmonthday=' + partition + ' and ' + score_field_name + ' > 0')
    res = cursor.next()[0]
    cursor.close()
    return res


def investigate(host, data_source, score_field_name, date):
    connection = connect(host=host, port=21050)
    table_name = data_source_to_score_tables[data_source]
    partition = time_utils.get_impala_partition(date)
    num_of_positive_scores = get_num_of_positive_scores(connection=connection,
                                                        table_name=table_name,
                                                        score_field_name=score_field_name,
                                                        partition=partition)
    cursor = connection.cursor()
    cursor.execute('select normalized_src_machine, count(*) cnt, count(*) over ()'
                   ' from ' + table_name +
                   ' where yearmonthday=' + partition +
                   ' and ' + score_field_name + ' > 0' +
                   ' group by normalized_src_machine'
                   ' order by cnt desc')
    for value, num_of_positive_scores_of_value, num_of_values_with_positive_scores in cursor:
        num_of_positive_scores_per_value_mean = 1. * num_of_positive_scores / num_of_values_with_positive_scores
        print '"' + value + '"', num_of_positive_scores_of_value, num_of_positive_scores_per_value_mean
        print 'continue from here'
        return
    cursor.close()
