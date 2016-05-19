import logging

import zipfile
import shutil
import subprocess
import os
import datetime
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from validation import validate_no_missing_events, validate_entities_synced, validate_cleanup_complete
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils.mongo import get_collections_time_boundary
import bdp_utils.runner

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
        self._runner = bdp_utils.runner.Runner(step_id='BdpAggregatedEventsToEntityEvents',
                                               logger=logger,
                                               host=host,
                                               block=False)
        self._cleaner = bdp_utils.runner.Runner(step_id='BdpCleanupAggregatedEventsToEntityEvents',
                                                logger=logger,
                                                host=host,
                                                block=True)
        self._host = host
        self._validation_timeout = validation_timeout * 60
        self._validation_polling = validation_polling * 60
        self._days_to_ignore = days_to_ignore

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
        kill_process = self._runner.infer_start_and_end(collection_names_regex='^aggr_').run(overrides_key='step3.run')
        is_valid = validate_no_missing_events(host=self._host,
                                              timeout=self._validation_timeout,
                                              start=self._runner.get_start(),
                                              end=self._runner.get_end())
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
        return validate_entities_synced(host=self._host,
                                        timeout=self._validation_timeout,
                                        polling=self._validation_polling)

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
                                              collection_names='^aggr_',
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
        self._cleaner.run(overrides_key='step3.cleanup')
        return validate_cleanup_complete(host=self._host,
                                         timeout=self._validation_timeout,
                                         polling=self._validation_polling)

    def _restart_kafka(self):
        #TODO implement
        return True
