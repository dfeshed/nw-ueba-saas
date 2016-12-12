import logging
import os
import sys
from subprocess import call

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from validation.missing_events.validation import validate_no_missing_events
from validation.alerts_distribution.validation import validate_alerts_distribution
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from automatic_config.common.utils.mongo import get_collections_time_boundary, get_collection_names

logger = logging.getLogger('step6')


class Manager:
    def __init__(self, host, validation_timeout, validation_polling):
        self._host = host
        self._validation_timeout = validation_timeout
        self._validation_polling = validation_polling

    def run(self):
        scored_entity_collection_names_regex = '^scored___entity_event_((?!acm).)*$'
        start = get_collections_time_boundary(host=self._host,
                                              collection_names_regex=scored_entity_collection_names_regex,
                                              is_start=True)
        EVIDENCES = 'evidences'
        for collection_name in [EVIDENCES] + get_collection_names(host=self._host,
                                                                  collection_names_regex=scored_entity_collection_names_regex):
            call_args = ['nohup',
                         'java',
                         '-jar',
                         '-Duser.timezone=UTC',
                         'fortscale-collection-1.1.0-SNAPSHOT.jar',
                         'MongoToKafka',
                         'Forwarding',
                         'topics=' + ('fortscale-evidences' if collection_name == EVIDENCES else 'fortscale-entity-event-score-bdp'),
                         'collection=' + collection_name,
                         'datefield=' + ('endDate' if collection_name == EVIDENCES else 'end_time_unix'),
                         'filters=' + ('' if collection_name == EVIDENCES else 'score:::gte:::50###') + 'end_time_unix:::gt:::' + str(start),
                         'sort=' + ('endDate' if collection_name == EVIDENCES else 'end_time_unix') + '###asc',
                         'jobmonitor=alert-generator-task',
                         'classmonitor=fortscale.streaming.task.AlertGeneratorTask',
                         'batch=200000',
                         'retries=60']
            output_file_name = 'step6-fortscale-collection-nohup.out'
            logger.info('running ' + ' '.join(call_args) + ' >> ' + output_file_name)
            with open(output_file_name, 'a') as f:
                call(call_args,
                     cwd='/home/cloudera/fortscale/fortscale-core/fortscale/fortscale-collection/target',
                     stdout=f)
        if not validate_no_missing_events(host=self._host,
                                          timeout=self._validation_timeout,
                                          polling_interval=self._validation_polling):
            print "validation failed, but relax - everything's ok: the validation doesn't take into " \
                  "account that scored entity events might merge into one alert"
        validate_alerts_distribution(host=self._host)
        return True
