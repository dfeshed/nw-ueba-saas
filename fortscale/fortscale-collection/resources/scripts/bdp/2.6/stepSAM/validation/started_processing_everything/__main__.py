import argparse
import os
import sys
from validation import validate_started_processing_everything
import logging

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..']))
from bdp_utils import parsers, colorer


def create_parser():
    parser = argparse.ArgumentParser(parents=[parsers.host,
                                              parsers.validation_data_sources])
    parser.add_argument('--data_source',
                        action='store',
                        dest='data_source',
                        help='The data source to validate',
                        required=True)
    return parser


if __name__ == '__main__':
    colorer.colorize()
    logger = logging.getLogger('stepSAM.validation')
    logging.basicConfig(format='%(message)s')
    logger.setLevel(logging.INFO)

    parser = create_parser()
    arguments = parser.parse_args()
    if not validate_started_processing_everything(host=arguments.host,
                                                  data_source=arguments.data_source):
        sys.exit(1)
