import logging
import time
from impala.dbapi import connect

logger = logging.getLogger('step1')


class Manager:
    def __init__(self,
                 host,
                 table_name,
                 max_batch_size,
                 force_max_batch_size_minutes,
                 max_gap):
        self._impala_connection = connect(host=host, port=21050)
        self._table_name = table_name
        self._max_batch_size = max_batch_size
        self._force_max_batch_size_minutes = force_max_batch_size_minutes
        self._max_gap = max_gap

    def run(self):
        pass

    def _get_partitions(self):
        c = self._impala_connection.cursor()
        c.execute('show partitions ' + self._table_name)
        partitions = [p[0] for p in c]
        c.close()
        return partitions

    def _get_count_per_minute(self, partition):
        c = self._impala_connection.cursor()
        c.execute('select count(*) from ' + self._table_name +
                  ' where yearmonthday = ' + partition + ' group by floor(date_time_unix / 60)')
        count_per_minute = [res[0] for res in c]
        c.close()
        return count_per_minute

    def get_max_batch_size_in_minutes(self):
        if self._force_max_batch_size_minutes is not None:
            return self._force_max_batch_size_minutes
        TIMEOUT = 30
        count_per_minute = []
        start_time = time.time()
        for partition in self._get_partitions():
            count_per_minute += self._get_count_per_minute(partition)
            if time.time() - start_time > TIMEOUT:
                break
        for batch_time_in_minutes in xrange(len(count_per_minute), 0, -1):
            for batch_start in xrange(0, len(count_per_minute), batch_time_in_minutes):
                if sum(count_per_minute[batch_start:batch_start + batch_time_in_minutes]) > self._max_batch_size:
                    break
            else:
                return batch_time_in_minutes
