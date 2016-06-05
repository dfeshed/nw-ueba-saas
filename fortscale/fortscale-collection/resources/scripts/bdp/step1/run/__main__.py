import argparse
import logging
import os
import sys
import time
from manager import Manager

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils import parsers
from bdp_utils.samza import are_tasks_running
from bdp_utils.data_sources import data_source_to_enriched_tables

logger = logging.getLogger('step1')


def create_parser():
    parser = argparse.ArgumentParser(parents=[parsers.host,
                                              parsers.start,
                                              parsers.end,
                                              parsers.validation_timeout,
                                              parsers.validation_polling_interval],
                                     formatter_class=argparse.RawDescriptionHelpFormatter,
                                     prog='step1/run',
                                     description=
'''Enriched to scoring step
------------------------
Step prerequisites:
    Data should be provided in impala enriched tables (the tables corresponding
    to the data sources passed to the script via the --data_sources argument).

Step results:
    Raw enriched events will be scored, and the results will appear in the
    impala scores tables.

Inner workings:
    All of the data sources will be processed one by one (once all of the
    first data source's events have been sent to Samza the second one will
    start).
    Once all have been sent to Samza validations will start: every data
    source will be validated independently of the others. Validations
    include making sure all events have been processed, and that the
    scores distribution is reasonable.

Usage example:
    python step1/run --timeout 5 --start 19870508 --end 20160628 --data_sources kerberos_logins ssh --max_batch_size 500000 --max_gap 1500000 --convert_to_minutes_timeout 10''')
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
    parser.add_argument('--convert_to_minutes_timeout',
                        action='store',
                        dest='convert_to_minutes_timeout',
                        help="When calculating duration in minutes out of max batch size and max gap daily queries "
                             "are performed against impala. The more days we query - the better the duration estimate "
                             "is. If you want this process to take only a limited amount of time, impala queries will "
                             "stop by the end of the specified timeout (in minutes), and the calculation will begin. "
                             "If not specified, no timeout will occur",
                        type=int,
                        required=True)
    parser.add_argument('--scores_anomalies_path',
                        action='store',
                        dest='scores_anomalies_path',
                        help='At the end of the step the scores will be analyzed in order to detect anomalies. '
                             'The data used for anomalies detection will be stored in this path. '
                             'Default is /home/cloudera/bdp_step1_scores_anomalies',
                        default='/home/cloudera/bdp_step1_scores_anomalies')
    parser.add_argument('--scores_anomalies_warming_period',
                        action='store',
                        dest='scores_anomalies_warming_period',
                        help='At the end of the step the scores will be analyzed in order to detect anomalies. '
                             'The number of days to warm up before starting to look for scores anomalies can be '
                             'specified here. Default is 7',
                        type=int,
                        default=7)
    parser.add_argument('--scores_anomalies_threshold',
                        action='store',
                        dest='scores_anomalies_threshold',
                        help='At the end of the step the scores will be analyzed in order to detect anomalies. '
                             'The threshold used when comparing two histograms in order to find if one is '
                             'anomalous can be specified here. Default is 0.025',
                        type=float,
                        default=0.025)
    return parser


def main():
    logging.basicConfig(level=logging.INFO,
                        format='%(asctime)s %(levelname)s %(name)s: %(message)s',
                        datefmt="%d/%m/%Y %H:%M:%S")
    arguments = create_parser().parse_args()
    if arguments.force_max_batch_size_in_minutes is None and arguments.max_gap < arguments.max_batch_size:
        print 'max_gap must be greater or equal to max_batch_size'
        sys.exit(1)
    if not are_tasks_running(logger=logger,
                             task_names=['raw-events-prevalence-stats-task', 'hdfs-events-writer-task',
                                         'evidence-creation-task', '4769-events-filter', 'vpnsession-events-filter',
                                         'vpn-events-filter', 'service-account-tagging']):
        sys.exit(1)
    managers = [Manager(host=arguments.host,
                        data_source=data_source,
                        max_batch_size=arguments.max_batch_size,
                        force_max_batch_size_in_minutes=arguments.force_max_batch_size_in_minutes,
                        max_gap=arguments.max_gap,
                        convert_to_minutes_timeout=arguments.convert_to_minutes_timeout * 60,
                        validation_timeout=arguments.timeout * 60,
                        validation_polling_interval=arguments.polling_interval * 60,
                        start=arguments.start,
                        end=arguments.end,
                        scores_anomalies_path=arguments.scores_anomalies_path,
                        scores_anomalies_warming_period=arguments.scores_anomalies_warming_period,
                        scores_anomalies_threshold=arguments.scores_anomalies_threshold)
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
