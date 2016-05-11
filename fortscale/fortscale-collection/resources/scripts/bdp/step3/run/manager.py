import logging
import shutil
import subprocess
import signal
import math

import os
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from validation.missing_events.validation import validate_no_missing_events
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from automatic_config.common.utils import time_utils

logger = logging.getLogger('step3')


class Manager:
    def __init__(self,
                 host,
                 validation_timeout):
        if not os.path.isfile(self._get_bdp_properties_file_name(the_one_that_should_be_used=True)):
            raise Exception(self._get_bdp_properties_file_name(the_one_that_should_be_used=True) +
                            ' does not exist. Please download this file from google drive')
        self._host = host
        self._validation_timeout = validation_timeout

    @staticmethod
    def _get_bdp_properties_file_name(the_one_that_should_be_used):
        return '/home/cloudera/devowls/BdpAggregatedEventsToEntityEvents.properties' \
            if the_one_that_should_be_used \
            else '/home/cloudera/fortscale/BDPtool/target/resources/bdp.properties'

    def run(self):
        shutil.copyfile(self._get_bdp_properties_file_name(the_one_that_should_be_used=True),
                        self._get_bdp_properties_file_name(the_one_that_should_be_used=False))
        start = self._get_first_event_time()
        duration_hours = math.ceil((self._get_last_event_time() - start) / (60 * 60.))
        call_args = ['nohup',
                     'java',
                     '-Duser.timezone=UTC',
                     '-jar',
                     'bdp-0.0.1-SNAPSHOT.jar',
                     'bdp_start_time=' + time_utils.get_datetime(start).strftime("%Y-%m-%d %H:%M:%S"),
                     'bdp_duration_hours=' + duration_hours,
                     'batch_duration_size=' + duration_hours]
        output_file_name = 'BdpAggregatedEventsToEntityEvents.out'
        logger.info('running ' + ' '.join(call_args) + ' > ' + output_file_name)
        with open(output_file_name, 'w') as f:
            pid = subprocess.Popen(call_args,
                                   cwd='/home/cloudera/fortscale/BDPtool/target',
                                   stdout=f).pid
        self._validate()
        self._kill_bdp(pid)

    def _get_first_event_time(self):
        #TODO: implement
        return 1

    def _get_last_event_time(self):
        #TODO: implement
        return 1

    @staticmethod
    def _kill_bdp(pid):
        logger.info('making sure bdp process exist...')
        try:
            os.kill(pid, signal.SIGTERM)
        except OSError:
            pass

    def _validate(self):
        if not validate_no_missing_events(host=self._host,
                                          timeout=self._validation_timeout * 60):
            logger.error('validation failed')
