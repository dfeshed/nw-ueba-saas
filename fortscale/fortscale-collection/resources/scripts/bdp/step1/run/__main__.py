import argparse
import logging
import os
import sys
import time
from manager import Manager

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils import parsers
from bdp_utils.samza import are_tasks_running
from bdp_utils.log import init_logging

logger = logging.getLogger('step1')


def create_parser():
    parser = argparse.ArgumentParser(parents=[parsers.host,
                                              parsers.start,
                                              parsers.end,
                                              parsers.data_sources_excluding_vpn_session_mandatory,
                                              parsers.validation_timeout,
                                              parsers.validation_polling_interval,
                                              parsers.throttling],
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
    python step1/run --timeout 5 --start 19870508 --end 20160628 --data_sources kerberos ssh --max_batch_size 500000 --max_gap 1500000 --convert_to_minutes_timeout 10''')
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
    arguments = create_parser().parse_args()
    init_logging(logger)
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
