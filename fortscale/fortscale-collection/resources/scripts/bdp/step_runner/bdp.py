import logging
import sys
import time
from subprocess import call

from data_sources import data_source_to_score_tables

sys.path.append(__file__ + r'\..\..')
from validation.validation import validate_all_buckets_synced, validate_no_missing_events
sys.path.append(__file__ + r'\..\..\..')
from automatic_config.common.utils import time_utils


def run_step(start_time_epoch, hours_to_run, retro_validation_gap, wait_between_validations):
    call(['echo',
          'nohup',
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
          'hoursToRun=' + str(hours_to_run)])
    is_valid = False
    while not is_valid:
        start_time_epoch = start_time_epoch - retro_validation_gap
        end_time_epoch = start_time_epoch + hours_to_run * 60 * 60
        logging.info('validating ' + time_utils.interval_to_str(start_time_epoch, end_time_epoch) + '...')
        is_valid = validate_all_buckets_synced(start_time_epoch=start_time_epoch,
                                               end_time_epoch=end_time_epoch)
        if not is_valid:
            logging.info('not valid yet - going to sleep for ' +
                         str(int(wait_between_validations / 60)) + ' minutes')
            time.sleep(wait_between_validations)
        else:
            validate_no_missing_events(start_time_epoch=start_time_epoch,
                                       end_time_epoch=end_time_epoch,
                                       data_sources=None,
                                       context_types=['normalized_username'],
                                       stop_on_failure=False)
