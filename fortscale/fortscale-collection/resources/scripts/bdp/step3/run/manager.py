import logging

import os
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from validation.missing_events.validation import validate_no_missing_events
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils import run as run_bdp

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
        start = self._get_first_event_time()
        end = self._get_last_event_time()
        # make sure we're dealing with integer hours
        end = (start - end) % (60 * 60)
        p = run_bdp(logger=logger,
                    path_to_bdp_properties=self._get_bdp_properties_file_name(),
                    start=start,
                    end=end,
                    block=True)
        validate_no_missing_events(host=self._host,
                                   timeout=self._validation_timeout * 60,
                                   start=self._get_first_event_time(),
                                   end=self._get_last_event_time())
        logger.info('making sure bdp process exist...')
        p.kill()

    def _get_first_event_time(self):
        #TODO: implement
        return 1

    def _get_last_event_time(self):
        #TODO: implement
        return 1
