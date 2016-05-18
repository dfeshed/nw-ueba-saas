from ..utils.mongo import validate_collections_are_empty


def validate_entities_synced(logger, host, validation_timeout, validation_polling):
    return validate_collections_are_empty(logger=logger,
                                          log_msg='validating entities synced...',
                                          host=host,
                                          validation_timeout=validation_timeout,
                                          validation_polling=validation_polling,
                                          collection_names_regex='^entity_event_meta_data')
