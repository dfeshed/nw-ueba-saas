import logging

import os
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from step4.validation.distribution.validation import validate_distribution
import bdp_utils.run


logger = logging.getLogger('2.7-step4')


class Manager:
    def __init__(self, host, days_to_ignore):
        self._runner = bdp_utils.run.Runner(name='2.7-BdpEntityEventsCreation.run',
                                            logger=logger,
                                            host=host,
                                            block=True)
        self._builder = bdp_utils.run.Runner(name='2.7-BdpEntityEventsCreation.build_models',
                                             logger=logger,
                                             host=host,
                                             block=True)
        self._host = host
        self._days_to_ignore = days_to_ignore

    def run(self):
        self._runner.infer_start_and_end(collection_names_regex='^entity_event_')
        self._builder.set_start(self._runner.get_end()).set_end(self._runner.get_end())
        for step in [lambda: self._run_bdp(days_to_ignore=self._days_to_ignore),
                     self._build_models,
                     self._move_models_back_in_time,
                     self._cleanup,
                     lambda: self._run_bdp(days_to_ignore=self._days_to_ignore),
                     self._build_models,
                     self._move_models_back_in_time,
                     self._cleanup,
                     lambda: self._run_bdp(days_to_ignore=0),
                     self._validate]:
            if not step():
                return False
        return True

    def _run_bdp(self, days_to_ignore):
        start = self._runner.get_start()
        self._runner.set_start(start + days_to_ignore * 60 * 60 * 24)
        self._runner.run(overrides_key='2.7-step4.run')
        self._runner.set_start(start)
        return True

    def _build_models(self):
        self._builder.run(overrides_key='2.7-step4.build_models')
        return True

    def _move_models_back_in_time(self):
        # TODO: implement
        pass

    def _cleanup(self):
        # TODO: implement
        pass

    def _validate(self):
        validate_distribution(host=self._host)
        return True
