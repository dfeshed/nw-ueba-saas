import argparse
import os
import sys
from validation import validate_no_missing_events

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils import parsers
from utils.data_sources import data_source_to_enriched_tables


def create_parser():
    parser = argparse.ArgumentParser(parents=[parsers.host,
                                              parsers.validation_data_sources,
                                              parsers.validation_timeout,
                                              parsers.validation_polling_interval,
                                              parsers.validation_interval])

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
                                          polling_interval=60 * arguments.polling_interval,
                                          start=arguments.start,
                                          end=arguments.end):
            sys.exit(1)
    logger.info('validation succeeded!')
