import logging

import os
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
import bdp_utils.manager

logger = logging.getLogger('step5')


class Manager:
    def __init__(self, host):
        self._manager = bdp_utils.manager.Manager(logger=logger,
                                                  host=host,
                                                  bdp_properties_file_name='BdpNotificationsToIndicators.properties',
                                                  block=True)

    def run(self):
        self._manager \
            .infer_start_and_end(collection_names_regex='^scored___entity_event_') \
            .run(additional_cmd_params=['data_sources=kerberos'])
        return True
