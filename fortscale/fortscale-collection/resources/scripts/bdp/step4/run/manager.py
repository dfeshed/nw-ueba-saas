import logging

import os
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from validation.missing_events.validation import validate_no_missing_events
from validation.distribution.validation import validate_distribution
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
import bdp_utils.run
from bdp_utils.kafka import send


logger = logging.getLogger('step4')


class Manager:
    def __init__(self,
                 host,
                 validation_timeout,
                 validation_polling):
        self._runner = bdp_utils.run.Runner(name='BdpEntityEventsCreation',
                                            logger=logger,
                                            host=host,
                                            block=True)
        self._host = host
        self._validation_timeout = validation_timeout
        self._validation_polling_interval = validation_polling

    def run(self):
        for step in [self._run_bdp, self._sync_models]:
            if not step():
                return False
        return True

    def _run_bdp(self):
        self._runner.infer_start_and_end(collection_names_regex='^entity_event_(?!meta_data)').run(overrides_key='step4')
        is_valid = validate_no_missing_events(host=self._host,
                                              timeout=self._validation_timeout,
                                              polling=self._validation_polling_interval,
                                              start=self._runner.get_start(),
                                              end=self._runner.get_end())
        validate_distribution(host=self._host)
        return is_valid

    def _sync_models(self):
        logger.info('syncing models...')
        send(logger=logger,
             host=self._host,
             topic='fortscale-aggregated-feature-event-prevalence-stats-control',
             message='{\\"type\\": \\"model_sync\\"}')
        return True
