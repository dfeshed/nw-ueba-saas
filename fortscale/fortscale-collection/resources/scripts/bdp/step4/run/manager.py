import logging

import subprocess
import os
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from validation.missing_events.validation import validate_no_missing_events, validate_models_synced
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils.run import run as run_bdp
from bdp_utils.mongo import get_collections_time_boundary


logger = logging.getLogger('step4')


class Manager:
    def __init__(self,
                 host,
                 validation_timeout,
                 validation_polling):
        if not os.path.isfile(self._get_bdp_properties_file_name()):
            raise Exception(self._get_bdp_properties_file_name() + ' does not exist. Please download this file from '
                                                                   'https://drive.google.com/drive/u/0/folders/0B8CUEFciXBeYOE5KZ2dIeUc3Y1E')
        self._host = host
        self._validation_timeout = validation_timeout * 60
        self._validation_polling = validation_polling * 60

    @staticmethod
    def _get_bdp_properties_file_name():
        return '/home/cloudera/devowls/BdpEntityEventsCreation.properties'

    def run(self):
        for step in [self._run_bdp, self._sync_models]:
            if not step():
                return False
        return True

    def _run_bdp(self):
        collection_names_regex = '^entity_event_'
        start = get_collections_time_boundary(host=self._host,
                                              collection_names_regex=collection_names_regex,
                                              is_start=True)
        end = get_collections_time_boundary(host=self._host,
                                            collection_names_regex=collection_names_regex,
                                            is_start=False)
        # make sure we're dealing with integer hours
        end += (start - end) % (60 * 60)
        run_bdp(logger=logger,
                path_to_bdp_properties=self._get_bdp_properties_file_name(),
                start=start,
                end=end,
                block=True)
        is_valid = validate_no_missing_events(host=self._host,
                                              timeout=self._validation_timeout,
                                              start=start,
                                              end=end)
        return is_valid

    def _sync_models(self):
        echo_args = [
            'echo',
            '{\\"type\": \\"model_sync\\"}'
        ]
        kafka_console_producer_args = [
            'kafka-console-producer',
            '--broker-list', self._host + ':9092',
            '--topic', 'fortscale-aggregated-feature-event-prevalence-stats-control'
        ]
        logger.info('syncing models: ' + ' '.join(echo_args) + ' | ' + ' '.join(kafka_console_producer_args))
        echo_p = subprocess.Popen(echo_args, stdout=subprocess.PIPE)
        kafka_p = subprocess.Popen(kafka_console_producer_args, stdin=echo_p.stdout)
        kafka_p.wait()
        return validate_models_synced()
