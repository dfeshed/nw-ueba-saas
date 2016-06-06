import argparse
import logging
import os
import sys

from manager import Manager
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils import parsers
from bdp_utils.samza import are_tasks_running

logger = logging.getLogger('step4')


def create_parser():
    return argparse.ArgumentParser(parents=[parsers.host,
                                            parsers.validation_timeout,
                                            parsers.validation_polling_interval],
                                   formatter_class=argparse.RawDescriptionHelpFormatter,
                                   prog='step4/run',
                                   description=
'''Scored entity events creation step
----------------------------------
Step prerequisites:
    Data should be provided in mongo collections whose names start
    with "entity_event_".

Step results:
    Scored entity events will be created out of the entity events, and
    will be placed in mongo collections with names starting with
    "scored___entity_event_".

Inner workings:
    This step will run BDP and then will validate that all events have been
    validated. Additionally, the distribution of scores across the scored
    entity events will be displayed for further manual validation.

 Usage example:
     python step4/run --timeout 5''')


def main():
    logging.basicConfig(level=logging.INFO,
                        format='%(asctime)s %(levelname)s %(name)s: %(message)s',
                        datefmt="%d/%m/%Y %H:%M:%S")
    arguments = create_parser().parse_args()
    if not are_tasks_running(logger=logger,
                             task_names=['event-scoring-persistency-task', 'aggregated-feature-event-stats']):
        sys.exit(1)

    if Manager(host=arguments.host,
               validation_timeout=arguments.timeout * 60,
               validation_polling=arguments.polling_interval * 60) \
            .run():
        logger.info('finished successfully')
    else:
        logger.error('failed')


if __name__ == '__main__':
    main()
