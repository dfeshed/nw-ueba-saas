import argparse
import logging
import os
import sys

from manager import Manager
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils import parsers
from bdp_utils.samza import are_tasks_running

logger = logging.getLogger('step3')


def create_parser():
    parser = argparse.ArgumentParser(parents=[parsers.host,
                                              parsers.validation_timeout,
                                              parsers.validation_polling_interval])
    parser.add_argument('--days_to_ignore',
                        action='store',
                        dest='days_to_ignore',
                        help='number of days from the beginning to ignore when calculating alphas & betas. '
                             'It should be big enough so the noise is ignored, but not too big - so we have enough '
                             'data in order to build good alphas and betas. Default is 10',
                        type=int,
                        required=True)
    parser.add_argument('--skip_to',
                        action='store',
                        dest='skip_to',
                        help='this step consists of sequence of sub steps. If for some reason you want to start from '
                             'a different sub step than the first (e.g. - you killed this script in the middle of '
                             'some validations and you want to continue from where you left off) - just '
                             'specify it here',
                        choices=['run_bdp',
                                 'sync_entities',
                                 'run_automatic_config',
                                 'cleanup',
                                 'start_kafka',
                                 'run_bdp_again'])
    return parser


def main():
    logging.basicConfig(level=logging.INFO,
                        format='%(asctime)s %(levelname)s %(name)s: %(message)s',
                        datefmt="%d/%m/%Y %H:%M:%S")
    arguments = create_parser().parse_args()
    if not are_tasks_running(logger=logger,
                             task_names=['event-scoring-persistency-task',
                                         'evidence-creation',
                                         'entity-events-streaming',
                                         'aggregated-feature-event-stats']):
        sys.exit(1)
    if Manager(host=arguments.host,
               validation_timeout=arguments.timeout * 60,
               validation_polling=arguments.polling_interval * 60,
               days_to_ignore=arguments.days_to_ignore,
               skip_to=arguments.skip_to) \
            .run():
        logger.info('finished successfully')
    else:
        logger.error('failed')


if __name__ == '__main__':
    main()
