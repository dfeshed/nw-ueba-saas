import re

from ..utils.mongo import validate_collections_are_empty


def validate_cleanup_complete(host, validation_timeout, validation_polling):
    return validate_collections_are_empty(log_msg='validating cleanup 1/2...',
                                          host=host,
                                          validation_timeout=validation_timeout,
                                          validation_polling=validation_polling,
                                          collection_names_regex='(^scored___aggr_event__|^entity_event_.*_(daily|hourly))') and \
           validate_collections_are_empty(log_msg='validating cleanup 2/2...',
                                          host=host,
                                          find_query={'modelName': re.compile('^aggr', re.IGNORECASE)},
                                          validation_timeout=validation_timeout,
                                          validation_polling=validation_polling,
                                          collection_names_regex='^streaming_models$')
