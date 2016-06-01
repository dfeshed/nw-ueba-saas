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
                                              parsers.validation_polling_interval],
                                     formatter_class=argparse.RawDescriptionHelpFormatter,
                                     prog='step3/run',
                                     description=
'''Aggregations to entity events step
----------------------------------
Step prerequisites:
    Data should be provided in mongo collections whose names start
    with "aggr_".

Step results:
    Entity events will be created out of the aggregations, and will be
    placed in mongo collections with names starting with "entity_event_".
    Additionally, each aggregation scorer (the Fs) will be reduced by
    a learnt low-values-score-reducer (the resulting reducers can be found
    in aggregated-feature_event-prevalance-stats.properties), and the
    SMART alphas and betas (which are used in step 4) will be calculated
    (which will be found in entity_events.json).

Inner workings:
    This step will first run the BDP in order to create the entity events.
    This includes giving scores to aggregations (Fs). But there are some
    aggregations that might be too noisy - they give high scores where
    they shouldn't. This problem is solved by configuring
    low-values-score-reducer. But it's hard to configure it before actually
    calculating the scores. This is why we can calculate the best
    configurations only after we give the scores.

    Next it calculates alphas and betas in such a way that the resulting
    alerts will have a good balance of different features and data sources.
    Because the first few days have bad scores (because there's not enough
    data to create good models), these days are irrelevant in the process
    of calculating the alphas and betas - so they should be ignored using
    the --days_to_ignore argument.

    Now that we have good configurations, all the aggregation scores are
    obsolete (because they weren't reduced by the new configuration) - so
    a cleanup is made.

    Now we run the BDP step again in order to create the good scores (and
    entity events).

    In this step we do various validations in order to make sure all
    events are processed.

 Usage example:
     python step3/run --timeout 5 --days_to_ignore 10''')
    parser.add_argument('--days_to_ignore',
                        action='store',
                        dest='days_to_ignore',
                        help='number of days from the beginning to ignore when calculating alphas & betas. '
                             'It should be big enough so the noise is ignored, but not too big - so we have enough '
                             'data in order to build good alphas and betas. If there is a big volume of data, '
                             '10 should do',
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
