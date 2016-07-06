import argparse
import logging
import os
import sys

from manager import Manager
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils import parsers
from bdp_utils.samza import are_tasks_running
from bdp_utils.log import init_logging

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
    This step is composed of several sub steps:
    1. Run BDP without building models. This is done so we have all the
       "scored_aggr" collections filled up (so we'll have F values for
       building the models in sub step 2).
    2. Run BDP in order to build models.
    3. Move the models back in time (so they will be used by sub step 4),
       and run a BDP cleanup (without removing the models).
    4. Run BDP again (without building models). Now we'll have "good"
       scores.
    5. Calculate the best F reducers and alphas & betas in such a way
       that noisy data sources and Fs will be reduced.
    6. Run BDP cleanup again (because F reducers have been changed).
    7. Run BDP again (without building models). Now we'll have "the
       best" scores.

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
                        choices=Manager.SUB_STEPS)
    parser.add_argument('--run_until',
                        action='store',
                        dest='run_until',
                        help='this step consists of sequence of sub steps. If for some reason you want to run only '
                             'until a specific sub step (including) - just specify it here',
                        choices=Manager.SUB_STEPS)
    return parser


def main():
    arguments = create_parser().parse_args()
    init_logging(logger)
    if not are_tasks_running(logger=logger,
                             task_names=['event-scoring-persistency-task',
                                         'evidence-creation-task',
                                         'entity-events-streaming',
                                         'aggregated-feature-events-scoring-task',
                                         'model-building-streaming']):
        sys.exit(1)
    if Manager(host=arguments.host,
               validation_timeout=arguments.timeout * 60,
               validation_polling=arguments.polling_interval * 60,
               days_to_ignore=arguments.days_to_ignore,
               skip_to=arguments.skip_to,
               run_until=arguments.run_until) \
            .run():
        logger.info('finished successfully')
    else:
        logger.error('failed')


if __name__ == '__main__':
    main()
