import logging

import subprocess
import os
import sys
from mongo_stats import get_aggr_collections_boundary, get_num_of_entity_event_metadatas
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from validation.missing_events.validation import validate_no_missing_events
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils.run import run as run_bdp
from bdp_utils.run import validate_by_polling

logger = logging.getLogger('step3')


class Manager:
    def __init__(self,
                 host,
                 validation_timeout,
                 validation_polling):
        if not os.path.isfile(self._get_bdp_properties_file_name()):
            raise Exception(self._get_bdp_properties_file_name() +
                            ' does not exist. Please download this file from https://drive.google.com/drive/u/0/folders/0B8CUEFciXBeYOE5KZ2dIeUc3Y1E')
        self._host = host
        self._validation_timeout = validation_timeout * 60
        self._validation_polling = validation_polling * 60

    @staticmethod
    def _get_bdp_properties_file_name():
        return '/home/cloudera/devowls/BdpAggregatedEventsToEntityEvents.properties'

    def run(self):
        for step in [self._run_bdp,
                     self._sync,
                     self._run_auto_config,
                     self._cleanup,
                     self._run_bdp]:
            if not step():
                return False
        return True

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
        is_valid = validate_no_missing_events(host=self._host,
                                              timeout=self._validation_timeout,
                                              start=start,
                                              end=end)
        logger.info('making sure bdp process exits...')
        kill_process()
        return is_valid
        
    def _sync(self):
        echo_args = [
            'echo',
            '{\\"type\": \\"entity_event_sync\\"}'
        ]
        kafka_console_producer_args = [
            'kafka-console-producer',
            '--broker-list', self._host + ':9092',
            '--topic', 'fortscale-entity-event-stream-control'
        ]
        logger.info('syncing entities: ' + ' '.join(echo_args) + ' | ' + ' '.join(kafka_console_producer_args))
        echo_p = subprocess.Popen(echo_args, stdout=subprocess.PIPE)
        kafka_p = subprocess.Popen(kafka_console_producer_args, stdin=echo_p.stdout)
        kafka_p.wait()
        return self._validate_sync()

    def _validate_sync(self):
        logger.info('validating entities synced...')
        if validate_by_polling(status_cb=lambda: get_num_of_entity_event_metadatas(host=self._host),
                               status_target=0,
                               no_progress_timeout=self._validation_timeout,
                               polling=self._validation_polling):
            logger.info('OK')
            return True
        else:
            logger.error('FAIL')
            return False

    def _run_auto_config(self):
        #TODO: implement
        return True

    def _cleanup(self):
        #TODO: implement
        return True
