import argparse
import logging
import os
import sys

from manager import Manager
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils import parsers
from bdp_utils.samza import are_tasks_running
from bdp_utils.log import init_logging

logger = logging.getLogger('2.6-step4')


def create_parser():
    parser = argparse.ArgumentParser(parents=[parsers.host],
                                     formatter_class=argparse.RawDescriptionHelpFormatter,
                                     prog='2.6/step4/run',
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
    This step is composed of several sub-steps:
    1. Run BDP without building models (so we have scored entity events
       collections filled up).
    2. Run BDP so the models will be built (but without streaming any
       events in the system).
    3. Keep the models which know how to give a score for an entity event
       value, and clean the scored entity events collections.
    4. Run BDP again without building models.
    5. Run BDP again in order to build models.
    6. This time we also keep the alert control models, and clean the
       scored entity events again.
    7. Run BDP without building models. Because now we already have all
       the models we need - at the end of this step the scored entity
       events collections will contain the result scores.
    8. At the end, the distribution of scores across the scored entity
       events will be displayed for further manual validation.

 Usage example:
     python 2.6/step4/run''')
    parser.add_argument('--days_to_ignore',
                        action='store',
                        dest='days_to_ignore',
                        help='number of days from the beginning to ignore when building the models. '
                             'It should be big enough so the noise is ignored, but not too big - so we have enough '
                             'data in order to build good models. If there is a big volume of data, 10 should do',
                        type=int,
                        required=True)
    return parser


def main():
    init_logging(logger)
    arguments = create_parser().parse_args()
    if not are_tasks_running(logger=logger,
                             task_names=['event-scoring-persistency-task', 'aggregated-feature-event-stats']):
        sys.exit(1)

    if Manager(host=arguments.host,
               days_to_ignore=arguments.days_to_ignore).run():
        logger.info('finished successfully')
    else:
        logger.error('failed')


if __name__ == '__main__':
    main()
