import argparse
import logging
import os
import sys
import time
from manager import Manager

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from utils.data_sources import data_source_to_enriched_tables
from utils.samza import are_tasks_running
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils import parsers

logger = logging.getLogger('step1')


def create_parser():
    parser = argparse.ArgumentParser(parents=[parsers.host,
                                              parsers.start,
                                              parsers.end,
                                              parsers.validation_timeout,
                                              parsers.validation_polling_interval])
    parser.add_argument('--data_sources',
                        nargs='+',
                        action='store',
                        dest='data_sources',
                        help='The data sources to run the step on',
                        choices=set(data_source_to_enriched_tables.keys()).difference(['vpn_session']),
                        required=True)
    parser.add_argument('--max_batch_size',
                        action='store',
                        dest='max_batch_size',
                        help="The maximal batch size (number of events) to read from impala. "
                             "This parameter is translated into BDP's forwardingBatchSizeInMinutes parameter",
                        required=True,
                        type=int)
    parser.add_argument('--force_max_batch_size_in_minutes',
                        action='store',
                        dest='force_max_batch_size_in_minutes',
                        help="The maximal batch size (in minutes) to read from impala. "
                             "This parameter overrides --max_batch_size. Use it only if you know what you're doing, "
                             "or if running the script without it results with too small batch size in minutes "
                             "(in this case a warning will be displayed)",
                        default=None,
                        type=int)
    parser.add_argument('--max_gap',
                        action='store',
                        dest='max_gap',
                        help="The maximal gap size (number of events) which is allowed before stopping and waiting. "
                             "This parameter is translated into BDP's maxSourceDestinationTimeGap parameter",
                        required=True,
                        type=int)

    return parser


def main():
    logging.basicConfig(level=logging.INFO,
                        format='%(asctime)s %(levelname)s %(name)s: %(message)s',
                        datefmt="%d/%m/%Y %H:%M:%S")
    arguments = create_parser().parse_args()
    if arguments.force_max_batch_size_in_minutes is None and arguments.max_gap < arguments.max_batch_size:
        print 'max_gap must be greater or equal to max_batch_size'
        sys.exit(1)
    if not are_tasks_running(task_names=['raw_events_stats', 'hdfs_writer', 'evidence_creation', 'event_filter_4769',
                                         'vpnsession_event_filter', 'vpn_event_filter', 'service_account_tagging'],
                             logger=logger):
        sys.exit(1)
    managers = [Manager(host=arguments.host,
                        # start=arguments.start, TODO: use it
                        data_source=data_source,
                        max_batch_size=arguments.max_batch_size,
                        force_max_batch_size_in_minutes=arguments.force_max_batch_size_in_minutes,
                        max_gap=arguments.max_gap,
                        validation_timeout=arguments.timeout,
                        validation_polling_interval=arguments.polling_interval,
                        start=arguments.start,
                        end=arguments.end)
                for data_source in arguments.data_sources]
    for manager in managers:
        max_batch_size_in_minutes = manager.get_max_batch_size_in_minutes()
        if max_batch_size_in_minutes < 15 and arguments.force_max_batch_size_in_minutes is None:
            print 'max_batch_size is relatively small. It translates to forwardingBatchSizeInMinutes=' + \
                  str(max_batch_size_in_minutes) + \
                  '. If you wish to proceed, run the script with "--force_max_batch_size_in_minutes ' + \
                  str(max_batch_size_in_minutes) + '"'
            sys.exit(1)
        logger.info('using batch size of ' + str(max_batch_size_in_minutes) + ' minutes')
        max_gap_in_minutes = manager.get_max_gap_in_minutes()
        if arguments.force_max_batch_size_in_minutes is not None and \
                        max_gap_in_minutes < arguments.force_max_batch_size_in_minutes:
            print 'max_gap is too small. It translated to maxSourceDestinationTimeGap=' + str(max_gap_in_minutes) + \
                  ' which is smaller than what was provided by --force_max_batch_size_in_minutes'
            sys.exit(1)
        logger.info('using gap size of ' + str(max_gap_in_minutes) + ' minutes')
        manager.run()
    if not validate(managers):
        sys.exit(1)


def validate(managers):
    logger.info('starting validations...')
    res = True
    for manager in managers:
        is_valid = manager.validate()
        while is_valid is None:
            minutes_to_sleep = 1
            logger.info('validation not done yet - going to sleep for ' + str(minutes_to_sleep) + ' minute' +
                        ('s' if minutes_to_sleep > 1 else '') + '...')
            time.sleep(minutes_to_sleep * 60)
            is_valid = manager.validate()
        res &= is_valid
    (logger.info if is_valid else logger.error)('validation ' + ('succeeded' if is_valid else 'failed'))
    return res


if __name__ == '__main__':
    main()
