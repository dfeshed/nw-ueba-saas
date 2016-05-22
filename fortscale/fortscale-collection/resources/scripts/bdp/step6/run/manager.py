import logging
import sys
import os
from subprocess import call

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from validation.missing_events.validation import validate_no_missing_events
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils.mongo import get_collections_time_boundary, get_collection_names
from bdp_utils.run import validate_by_polling

logger = logging.getLogger('step6')


class Manager:
    def __init__(self, host, validation_timeout, validation_polling):
        self._host = host
        self._validation_timeout = validation_timeout
        self._validation_polling = validation_polling

    def run(self):
        scored_entity_collection_names_regex = '^scored___entity_event_'
        start = get_collections_time_boundary(host=self._host,
                                              collection_names_regex=scored_entity_collection_names_regex,
                                              is_start=True)
        for collection_name in get_collection_names(host=self._host,
                                                    collection_names_regex=scored_entity_collection_names_regex):
            call_args = ['nohup',
                         'java',
                         '-jar',
                         '-Duser.timezone=UTC',
                         'fortscale-collection-1.1.0-SNAPSHOT.jar',
                         'MongoToKafka',
                         'Forwarding',
                         'topics=fortscale-entity-event-score-bdp',
                         'collection=' + collection_name,
                         'datefield=end_time_unix',
                         'filters=score:::gte:::50###end_time_unix:::gt:::' + str(start),
                         'sort=end_time_unix###asc',
                         'jobmonitor=alert-generator-task',
                         'classmonitor=fortscale.streaming.task.AlertGeneratorTask',
                         'batch=200000',
                         'retries=60']
            output_file_name = 'step6-fortscale-collection-nohup.out'
            logger.info('running ' + ' '.join(call_args) + ' > ' + output_file_name)
            with open(output_file_name, 'w') as f:
                call(call_args,
                     cwd='/home/cloudera/fortscale/fortscale-core/fortscale/fortscale-collection/target',
                     stdout=f)
        validate_by_polling(status_cb=lambda: validate_no_missing_events(host=self._host, start=start),
                            status_target=True,
                            no_progress_timeout=self._validation_timeout,
                            polling=self._validation_polling)
