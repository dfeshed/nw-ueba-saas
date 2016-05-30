import logging

import zipfile
import shutil
import os
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from validation import validate_no_missing_events, validate_entities_synced, validate_cleanup_complete
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils.mongo import get_collections_time_boundary
import bdp_utils.run
from bdp_utils.kafka import send

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from automatic_config.common import config
from automatic_config.common.results.committer import update_configurations
from automatic_config.common.results.store import Store
from automatic_config.fs_reduction import main as fs_main
from automatic_config.alphas_and_betas import main as weights_main
from automatic_config.common.utils.io import FileWriter


logger = logging.getLogger('step3')


class Manager:
    def __init__(self,
                 host,
                 validation_timeout,
                 validation_polling,
                 days_to_ignore,
                 skip_to):
        self._runner = bdp_utils.run.Runner(name='BdpAggregatedEventsToEntityEvents',
                                            logger=logger,
                                            host=host,
                                            block=False)
        self._cleaner = bdp_utils.run.Runner(name='BdpCleanupAggregatedEventsToEntityEvents',
                                             logger=logger,
                                             host=host,
                                             block=True)
        self._host = host
        self._validation_timeout = validation_timeout
        self._validation_polling = validation_polling
        self._days_to_ignore = days_to_ignore
        self._skip_to = skip_to

    def run(self):
        for step_name, step in [('run_bdp', self._run_bdp),
                                ('sync_entities', self._sync_entities),
                                ('run_automatic_config', self._run_automatic_config),
                                ('cleanup', self._cleanup),
                                ('restart_kafka', self._restart_kafka),
                                ('run_bdp_again', self._run_bdp)]:
            if self._skip_to is not None and self._skip_to != step_name:
                logger.info('skipping sub-step ' + step_name)
                continue
            self._skip_to = None
            logger.info('executing sub-step ' + step_name)
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
        logger.info('syncing entities...')
        send(logger=logger,
             host=self._host,
             topic='fortscale-entity-event-stream-control',
             message='{\\"type\\": \\"entity_event_sync\\"}')
        return validate_entities_synced(host=self._host,
                                        timeout=self._validation_timeout,
                                        polling=self._validation_polling)

    def _run_automatic_config(self):
        # extract entity_events.json to the overriding folder
        jar_filename = '/home/cloudera/fortscale/streaming/lib/fortscale-aggregation-1.1.0-SNAPSHOT.jar'
        logger.info('extracting entity_events.json from ' + jar_filename + '...')
        zf = zipfile.ZipFile(jar_filename, 'r')
        with FileWriter('/home/cloudera/fortscale/config/asl/entity_events/overriding/entity_events.json') as f:
            shutil.copyfileobj(zf.open('config/asl/entity_events.json', 'r'), f)
        zf.close()
        # calculate Fs reducers and alphas and betas
        start = get_collections_time_boundary(host=self._host,
                                              collection_names_regex='^aggr_',
                                              is_start=True)
        end = get_collections_time_boundary(host=self._host,
                                            collection_names_regex='^aggr_',
                                            is_start=False)
        logger.info('calculating Fs reducers (using start time ' + str(start) + ' and end time ' + str(end) + ')...')
        fs_main.load_data_and_run_algo(start=start, end=end)
        start += 60 * 60 * 24 * self._days_to_ignore
        logger.info('calculating alphas and betas (ignoring first ' + str(self._days_to_ignore) +
                    ' days - using start time ' + str(start) + ' and end time ' + str(end) + ')...')
        weights_main.load_data_and_run_algo(start=start, end=end)
        # commit everything
        logger.info('updating configuration files with Fs reducers and alphas and betas...')
        update_configurations()
        return not Store(config.interim_results_path + '/results.json').is_empty()

    def _cleanup(self):
        self._cleaner.run(overrides_key='step3.cleanup')
        return validate_cleanup_complete(host=self._host,
                                         timeout=self._validation_timeout,
                                         polling=self._validation_polling)

    def _restart_kafka(self):
        #TODO implement
        return True
