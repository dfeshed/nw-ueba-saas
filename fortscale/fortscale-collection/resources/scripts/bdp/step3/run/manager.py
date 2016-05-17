import logging

import os
import sys
from mongo_stats import get_aggr_collections_boundary
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from validation.missing_events.validation import validate_no_missing_events
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils.run import run as run_bdp

logger = logging.getLogger('step3')


class Manager:
    def __init__(self,
                 host,
                 validation_timeout):
        if not os.path.isfile(self._get_bdp_properties_file_name()):
            raise Exception(self._get_bdp_properties_file_name() +
                            ' does not exist. Please download this file from https://drive.google.com/drive/u/0/folders/0B8CUEFciXBeYOE5KZ2dIeUc3Y1E')
        self._host = host
        self._validation_timeout = validation_timeout

    @staticmethod
    def _get_bdp_properties_file_name():
        return '/home/cloudera/devowls/BdpAggregatedEventsToEntityEvents.properties'

    def run(self):
        self._run_bdp()
        self._sync()
        self._run_auto_config()
        self._cleanup()
        self._run_bdp()

    def _run_bdp(self):
        start = get_aggr_collections_boundary(host=self._host, is_start=True)
        end = get_aggr_collections_boundary(host=self._host, is_start=False)
        # make sure we're dealing with integer hours
        end += (start - end) % (60 * 60)
        kill_process = run_bdp(logger=logger,
                               path_to_bdp_properties=self._get_bdp_properties_file_name(),
                               start=start,
                               end=end,
                               block=False)
        validate_no_missing_events(host=self._host,
                                   timeout=self._validation_timeout * 60,
                                   start=start,
                                   end=end)
        logger.info('making sure bdp process exits...')
        kill_process()

    def _sync(self):
        #TODO: implement
        pass

    def _run_auto_config(self):
        #TODO: implement
        pass

    def _cleanup(self):
        #TODO: implement
        pass
