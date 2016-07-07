import sys
import os
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from automatic_config.common.utils import time_utils

really_big_epochtime = time_utils.get_epochtime('29990101')

step4 = [
    'single_step = EntityEventsCreation',
    'cleanup_step = Cleanup',
    'records_batch_size = 500000000'
]
step3 = [
    'single_step = AggregatedEventsToEntityEvents',
    'cleanup_step = Cleanup',
    'records_batch_size = 300000000',
    'secondsBetweenModelSyncs = ' + str(really_big_epochtime)
]
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
    'step3.scores': step3 + [
        'buildModelsFirst = false',
        'removeModelsFinally = false'
    ],
    'step3.build_models': step3 + [
        'buildModelsFirst = true',
        'removeModelsFinally = false'
    ],
    'step3.cleanup': [
        'cleanup_step = AggregatedEventsToEntityEvents',
        'records_batch_size = 500000',
    ],
    'step4': step4,
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
    ],
    'stepSAM.cleanup': [
        'cleanup_step = AfterEnriched'
    ],
    '2.6-step4.scores': step4 + [
        'secondsBetweenModelSyncs = ' + str(really_big_epochtime),
        'eventProcessingSyncTimeoutInSeconds = 3600',
        'removeModelsFinally = false'
    ],
    '2.6-step4.build_models': step4 + [
        'buildModelsFirst = true',
        'removeModelsFinally = false',
        'eventProcessingSyncTimeoutInSeconds = 3600'
    ]
}
