import argparse
import logging
import os
import sys
from manager import Manager

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils import parsers
from bdp_utils.data_sources import data_source_to_enriched_tables
from bdp_utils.samza import are_tasks_running
from bdp_utils.log import init_logging

logger = logging.getLogger('stepSAM')


def create_parser():
    parser = argparse.ArgumentParser(parents=[parsers.host,
                                              parsers.start,
                                              parsers.online_manager,
                                              parsers.throttling],
                                     formatter_class=argparse.RawDescriptionHelpFormatter,
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
    This step supports two operation modes:
    1. Offline (which is the default): all of the data sources (specified by
       the --data_sources argument) will be processed (starting from the time
       specified by the --start argument) by running BDP on each data source
       (one by one) until there's no more data available in impala.
    2. Online (can be turned on by using the --online switch): the data sources
       will be processed by batches of one hour (starting from the time specified
       by --start) data source by data source.
       Once there's no more data available the script will wait until there's
       more data in all of the data sources.

Usage example:
    python stepSAM/run --start "8 may 1987" --data_sources ssh ntlm --wait_between_batches 0 --min_free_memory 16''')
    parser.add_argument('--data_sources',
                        nargs='+',
                        action='store',
                        dest='data_sources',
                        help='The data sources to run the step on',
                        choices=set(data_source_to_enriched_tables.keys()),
                        required=True)
    parser.add_argument('--wait_between_loads_seconds',
                        action='store',
                        dest='wait_between_loads_seconds',
                        help="querying models from mongo is throttled (so there's no performance overhead). "
                             "This throttling is specified by fortscale.model.wait.sec.between.loads "
                             "in bdp-overriding.properties. The python script automatically calculates this "
                             "parameter, but it can be manually specified here. Notice this parameter should be "
                             "specified in seconds",
                        choices=set(data_source_to_enriched_tables.keys()))
    return parser


def main():
    init_logging(logger)
    parser = create_parser()
    arguments = parser.parse_args()
    if arguments.is_online_mode:
        logger.error('online mode is not supported yet (yes - the manual has lied!)')
        sys.exit(1)
    if not are_tasks_running(logger=logger,
                             task_names=[]):
        sys.exit(1)

    Manager(host=arguments.host,
            is_online_mode=arguments.is_online_mode,
            start=arguments.start,
            data_sources=arguments.data_sources,
            wait_between_batches=arguments.wait_between_batches * 60,
            min_free_memory=arguments.min_free_memory * (1024 ** 3),
            polling_interval=arguments.polling_interval * 60,
            max_delay=arguments.max_delay * 60 * 60,
            wait_between_loads=arguments.wait_between_loads_seconds,
            max_batch_size=arguments.max_batch_size,
            force_max_batch_size_in_minutes=arguments.force_max_batch_size_in_minutes,
            max_gap=arguments.max_gap,
            convert_to_minutes_timeout=arguments.convert_to_minutes_timeout) \
        .run()


if __name__ == '__main__':
    main()
