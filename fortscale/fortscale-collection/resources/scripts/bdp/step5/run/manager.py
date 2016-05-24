import logging

import os
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
import bdp_utils.runner

logger = logging.getLogger('step5')


class Manager:
    def __init__(self, host):
        self._runner = bdp_utils.run.Runner(name='BdpNotificationsToIndicators',
                                            logger=logger,
                                            host=host,
                                            block=True)

    def run(self):
        self._runner \
            .infer_start_and_end(collection_names_regex='^scored___entity_event_') \
            .run(overrides_key='step5')
