import argparse
import os
import sys
from validation import validate_no_missing_events

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils.parser import validation_parent_parser
from utils.data_sources import data_source_to_enriched_tables


def create_parser():
    parser = argparse.ArgumentParser(parents=[validation_parent_parser])
    parser.add_argument('--timeout',
                        action='store',
                        dest='timeout',
                        help="The timeout (in minutes) for waiting for the validation to finish. "
                             "If metrics aren't updated for the given time period the validation fails",
                        required=True,
                        type=int)
    parser.add_argument('--polling_interval',
                        action='store',
                        dest='polling_interval',
                        help='The time (in minutes) to wait between each validation try. Default is 3',
                        type=int,
                        default='3')

    return parser


if __name__ == '__main__':
    import logging
    from bdp_utils import colorer

    colorer.colorize()
    logger = logging.getLogger('validation')
    logging.basicConfig(format='%(message)s')
    logger.setLevel(logging.INFO)

    parser = create_parser()
    arguments = parser.parse_args()

    for data_source in arguments.data_sources or data_source_to_enriched_tables.iterkeys():
        if not validate_no_missing_events(host=arguments.host,
                                          data_source=data_source,
                                          timeout=arguments.timeout * 60,
                                          polling_interval=60 * arguments.polling_interval):
            sys.exit(1)
