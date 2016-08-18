import logging
import os
import sys

from job import run as run_job

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from validation.validation import block_until_everything_is_validated
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils.kafka import send
from bdp_utils.manager import OnlineManager
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from automatic_config.common.utils import time_utils

logger = logging.getLogger('step2')


class Manager(OnlineManager):
    def __init__(self,
                 host,
                 is_online_mode,
                 start,
                 block_on_tables,
                 calc_block_on_tables_based_on_days,
                 wait_between_batches,
                 min_free_memory_gb,
                 polling_interval,
                 timeout,
                 validation_batches_delay,
                 max_delay,
                 batch_size_in_hours):
        super(Manager, self).__init__(logger=logger,
                                      host=host,
                                      is_online_mode=is_online_mode,
                                      start=start,
                                      block_on_tables=block_on_tables,
                                      calc_block_on_tables_based_on_days=calc_block_on_tables_based_on_days,
                                      wait_between_batches=wait_between_batches,
                                      min_free_memory_gb=min_free_memory_gb,
                                      polling_interval=polling_interval,
                                      max_delay=max_delay,
                                      batch_size_in_hours=batch_size_in_hours)
        if is_online_mode and timeout is not None:
            raise Exception('in online mode there should be no timeout')
        self._timeout = timeout
        self._validation_batches_delay = validation_batches_delay
        self._is_online_mode = is_online_mode

    def _run_batch(self, start_time_epoch):
        run_job(start_time_epoch=start_time_epoch,
                batch_size_in_hours=self._batch_size_in_hours,
                is_online_mode=self._is_online_mode)
        validation_start_time = \
            start_time_epoch - self._validation_batches_delay * self._batch_size_in_hours * 60 * 60
        block_until_everything_is_validated(host=self._host,
                                            start_time_epoch=validation_start_time,
                                            end_time_epoch=validation_start_time + self._batch_size_in_hours * 60 * 60,
                                            wait_between_validations=self._polling_interval,
                                            max_delay=self._max_delay,
                                            timeout=0,
                                            polling_interval=0)
        return True

    def run(self):
        is_success = super(Manager, self).run()
        if not is_success:
            return is_success
        logger.info('sending dummy event (so the last partial batch will be closed)...')
        validation_end_time = time_utils.get_epochtime(self._last_batch_end_time)
        send(logger=logger,
             host=self._host,
             topic='fortscale-vpn-event-score-from-hdfs',
             message='{\\"data_source\\": \\"dummy\\", \\"date_time_unix\\": ' +
                     str(validation_end_time + 1) + '}')
        logger.info('validating last partial batch...')
        validation_start_time = \
            validation_end_time - self._validation_batches_delay * self._batch_size_in_hours * 60 * 60
        block_until_everything_is_validated(host=self._host,
                                            start_time_epoch=validation_start_time,
                                            end_time_epoch=validation_end_time,
                                            wait_between_validations=self._polling_interval,
                                            max_delay=self._max_delay,
                                            timeout=self._timeout,
                                            polling_interval=self._polling_interval)
        return True
