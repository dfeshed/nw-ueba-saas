from subprocess import call

from data_sources import data_source_to_score_tables


def run_step(start_time_epoch, hours_to_run):
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
