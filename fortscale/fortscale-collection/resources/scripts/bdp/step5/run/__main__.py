import argparse
import logging
import os
import sys

from manager import Manager
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils import parsers

logger = logging.getLogger('step5')


def create_parser():
    parser = argparse.ArgumentParser(parents=[parsers.host])
    return parser


def main():
    logging.basicConfig(level=logging.INFO,
                        format='%(asctime)s %(levelname)s %(name)s: %(message)s',
                        datefmt="%d/%m/%Y %H:%M:%S")
    arguments = create_parser().parse_args()
    Manager(host=arguments.host).run()
    logger.info('finished successfully')


if __name__ == '__main__':
    main()
