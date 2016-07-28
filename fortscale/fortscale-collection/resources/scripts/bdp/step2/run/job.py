import logging
import os
import sys
from subprocess import call

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils.data_sources import data_source_to_score_tables
from bdp_utils.run import validate_bdp_flag

logger = logging.getLogger('step2')


def run(start_time_epoch, batch_size_in_hours, is_online_mode):
    validate_bdp_flag(is_online_mode=is_online_mode)
    call_args = ['nohup',
                 'java',
                 '-jar',
                 '-Duser.timezone=UTC',
                 'fortscale-collection-1.1.0-SNAPSHOT.jar',
                 'ScoringToAggregation',
                 'Forwarding',
                 'securityDataSources=' + ','.join(map(lambda data_source: data_source
                 if data_source != 'kerberos'
                 else 'kerberos_logins', data_source_to_score_tables.iterkeys())),
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
