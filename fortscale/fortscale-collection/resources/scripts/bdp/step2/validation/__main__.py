import argparse
import os
import sys
from validation import validate_no_missing_events

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from automatic_config.common.utils import time_utils
from bdp_utils.parser import validation_parent_parser


def create_parser():
    parser = argparse.ArgumentParser(parents=[validation_parent_parser])
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
    parser.add_argument('--context_types',
                        nargs='+',
                        action='store',
                        dest='context_types',
                        help="The mongo contexts to validate. "
                             "Usually normalized_username should be used, since other contexts might contain "
                             "less data than what's contained in impala, e.g. - due to failure in IP resolving. "
                             "Default is normalized_username"
                             "Default is normalized_username",
                        default=['normalized_username'])

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

    start_time_epoch = time_utils.get_epoch(arguments.start)
    end_time_epoch = time_utils.get_epoch(arguments.end)

    is_valid = validate_no_missing_events(host=arguments.host,
                                          start_time_epoch=start_time_epoch,
                                          end_time_epoch=end_time_epoch,
                                          data_sources=arguments.data_sources,
                                          context_types=arguments.context_types)
    sys.exit(0 if is_valid else 1)
