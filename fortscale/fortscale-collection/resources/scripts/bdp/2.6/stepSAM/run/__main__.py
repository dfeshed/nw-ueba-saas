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
    1. Offline: all of the data sources (specified by the --data_sources argument)
       will be processed (starting from the time specified by the --start argument)
       by running BDP on each data source (one by one) until there's no more data
       available in impala.
    2. Online: the data sources will be processed by batches of one hour (
       from the time specified by --start) data source by data source.
       Once there's no more data available the script will wait until there's
       more data in all of the data sources.

Usage example:
    python 2.6/stepSAM/run offline --start "8 may 1987" --data_sources ssh ntlm --convert_to_minutes_timeout -1 --max_batch_size 100000 --max_gap 500000''')
    more_args_parent = argparse.ArgumentParser(add_help=False)
    more_args_parent.add_argument('--wait_between_loads_seconds',
                                  action='store',
                                  dest='wait_between_loads_seconds',
                                  help="querying models from mongo is throttled (so there's no performance overhead). "
                                       "This throttling is specified by fortscale.model.wait.sec.between.loads "
                                       "in bdp-overriding.properties. The python script automatically calculates this "
                                       "parameter, but it can be manually specified here. Notice this parameter should be "
                                       "specified in seconds")
    subparsers = parser.add_subparsers(help='commands')
    common_parents = [more_args_parent,
                      parsers.host,
                      parsers.start,
                      parsers.data_sources,
                      parsers.throttling]
    online_parser = subparsers.add_parser('online',
                                          help='Run the step in online mode',
                                          parents=common_parents + [parsers.online_manager])
    online_parser.set_defaults(is_online_mode=True)
    offline_parser = subparsers.add_parser('offline',
                                           help='Run the step in offline mode',
                                           parents=common_parents + [parsers.validation_polling_interval])
    offline_parser.set_defaults(is_online_mode=False)
    return parser


def main():
    arguments = create_parser().parse_args()
    init_logging(logger)
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
            wait_between_batches=arguments.wait_between_batches * 60 if 'wait_between_batches' in arguments else 0,
            min_free_memory=arguments.min_free_memory * (1024 ** 3) if 'min_free_memory' in arguments else 0,
            polling_interval=arguments.polling_interval * 60,
            max_delay=arguments.max_delay * 60 * 60 if 'max_delay' in arguments else -1,
            wait_between_loads=arguments.wait_between_loads_seconds,
            max_batch_size=arguments.max_batch_size,
            force_max_batch_size_in_minutes=arguments.force_max_batch_size_in_minutes,
            max_gap=arguments.max_gap,
            convert_to_minutes_timeout=arguments.convert_to_minutes_timeout) \
        .run()


if __name__ == '__main__':
    main()
