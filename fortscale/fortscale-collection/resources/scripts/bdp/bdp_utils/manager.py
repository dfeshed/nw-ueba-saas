import datetime
import re
import subprocess
import sys
import time
import os

from data_sources import data_source_to_score_tables
from log import log_and_send_mail
from run import Cleaner
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from automatic_config.common.utils import time_utils, impala_utils, io
from automatic_config.common.utils.mongo import rename_documents


def cleanup_everything_but_models(logger,
                                  host,
                                  clean_overrides_key,
                                  start_time_epoch=None,
                                  end_time_epoch=None,
                                  infer_start_and_end_from_collection_names_regex=None,
                                  fail_if_no_models=True):
    if (start_time_epoch is None) ^ (end_time_epoch is None) or \
            not (start_time_epoch is None) ^ (infer_start_and_end_from_collection_names_regex is None):
        raise ValueError()
    logger.info('renaming model collections (to protect them from cleanup)...')
    models_backup_prefix = 'backup_'
    renames = rename_documents(logger=logger,
                               host=host,
                               collection_names_regex='^model_',
                               name_to_new_name_cb=lambda name: models_backup_prefix + name)
    if renames == 0:
        if fail_if_no_models:
            logger.error('failed to rename collections')
            return False
        else:
            logger.warning('no models in mongo')

    logger.info('running cleanup...')
    cleaner = Cleaner(name=clean_overrides_key,
                      logger=logger,
                      host=host)
    if infer_start_and_end_from_collection_names_regex is not None:
        cleaner.infer_start_and_end(collection_names_regex=infer_start_and_end_from_collection_names_regex)
    else:
        cleaner.set_start(start_time_epoch).set_end(end_time_epoch)
    is_success = cleaner.run(overrides_key=clean_overrides_key,
                             overrides=['data_sources = ' + ','.join(data_source_to_score_tables.iterkeys())])

    logger.info('renaming model collections back...')
    if rename_documents(logger=logger,
                        host=host,
                        collection_names_regex='^' + models_backup_prefix + 'model_',
                        name_to_new_name_cb=lambda name: name[len(models_backup_prefix):]) != renames:
        logger.error('failed to rename collections back')
        return False

    logger.info('DONE')
    return is_success


class OverridingManager(object):
    def __init__(self, logger):
        self._logger = logger

    def run(self):
        self._logger.info('preparing configurations...')
        original_to_backup = self._backup_and_override()
        self._logger.info('DONE')
        try:
            return self._run()
        finally:
            self._revert_configurations(original_to_backup)

    def _revert_configurations(self, original_to_backup):
        self._logger.info('reverting configurations...')
        for original, backup in original_to_backup.iteritems():
            os.remove(original)
            if backup is not None:
                os.rename(backup, original)
        self._logger.warning("DONE. Don't forget to restart relevant stuff (e.g. - samza tasks) in order to apply them")

    def _backup_and_override(self):
        raise NotImplementedException()

    def _run(self):
        raise NotImplementedException()


class DontReloadModelsOverridingManager(OverridingManager):
    _FORTSCALE_OVERRIDING_PATH = '/home/cloudera/fortscale/streaming/config/fortscale-overriding-streaming.properties'

    def __init__(self, logger):
        super(DontReloadModelsOverridingManager, self).__init__(logger=logger)

    def _backup_and_override(self):
        self._logger.info('updating fortscale-overriding-streaming.properties...')
        original_to_backup = {
            DontReloadModelsOverridingManager._FORTSCALE_OVERRIDING_PATH: io.backup(path=DontReloadModelsOverridingManager._FORTSCALE_OVERRIDING_PATH) \
                if os.path.isfile(DontReloadModelsOverridingManager._FORTSCALE_OVERRIDING_PATH) \
                else None
        }
        really_big_epochtime = time_utils.get_epochtime('29990101')
        configuration = [
            '',
            'fortscale.model.wait.sec.between.loads=' + str(really_big_epochtime),
            'fortscale.model.max.sec.diff.before.outdated=' + str(really_big_epochtime)
        ]
        self._logger.info('overriding the following:' + '\n\t'.join(configuration))
        with open(DontReloadModelsOverridingManager._FORTSCALE_OVERRIDING_PATH, 'a') as f:
            f.write('\n'.join(configuration))
        return original_to_backup


