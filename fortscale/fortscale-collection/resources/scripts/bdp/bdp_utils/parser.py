import argparse
import sys
import os
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from automatic_config.common.utils import time_utils


def _time_type(time):
    if time_utils.get_epoch(time) % (60 * 60) != 0:
        raise argparse.ArgumentTypeError("time can't be in the middle of an hour")
    return time_utils.get_datetime(time)


host_parent_parser = argparse.ArgumentParser(add_help=False)
host_parent_parser.add_argument('--host',
                                action='store',
                                dest='host',
                                help='The host to which to connect to. Default is localhost',
                                default='localhost')

step_parent_parser = argparse.ArgumentParser(add_help=False, parents=[host_parent_parser])
step_parent_parser.add_argument('--start',
                                action='store',
                                dest='start',
                                help='The date from which to start, '
                                     'e.g. - "23 march 2016 13:00" / "20160323" / "1458730800"',
                                required=True,
                                type=_time_type)
step_parent_parser.add_argument('--batch_size',
                                action='store',
                                dest='batch_size',
                                help='The batch size (in hours) to pass to the step. Default is 1',
                                type=int,
                                default='1')
