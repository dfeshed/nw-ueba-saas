import argparse
import logging
import os
import sys

from manager import Manager
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils import parsers
from bdp_utils.samza import are_tasks_running
from bdp_utils.log import init_logging

logger = logging.getLogger('2.7-step4')


def create_parser():
    return argparse.ArgumentParser(parents=[parsers.host],
                                   formatter_class=argparse.RawDescriptionHelpFormatter,
                                   prog='2.7/step4/run',
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
    This step will run BDP in order to #TODO: continue documentation
    At the end, the distribution of scores across the scored
    entity events will be displayed for further manual validation.

 Usage example:
     python 2.7/step4/run''')


def main():
    init_logging(logger)
    arguments = create_parser().parse_args()
    if not are_tasks_running(logger=logger,
                             task_names=['event-scoring-persistency-task', 'aggregated-feature-event-stats']):
        sys.exit(1)

    if Manager(host=arguments.host).run():
        logger.info('finished successfully')
    else:
        logger.error('failed')


if __name__ == '__main__':
    main()