class OnlineManager(object):
    def __init__(self,
                 logger,
                 host,
                 is_online_mode,
                 start,
                 block_on_tables,
                 wait_between_batches,
                 min_free_memory,
                 polling_interval,
                 max_delay,
                 batch_size_in_hours):
        self._logger = logger
        self._impala_connection = impala_utils.connect(host=host)
        self._is_online_mode = is_online_mode
        self._last_job_real_time = time.time()
        self._last_batch_end_time = time_utils.get_datetime(start)
        self._block_on_tables = block_on_tables
        self._wait_between_batches = wait_between_batches
        self._min_free_memory = min_free_memory
        self._polling_interval = polling_interval
        self._max_delay = max_delay
        self._batch_size_in_hours = batch_size_in_hours

    def _run_batch(self, start_time_epoch):
        raise NotImplementedException()

    def run(self):
        res = True
        while res:
            if self._is_online_mode:
                self._wait_until(self._reached_next_barrier)
            self._wait_until(self._enough_memory)
            if not self._is_online_mode and not self._reached_next_barrier()[0]:
                self._logger.info("there's not enough data to fill a whole batch - running partial batch...")
                res = self._run_next_batch()
                self._logger.info('DONE - no more data')
                break
            self._logger.info(str(self._batch_size_in_hours) + ' hour' +
                              ('s' if self._batch_size_in_hours > 1 else '') + ' have been filled')
            res = self._run_next_batch()
        return res

    def _wait_until(self, cb):
        while True:
            is_success, fail_msg = cb()
            if is_success:
                return
            if 0 <= self._max_delay < time.time() - self._last_job_real_time:
                log_and_send_mail('failed for more than ' +
                                  str(int(self._max_delay / (60 * 60))) + ' hours: ' + fail_msg)
            self._logger.info(fail_msg + '. going to sleep for ' + str(int(self._polling_interval / 60)) +
                              ' minute' + ('s' if self._polling_interval / 60 > 1 else ''))
            time.sleep(self._polling_interval)

    def _reached_next_barrier(self):
        self._logger.info('polling impala tables (to see if we can run next batch ' +
                          time_utils.interval_to_str(self._last_batch_end_time,
                                                     self._last_batch_end_time +
                                                     datetime.timedelta(hours=self._batch_size_in_hours)) + ')...')
        for table in self._block_on_tables:
            if not self._has_table_reached_barrier(table=table):
                return False, 'data sources have not filled an hour yet'
        return True, None

    def _enough_memory(self):
        output = subprocess.Popen(['free', '-b'], stdout=subprocess.PIPE).communicate()[0]
        free_memory = int(re.search('(\d+)\W*$', output.split('\n')[2]).groups()[0])
        if free_memory >= self._min_free_memory:
            return True, None
        return False, 'not enough free memory (only ' + str(free_memory / 1024 ** 3) + ' GB)'

    def _run_next_batch(self):
        self._logger.info('running next batch...')
        self._last_job_real_time = time.time()
        last_batch_end_time_epoch = time_utils.get_epochtime(self._last_batch_end_time)
        if not self._run_batch(start_time_epoch=last_batch_end_time_epoch):
            self._logger.error('running batch failed')
            return False
        self._last_batch_end_time += datetime.timedelta(hours=self._batch_size_in_hours)
        wait_time = self._wait_between_batches - (time.time() - self._last_job_real_time)
        if wait_time > 0:
            self._logger.info('going to sleep for ' + str(int(wait_time / 60)) + ' minutes')
            time.sleep(wait_time)
        return True

    def _has_table_reached_barrier(self, table):
        last_event_time = impala_utils.get_last_event_time(connection=self._impala_connection, table=table)
        if last_event_time is not None and time_utils.get_timedelta_total_seconds(
                        time_utils.get_datetime(last_event_time) - self._last_batch_end_time) >= \
                                self._batch_size_in_hours * 60 * 60:
            self._logger.info('impala table ' + table + ' has reached to at least ' + str(last_event_time))
            return True
        self._logger.info('impala table ' + table + ' has not enough data since last batch')
        return False
