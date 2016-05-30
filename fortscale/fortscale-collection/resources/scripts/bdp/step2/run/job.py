import logging
import sys
import time
from subprocess import call
import os

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from validation.validation import validate_all_buckets_synced, validate_no_missing_events
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils.log import log_and_send_mail
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from automatic_config.common.utils import time_utils
from bdp_utils.data_sources import data_source_to_score_tables

logger = logging.getLogger('step2')


def run(start_time_epoch, batch_size_in_hours):
    call_args = ['nohup',
                 'java',
                 '-jar',
                 '-Duser.timezone=UTC',
                 'fortscale-collection-1.1.0-SNAPSHOT.jar',
                 'ScoringToAggregation',
                 'Forwarding',
                 'securityDataSources=' + ','.join(data_source_to_score_tables.iterkeys()),
                 'retries=60',
                 'batchSize=500000000',
                 'startTime=' + str(int(start_time_epoch * 1000)),
                 'hoursToRun=' + str(batch_size_in_hours)]
    output_file_name = 'step2-fortscale-collection-nohup.out'
    logger.info('running ' + ' '.join(call_args) + ' >> ' + output_file_name)
    with open(output_file_name, 'a') as f:
        call(call_args,
             cwd='/home/cloudera/fortscale/fortscale-core/fortscale/fortscale-collection/target',
             stdout=f)


def validate(host,
             start_time_epoch,
             end_time_epoch,
             wait_between_validations,
             max_delay,
             timeout,
             polling_interval):
    last_validation_time = time.time()
    is_valid = False
    while not is_valid:
        is_valid = _validate(host=host,
                             start_time_epoch=start_time_epoch,
                             end_time_epoch=end_time_epoch,
                             timeout=timeout,
                             polling_interval=polling_interval)
        if not is_valid:
            if time.time() - last_validation_time > max_delay:
                log_and_send_mail('validation failed for more than ' + str(int(max_delay / (60 * 60))) + ' hours')
            logger.info('not valid yet - going to sleep for ' +
                         str(int(wait_between_validations / 60)) + ' minutes')
            time.sleep(wait_between_validations)


def _validate(host, start_time_epoch, end_time_epoch, timeout, polling_interval):
    logger.info('validating ' + time_utils.interval_to_str(start_time_epoch, end_time_epoch) + '...')
    is_valid = validate_all_buckets_synced(host=host,
                                           start_time_epoch=start_time_epoch,
                                           end_time_epoch=end_time_epoch)
    if is_valid:
        validate_no_missing_events(host=host,
                                   start_time_epoch=start_time_epoch,
                                   end_time_epoch=end_time_epoch,
                                   data_sources=None,
                                   context_types=['normalized_username'],
                                   timeout=timeout,
                                   polling_interval=polling_interval)
    return is_valid
