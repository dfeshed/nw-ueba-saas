import argparse
import datetime
import sys
from dateutil.parser import parse

import mongo_stats
from validate import validate_no_missing_events


def create_parser():
    parser = argparse.ArgumentParser()
    parser.add_argument('--start_date',
                        action='store',
                        dest='start_date',
                        help='The start date (including) from which to make the validation, e.g. - "23 march 2016"',
                        required=True)
    parser.add_argument('--end_date',
                        action='store',
                        dest='end_date',
                        help='The end date (excluding) from which to make the validation, e.g. - "24 march 2016"',
                        required=True)
    parser.add_argument('--data_sources',
                        nargs='+',
                        action='store',
                        dest='data_sources',
                        help='The data sources to validate',
                        default=None)
    parser.add_argument('--context_types',
                        nargs='+',
                        action='store',
                        dest='context_types',
                        choices=mongo_stats.get_all_context_types(),
                        help='The types of aggregations to validate',
                        default=None)

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

    start_time_epoch = (parse(arguments.start_date) - datetime.datetime.utcfromtimestamp(0)).total_seconds()
    end_time_epoch = (parse(arguments.end_date) - datetime.datetime.utcfromtimestamp(0)).total_seconds()

    is_valid = validate_no_missing_events(start_time_epoch=start_time_epoch,
                                          end_time_epoch=end_time_epoch,
                                          data_sources=arguments.data_sources,
                                          context_types=arguments.context_types,
                                          stop_on_failure=False)
    sys.exit(0 if is_valid else 1)
