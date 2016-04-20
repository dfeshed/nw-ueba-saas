import argparse
import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from automatic_config.common.utils import time_utils

from validation import validate_no_missing_events


def create_parser():
    parser = argparse.ArgumentParser()
    parser.add_argument('--start',
                        action='store',
                        dest='start',
                        help='The start date (including) from which to make the validation, '
                             'e.g. - "23 march 2016 13:00" / "20160323" / "1458730800"',
                        required=True)
    parser.add_argument('--end',
                        action='store',
                        dest='end',
                        help='The end date (excluding) from which to make the validation, '
                             'e.g. - "24 march 2016 15:00" / "20160324" / "1458824400"',
                        required=True)
    parser.add_argument('--data_sources',
                        nargs='+',
                        action='store',
                        dest='data_sources',
                        help='The data sources to validate. '
                             'If not specified - all of the data sources will be validated',
                        default=None)
    parser.add_argument('--context_types',
                        nargs='+',
                        action='store',
                        dest='context_types',
                        help="The mongo contexts to validate. "
                             "Usually normalized_username should be used, since other contexts might contain "
                             "less data than what's contained in impala, e.g. - due to failure in IP resolving. "
                             "If not specified - all of the contexts will be validated. "
                             "Default is normalized_username",
                        default=['normalized_username'])
    parser.add_argument('--host',
                        action='store',
                        dest='host',
                        help='The host to which to connect to. Default is localhost',
                        default='localhost')

    return parser


if __name__ == '__main__':
    import logging
    import colorer
    colorer.colorize()
    logger = logging.getLogger('validation')
    logging.basicConfig(format='%(message)s')
    logger.setLevel(logging.INFO)

    parser = create_parser()
    arguments = parser.parse_args()

    start_time_epoch = time_utils.time_to_epoch(arguments.start)
    end_time_epoch = time_utils.time_to_epoch(arguments.end)

    is_valid = validate_no_missing_events(host=arguments.host,
                                          start_time_epoch=start_time_epoch,
                                          end_time_epoch=end_time_epoch,
                                          data_sources=arguments.data_sources,
                                          context_types=arguments.context_types)
    sys.exit(0 if is_valid else 1)
