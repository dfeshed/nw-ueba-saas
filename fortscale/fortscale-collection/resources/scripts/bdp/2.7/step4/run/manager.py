import logging

import os
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from step4.validation.distribution.validation import validate_distribution
import bdp_utils.run


logger = logging.getLogger('2.7-step4')


class Manager:
    def __init__(self, host):
        self._runner = bdp_utils.run.Runner(name='2.7-BdpEntityEventsCreation',
                                            logger=logger,
                                            host=host,
                                            block=True)
        self._host = host

    def run(self):
        for step in [self._run_bdp, self._validate]:
            if not step():
                return False
        return True

    def _run_bdp(self):
        self._runner \
            .infer_start_and_end(collection_names_regex='^entity_event_') \
            .run(overrides_key='2.7-step4.without_models')
        return True

    def _validate(self):
        validate_distribution(host=self._host)
        return True
