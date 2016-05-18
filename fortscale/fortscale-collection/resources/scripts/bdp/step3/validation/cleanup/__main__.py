import argparse
import os
import sys
from validation import validate_cleanup_complete
import logging

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils import parsers, colorer


def create_parser():
    parser = argparse.ArgumentParser(parents=[parsers.host,
                                              parsers.validation_timeout,
                                              parsers.validation_polling_interval])

    return parser


if __name__ == '__main__':
    colorer.colorize()
    logger = logging.getLogger('step3.validation')
    logging.basicConfig(format='%(message)s')
    logger.setLevel(logging.INFO)

    parser = create_parser()
    arguments = parser.parse_args()

    if not validate_cleanup_complete(logger=logger,
                                     host=arguments.host,
                                     validation_timeout=arguments.timeout,
                                     validation_polling=arguments.polling_interval):
        sys.exit(1)
