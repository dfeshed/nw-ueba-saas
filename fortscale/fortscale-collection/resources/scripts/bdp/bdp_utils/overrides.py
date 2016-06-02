import os
import zipfile
from contextlib import contextmanager


overrides = {
    'common': [
        'validate_Fetch = false',
        'validate_ETL = false',
        'validate_Enrich = false',
        'validate_EnrichedDataToSingleEventIndicator = false',
        'validate_ScoredDataToBucketCreation = false',
        'validate_NotificationsToIndicators = false',
        'validate_AlertGeneration = false',
        'validate_Clean = true',
        'validate_ScoredEventsToIndicator = false',
        'validate_AggregatedEventsToEntityEvents = false',
        'validate_EntityEventsCreation = false',
        'bdp_flag_validation_enabled = false',
        'step_backup_enabled = false',
        'cleanup_before_step_enabled = false',
        'backup_model_and_scoring_hdfs_files = false'
    ],
    'step1': [
        'single_step = EnrichedDataToSingleEventIndicator',
        'cleanup_step = Cleanup',
        'throttlingSleep = 30'
    ],
    'step3.run': [
        'single_step = AggregatedEventsToEntityEvents',
        'cleanup_step = Cleanup',
        'records_batch_size = 300000000',
    ],
    'step3.cleanup': [
        'single_step = Cleanup',
        'cleanup_step = AggregatedEventsToEntityEvents',
        'records_batch_size = 500000',
    ],
    'step4': [
        'single_step = EntityEventsCreation',
        'cleanup_step = Cleanup',
        'records_batch_size = 500000000',
    ],
    'step5': [
        'single_step = NotificationsToIndicators',
        'cleanup_step = Cleanup',
        'data_sources = kerberos',
        'records_batch_size = 200000'
    ],
    'stepSAM': [
        'single_step = ScoreAggregateModelRawEvents',
        'cleanup_step = Cleanup',
        'removeModelsFinally = false'
    ]
}


@contextmanager
def open_overrides_file(overriding_path, jar_name, path_in_jar):
    if os.path.isfile(overriding_path):
        f = open(overriding_path, 'r')
        yield f
        f.close()
    else:
        zf = zipfile.ZipFile('/home/cloudera/fortscale/streaming/lib/' + jar_name, 'r')
        f = zf.open(path_in_jar, 'r')
        yield f
        f.close()
        zf.close()
