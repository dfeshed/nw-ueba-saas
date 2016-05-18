import logging

import subprocess
import os
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from validation.missing_events.validation import validate_no_missing_events, validate_models_synced
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
import bdp_utils.manager


logger = logging.getLogger('step4')


class Manager:
    def __init__(self,
                 host,
                 validation_timeout,
                 validation_polling):
        self._manager = bdp_utils.manager.Manager(logger=logger,
                                                  host=host,
                                                  path_to_bdp_properties='BdpEntityEventsCreation.properties',
                                                  block=True)
        self._host = host
        self._validation_timeout = validation_timeout * 60
        self._validation_polling = validation_polling * 60

    def run(self):
        for step in [self._run_bdp, self._sync_models]:
            if not step():
                return False
        return True

    def _run_bdp(self):
        self._manager.infer_start_and_end(collection_names_regex='^entity_event_').run()
        is_valid = validate_no_missing_events(host=self._host,
                                              timeout=self._validation_timeout,
                                              start=self._manager.get_start(),
                                              end=self._manager.get_end())
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
