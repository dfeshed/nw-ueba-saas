import os
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils.data_sources import data_source_to_enriched_tables


def calc_time_to_process_most_sparse_day(connection, data_sources):
    events_per_day = {}
    for data_source in data_sources:
        for yearmonthday, count in _calc_time_to_process_most_sparse_day(connection=connection,
                                                                         data_source=data_source).iteritems():
            events_per_day[yearmonthday] = events_per_day.get(yearmonthday, 0) + count
    # first and last day might contain only partial data, so ignore them
    min_count = min([count for yearmonthday, count in sorted(events_per_day.iteritems())[1:-2]])
    # in Cisco we see this throughput:
    events_per_hour_processing_rate = 6000000 / 1.5
    time_to_process_min_count_in_hours = min_count / events_per_hour_processing_rate
    return int(time_to_process_min_count_in_hours * 60 * 60)


def _calc_time_to_process_most_sparse_day(connection, data_source):
    c = connection.cursor()
    c.execute('select yearmonthday, count(*) from ' + data_source_to_enriched_tables[data_source] +
              ' group by yearmonthday')
    events_per_day = dict(c)
    c.close()
    return events_per_day
