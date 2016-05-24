import argparse
import os
import sys
from validation import validate_no_missing_events
import logging

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils import parsers, colorer
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from automatic_config.common.utils import time_utils


def create_parser():
    parser = argparse.ArgumentParser(parents=[parsers.host,
                                              parsers.validation_data_sources,
                                              parsers.validation_interval,
                                              parsers.validation_timeout,
                                              parsers.validation_polling_interval])
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
    colorer.colorize()
    logger = logging.getLogger('step2.validation')
    logging.basicConfig(format='%(message)s')
    logger.setLevel(logging.INFO)

    parser = create_parser()
    arguments = parser.parse_args()

    start_time_epoch = time_utils.get_epochtime(arguments.start)
    end_time_epoch = time_utils.get_epochtime(arguments.end)

    is_valid = validate_no_missing_events(host=arguments.host,
                                          start_time_epoch=start_time_epoch,
                                          end_time_epoch=end_time_epoch,
                                          data_sources=arguments.data_sources,
                                          context_types=arguments.context_types,
                                          timeout=arguments.timeout * 60,
                                          polling_interval=arguments.polling_interval * 60)
    sys.exit(0 if is_valid else 1)
