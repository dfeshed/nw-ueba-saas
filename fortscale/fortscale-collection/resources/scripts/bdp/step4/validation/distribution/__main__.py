import argparse
import os
import sys
from validation import validate_distribution
import logging

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils import parsers, colorer


def create_parser():
    parser = argparse.ArgumentParser(parents=[parsers.host])

    return parser


if __name__ == '__main__':
    colorer.colorize()
    logger = logging.getLogger('step4.validation')
    logging.basicConfig(format='%(message)s')
    logger.setLevel(logging.INFO)

    parser = create_parser()
    arguments = parser.parse_args()
    validate_distribution(host=arguments.host)
