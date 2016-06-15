import argparse
import logging
import os
import sys

from manager import Manager
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils import parsers
from bdp_utils.log import init_logging

logger = logging.getLogger('step6')


def create_parser():
    return argparse.ArgumentParser(parents=[parsers.host,
                                            parsers.validation_timeout,
                                            parsers.validation_polling_interval],
                                   formatter_class=argparse.RawDescriptionHelpFormatter,
                                   prog='step6/run',
                                   description=
'''Alerts creation step
--------------------
Step prerequisites:
    Data should be provided in mongo collections whose names start
    with "scored___entity_event_", and evidences collection should
    contain relevant documents.

Step results:
    The alerts mongo collection will contain alerts.

Inner workings:
    This step will run fortscale-collection-1.1.0-SNAPSHOT.jar directly
    without using BDP).
    Next it'll validate that all SMART events with score bigger than 50
    will result in an alert. Additionally, the distribution of alerts
    scores will be displayed for further manual validation.

 Usage example:
     python step6/run --timeout 5''')


def main():
    arguments = create_parser().parse_args()
    init_logging(logger)
    if Manager(host=arguments.host,
               validation_timeout=arguments.timeout * 60,
               validation_polling=arguments.polling_interval * 60) \
            .run():
        logger.info('finished successfully')
    else:
        logger.error('failed')


if __name__ == '__main__':
    main()
