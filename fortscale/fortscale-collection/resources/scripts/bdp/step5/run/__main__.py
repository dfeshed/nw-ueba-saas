import argparse
import logging
import os
import sys

from manager import Manager

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils import parsers
from bdp_utils.run import step_runner_main
from bdp_utils.log import init_logging

logger = logging.getLogger('step5')
init_logging(logger)


def create_parser():
    parser = argparse.ArgumentParser(parents=[parsers.host],
                                     formatter_class=argparse.RawDescriptionHelpFormatter,
                                     prog='step5/run',
                                     description=
'''Notifications to indicators step
--------------------------------
Step prerequisites:
    Data should be provided in mongo collections whose names start
    with "scored___entity_event_".

Step results:
    The evidences mongo collection will contain evidences of type "Notification".

Inner workings:
    This step will run BDP. No validations are done.

 Usage example:
     python step5/run''')
    return parser


@step_runner_main(logger)
def main():
    arguments = create_parser().parse_args()
    if Manager(host=arguments.host).run():
        logger.info('finished successfully')
        return True
    else:
        logger.error('FAILED')
        return False
if __name__ == '__main__':
    main()
