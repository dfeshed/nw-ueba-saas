import logging

import os
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils.run import run as run_bdp
from bdp_utils.mongo import get_collections_time_boundary


logger = logging.getLogger('step5')


class Manager:
    def __init__(self, host):
        if not os.path.isfile(self._get_bdp_properties_file_name()):
            raise Exception(self._get_bdp_properties_file_name() + ' does not exist. Please download this file from '
                                                                   'https://dri`e.google.com/drive/u/0/folders/0B8CUEFciXBeYOE5KZ2dIeUc3Y1E')
        self._host = host

    @staticmethod
    def _get_bdp_properties_file_name():
        return '/home/cloudera/devowls/BdpNotificationsToIndicators.properties'

    def run(self):
        collection_names_regex = '^scored___entity_event_'
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
                block=True,
                additional_cmd_params=['data_sources=kerberos'])
        return True
