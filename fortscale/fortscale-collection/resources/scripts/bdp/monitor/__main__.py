import argparse
import logging
import os
import sys

from validation import validate_progress

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from bdp_utils import parsers, colorer, log


def create_parser():
    parser = argparse.ArgumentParser(parents=[parsers.host])
    parser.add_argument('--collection_name',
                        action='store',
                        dest='collection_name',
                        help='the name of the collection to monitor.'
                             'Default is scored___entity_event__normalized_username_hourly',
                        default='scored___entity_event__normalized_username_hourly')
    parser.add_argument('--polling_interval',
                        action='store',
                        dest='polling_interval',
                        help='the interval (in minutes) between polling the collection. Default is 15',
                        type=int,
                        default='15')
    parser.add_argument('--max_delay',
                        action='store',
                        dest='max_delay',
                        help='the maximal delay (in minutes) of no progress before an error mail is sent',
                        type=int,
                        required=True)

    return parser


if __name__ == '__main__':
    colorer.colorize()
    logger = logging.getLogger('monitoring')
    logging.basicConfig(format='%(message)s')
    logger.setLevel(logging.INFO)

    parser = create_parser()
    arguments = parser.parse_args()
    try:
        validate_progress(host=arguments.host,
                          collection_name=arguments.collection_name,
                          polling_interval=arguments.polling_interval,
                          max_delay=arguments.max_delay)
    except Exception, e:
        log.log_and_send_mail.info('monitoring script has failed and exited for the following reason: %s' % e)
