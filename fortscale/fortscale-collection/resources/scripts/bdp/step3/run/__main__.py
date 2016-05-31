import argparse
import logging
import os
import sys

from manager import Manager
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils import parsers

logger = logging.getLogger('step3')


def create_parser():
    return argparse.ArgumentParser(parents=[parsers.host,
                                            parsers.validation_timeout])


def main():
    logging.basicConfig(level=logging.INFO,
                        format='%(asctime)s %(levelname)s %(name)s: %(message)s',
                        datefmt="%d/%m/%Y %H:%M:%S")
    arguments = create_parser().parse_args()
    Manager(host=arguments.host, validation_timeout=arguments.timeout).run()


if __name__ == '__main__':
    main()
