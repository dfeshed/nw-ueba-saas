import logging
import sys
import os

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils.manager import OnlineManager

logger = logging.getLogger('stepSAM')


class Manager(OnlineManager):
    def __init__(self,
                 host,
                 is_online_mode,
                 start,
                 tables,
                 wait_between_batches,
                 min_free_memory,
                 polling_interval,
                 timeout,
                 max_delay):
        super(Manager, self).__init__(host=host,
                                      is_online_mode=is_online_mode,
                                      start=start,
                                      block_on_tables=tables,
                                      wait_between_batches=wait_between_batches,
                                      min_free_memory=min_free_memory,
                                      polling_interval=polling_interval,
                                      max_delay=max_delay,
                                      batch_size_in_hours=1)
        self._timeout = timeout

    def _run_batch(self, start_time_epoch):
        # TODO: implement
        pass
