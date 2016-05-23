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
        'start_with_step = EnrichedDataToSingleEventIndicator',
        'end_with_step = EnrichedDataToSingleEventIndicator',
        'cleanup_step = Cleanup',
        'throttlingSleep = 30'
    ],
    'step3.run': [
        'start_with_step = AggregatedEventsToEntityEvents',
        'end_with_step = AggregatedEventsToEntityEvents',
        'cleanup_step = Cleanup',
        'records_batch_size = 300000000',
    ],
    'step3.cleanup': [
        'start_with_step = Cleanup',
        'end_with_step = Cleanup',
        'cleanup_step = AggregatedEventsToEntityEvents',
        'records_batch_size = 500000',
    ],
    'step4': [
        'start_with_step = EntityEventsCreation',
        'end_with_step = EntityEventsCreation',
        'cleanup_step = Cleanup',
        'records_batch_size = 500000000',
    ]
}
