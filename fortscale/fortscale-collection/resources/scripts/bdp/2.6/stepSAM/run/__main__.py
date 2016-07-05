import argparse
import logging
import os
import sys
from manager import Manager

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils import parsers
from bdp_utils.samza import are_tasks_running
from bdp_utils.log import init_logging

logger = logging.getLogger('stepSAM')


def create_parser():
    parser = argparse.ArgumentParser(formatter_class=argparse.RawDescriptionHelpFormatter,
                                     parents=[parsers.host,
                                              parsers.start_optional,
                                              parsers.end_optional,
                                              parsers.data_sources,
                                              parsers.throttling,
                                              parsers.validation_polling_interval],
                                     prog='stepSAM/run',
                                     description=
'''Scoring, Aggregations and Modeling step
---------------------------------------
Step prerequisites:
    Data should be provided in impala enriched tables (the tables corresponding
    to the data sources passed to the script via the --data_sources argument).

Step results:
    Raw enriched events will be scored, and the results will appear in the
    impala scores tables. In addition, the raw events will be aggregated,
    and the results will appear in mongo collections (all those starting with
    "aggr_").

Inner workings:
    All of the data sources (specified by the --data_sources argument) will be
    processed, starting from the time specified by the --start argument up until
    the time specified by --end. If one of them is not specified - it'll be
    inferred from the data in the enriched tables.
    There are several sub steps:
    1. First a BDP process will run on all of the data sources in order to create
       aggregations and give scores. No models will be built.
    2. A second BDP process will create models for each data source based on all
       the data.
    3. A BDP process will clean everything but the models (because the scores
       are empty).
    4. Sub step 1 will run again in order to give the right scores based on the
       models.
    Note that if for some reason this script fails in the middle of some sub step
    and you want to run it again, you can just pass the --cleanup_first flag. The
    script will first clean everything but the models (if the previous script run
    succeeded in creating some data source's models), and then will skip sub steps
    1 and 2 for all the data sources which already have models (to save you time).

Usage example:
    python 2.6/stepSAM/run --data_sources ssh ntlm --convert_to_minutes_timeout -1 --max_batch_size 100000 --max_gap 500000''')
    parser.add_argument('--wait_between_loads_seconds',
                        action='store',
                        dest='wait_between_loads_seconds',
                        help="querying models from mongo is throttled (so there's no performance overhead). "
                             "This throttling is specified by fortscale.model.wait.sec.between.loads "
                             "in bdp-overriding.properties. The python script automatically calculates this "
                             "parameter, but it can be manually specified here. Notice this parameter should be "
                             "specified in seconds")
    parser.add_argument('--timeoutInSeconds',
                        action='store',
                        dest='timeoutInSeconds',
                        help='this parameter will be passed directly to BDP. '
                             'If not specified, the default specified by BDP will be used',
                        type=int)
    parser.add_argument('--cleanup_first',
                        action='store_const',
                        dest='cleanup_first',
                        const=True,
                        help='pass this flag if you want to run a bdp cleanup before '
                             'starting to process the data sources')
    return parser


def main():
    arguments = create_parser().parse_args()
    init_logging(logger)
    if not are_tasks_running(logger=logger,
                             task_names=[]):
        sys.exit(1)

    Manager(host=arguments.host,
            data_sources=arguments.data_sources,
            polling_interval=arguments.polling_interval * 60,
            max_batch_size=arguments.max_batch_size,
            force_max_batch_size_in_minutes=arguments.force_max_batch_size_in_minutes,
            max_gap=arguments.max_gap,
            force_max_gap_in_seconds=arguments.force_max_gap_in_seconds,
            convert_to_minutes_timeout=arguments.convert_to_minutes_timeout,
            timeoutInSeconds=arguments.timeoutInSeconds,
            cleanup_first=arguments.cleanup_first,
            start=arguments.start,
            end=arguments.end) \
        .run()


if __name__ == '__main__':
    main()
