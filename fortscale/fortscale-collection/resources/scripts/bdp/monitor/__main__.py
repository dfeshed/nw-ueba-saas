import argparse
import logging
import os
import sys

from validation import validate_progress

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from bdp_utils import parsers, colorer, log


def create_parser():
    parser = argparse.ArgumentParser(parents=[parsers.host],
                                     formatter_class=argparse.RawDescriptionHelpFormatter,
                                     prog='monitor',
                                     description=
'''monitor
-------
Monitor the progress of the Fortscale product. This is done by making
sure there's no essential gap between the last scored entity event
available in mongo and system time. The maximal allowed gap is controlled
by the --max_delay argument (in minutes). If the gap is crossed, an error
mail will be sent.
''')
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
                        help='the maximal gap (in minutes) allowed between system time and the last scored entity',
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
        log.log_and_send_mail('monitoring script has failed and exited for the following reason: %s' % e)
