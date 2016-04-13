import logging
logging.basicConfig(level=logging.INFO)
import datetime
import time
from impala.dbapi import connect
from subprocess import call
from data_sources import data_source_to_score_tables
import sys
sys.path.append(__file__ + r'\..\..')
from automatic_config.common.utils import time_utils



class Synchronizer:
    _HOUR = 60 * 60

    def __init__(self, host, start, block_on_tables, wait_between_syncs, polling_interval):
        self._connection = connect(host=host, port=21050)
        self._last_synched = start
        self._tables = block_on_tables
        self._wait_between_syncs = wait_between_syncs
        self._polling_interval = polling_interval

    def run(self):
        sync_batch_size_in_hours = 1
        while True:
            logging.info('polling impala tables (query if we can sync ' +
                         time_utils.interval_to_str(self._last_synched,
                                                    self._last_synched + datetime.timedelta(hours=sync_batch_size_in_hours)) + ')...')
            barrier = min([self._get_last_event(table) for table in self._tables])
            if (barrier - self._last_synched).total_seconds() >= Synchronizer._HOUR:
                logging.info('an hour has been filled - running bdp for ' + str(sync_batch_size_in_hours) +
                             ' hour' + ('s' if sync_batch_size_in_hours > 1 else ''))
                start_syncing_time = time.time()
                self._run_bdp(sync_batch_size_in_hours)
                self._last_synched += datetime.timedelta(hours=sync_batch_size_in_hours)
                wait_time = self._wait_between_syncs - (time.time() - start_syncing_time)
                if wait_time > 0:
                    logging.info('going to sleep for ' + str(int(wait_time / 60)) + ' minutes')
                    time.sleep(wait_time)
            else:
                logging.info('not enough data - going to sleep for ' + str(int(self._polling_interval / 60)) +
                             ' minute' + ('s' if self._polling_interval / 60 > 1 else ''))
                time.sleep(self._polling_interval)

    def _get_last_event(self, table):
        c = self._connection.cursor()
        c.execute('select max(date_time) from ' + table +
                  ' where yearmonthday=' + time_utils.time_to_impala_partition(self._last_synched) +
                  ' or yearmonthday=' + (time_utils.time_to_impala_partition(self._last_synched + datetime.timedelta(days=1))))
        res = c.next()[0] or datetime.datetime.fromtimestamp(0)
        logging.info('impala table ' + table + ' has reached to ' + str(res))
        return res

    def _run_bdp(self, hours_to_run):
        call(['echo',
              'nohup',
              'java',
              '-jar',
              '-Duser.timezone=UTC',
              'fortscale-collection-1.1.0-SNAPSHOT.jar',
              'ScoringToAggregation',
              'Forwarding',
              'securityDataSources=' + ','.join(data_source_to_score_tables.iterkeys()),
              'retries=60',
              'batchSize=500000000',
              'startTime=' + str(int((self._last_synched - datetime.datetime.fromtimestamp(0)).total_seconds() * 1000)),
              'hoursToRun=' + str(hours_to_run)])
