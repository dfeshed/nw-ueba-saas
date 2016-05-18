import logging

logger = logging.getLogger('step4.validation')


def validate_models_synced(host):
    logger.info('validating that all models have been synced...')
    # TODO: implement
    is_valid = True
    if is_valid:
        logger.info('OK')
        return True
    else:
        logger.error('FAILED')
        return False
