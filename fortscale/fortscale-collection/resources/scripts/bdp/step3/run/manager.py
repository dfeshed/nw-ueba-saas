import logging

import zipfile
import shutil
import subprocess
import os
import re
import datetime
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from mongo_stats import get_collections_size
from validation.missing_events.validation import validate_no_missing_events
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils.run import run as run_bdp
from bdp_utils.run import validate_by_polling
from bdp_utils.mongo import get_all_aggr_collection_names, get_collections_time_boundary

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from automatic_config.common.utils import time_utils
from automatic_config.common import config
from automatic_config.fs_reduction import main as fs_main
from automatic_config.alphas_and_betas import main as weights_main
from automatic_config.common.results.committer import update_configurations


logger = logging.getLogger('step3')


class Manager:
    def __init__(self,
                 host,
                 validation_timeout,
                 validation_polling,
                 days_to_ignore):
        for file_name in [self._get_bdp_properties_file_name(), self._get_bdp_cleanup_properties_file_name()]:
            if not os.path.isfile(file_name):
                raise Exception(file_name + ' does not exist. Please download this file from '
                                            'https://drive.google.com/drive/u/0/folders/0B8CUEFciXBeYOE5KZ2dIeUc3Y1E')
        self._host = host
        self._validation_timeout = validation_timeout * 60
        self._validation_polling = validation_polling * 60
        self._days_to_ignore = days_to_ignore

    @staticmethod
    def _get_bdp_properties_file_name():
        return '/home/cloudera/devowls/BdpAggregatedEventsToEntityEvents.properties'

    @staticmethod
    def _get_bdp_cleanup_properties_file_name():
        return '/home/cloudera/devowls/BdpCleanupAggregatedEventsToEntityEvents.properties'

    def run(self):
        for step in [self._run_bdp,
                     self._sync_entities,
                     self._run_automatic_config,
                     self._cleanup,
                     self._restart_kafka,
                     self._run_bdp]:
            if not step():
                return False
        return True

    def _run_bdp(self):
        aggr_collection_names = get_all_aggr_collection_names(host=self._host)
        start = get_collections_time_boundary(host=self._host,
                                              collection_names=aggr_collection_names,
                                              is_start=True)
        end = get_collections_time_boundary(host=self._host,
                                            collection_names=aggr_collection_names,
                                            is_start=False)
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

    def _sync_entities(self):
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
        return self._validate_collections_are_empty(log_msg='validating entities synced...',
                                                    collection_names_regex='^entity_event_meta_data')

    def _validate_collections_are_empty(self, log_msg, collection_names_regex):
        logger.info(log_msg)
        if validate_by_polling(status_cb=lambda: get_collections_size(host=self._host,
                                                                      collection_names_regex=collection_names_regex),
                               status_target=0,
                               no_progress_timeout=self._validation_timeout,
                               polling=self._validation_polling):
            logger.info('OK')
            return True
        else:
            logger.error('FAIL')
            return False

    def _run_automatic_config(self):
        # extract entity_events.json to the overriding folder
        jar_filename = '/home/cloudera/fortscale/streaming/lib/fortscale-aggregation-1.1.0-SNAPSHOT.jar'
        logger.info('extracting entity_events.json from ' + jar_filename + '...')
        zf = zipfile.ZipFile(jar_filename, 'r')
        with open('/home/cloudera/fortscale/config/asl/entity_events/overriding/entity_events.json', 'w') as f:
            shutil.copyfileobj(zf.open('config/asl/entity_events.json', 'r'), f)
        zf.close()
        # calculate Fs reducers and alphas and betas
        logger.info('calculating Fs reducers...')
        start = get_collections_time_boundary(host=self._host,
                                              collection_names=get_all_aggr_collection_names(host=self._host),
                                              is_start=True)
        config.START_TIME = start
        fs_main.run_algo()
        start = time_utils.get_datetime(start)
        config.START_TIME = time_utils.get_epoch(datetime.datetime(year=start.year,
                                                                   month=start.month,
                                                                   day=start.day)) + 60 * 60 * 24 * self._days_to_ignore
        logger.info('calculating alphas and betas (ignoring first', self._days_to_ignore, 'days)...')
        weights_main.run_algo()
        # commit everything
        logger.info('updating configuration files with Fs reducers and alphas and betas...')
        update_configurations()
        return True

    def _cleanup(self):
        run_bdp(logger=logger,
                path_to_bdp_properties=self._get_bdp_cleanup_properties_file_name(),
                start=None,
                end=None,
                block=True)
        return self._validate_collections_are_empty(log_msg='validating cleanup 1/2...',
                                                    collection_names_regex='(^scored___aggr_event__|^entity_event_.*_(daily|hourly))') and \
               self._validate_collections_are_empty(log_msg='validating cleanup 2/2...',
                                                    collection_names_regex='^streaming_models$',
                                                    find_query={'modelName': re.compile('^aggr', re.IGNORECASE)})

    def _restart_kafka(self):
        #TODO implement
        return True
