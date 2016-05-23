import argparse
import sys
import os

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from automatic_config.common.utils import time_utils


def _time_type(time):
    if time_utils.get_epochtime(time) % (60 * 60) != 0:
        raise argparse.ArgumentTypeError("time can't be in the middle of an hour")
    return time_utils.get_datetime(time)


host = argparse.ArgumentParser(add_help=False)
host.add_argument('--host',
                  action='store',
                  dest='host',
                  help='The host to which to connect to. Default is localhost',
                  default='localhost')

start_args = {
    'action': 'store',
    'dest': 'start',
    'help': 'The date from which to start (including), '
            'e.g. - "23 march 2016 13:00" / "20160323" / "1458730800"',
    'type': _time_type
}
start = argparse.ArgumentParser(add_help=False)
start.add_argument('--start',
                   required=True,
                   **start_args)

start_optional = argparse.ArgumentParser(add_help=False)
start_optional.add_argument('--start',
                            **start_args)

end = argparse.ArgumentParser(add_help=False)
end.add_argument('--end',
                 action='store',
                 dest='end',
                 help='The date until to run (excluding), '
                      'e.g. - "24 march 2016 13:00" / "20160324" / "1458824400"',
                 required=True,
                 type=_time_type)

validation_data_sources = argparse.ArgumentParser(add_help=False)
validation_data_sources.add_argument('--data_sources',
                                     nargs='+',
                                     action='store',
                                     dest='data_sources',
                                     help='The data sources to validate. '
                                          'If not specified - all of the data sources will be validated',
                                     default=None)

validation_timeout = argparse.ArgumentParser(add_help=False)
validation_timeout.add_argument('--timeout',
                                action='store',
                                dest='timeout',
                                help="The timeout (in minutes) for waiting for the validation to "
                                     "finish. If metrics aren't updated for the given time period "
                                     "the validation fails",
                                required=True,
                                type=int)

validation_polling_interval = argparse.ArgumentParser(add_help=False)
validation_polling_interval.add_argument('--polling_interval',
                                         action='store',
                                         dest='polling_interval',
                                         help='The time (in minutes) to wait between each '
                                              'validation try. Default is 3',
                                         type=int,
                                         default='3')

validation_interval = argparse.ArgumentParser(add_help=False)
validation_interval.add_argument('--start',
                                 action='store',
                                 dest='start',
                                 help='The start date (including) from which to make the validation, '
                                      'e.g. - "23 march 2016 13:00" / "20160323" / "1458730800"',
                                 required=True)
validation_interval.add_argument('--end',
                                 action='store',
                                 dest='end',
                                 help='The end date (excluding) from which to make the validation, '
                                      'e.g. - "24 march 2016 15:00" / "20160324" / "1458824400"',
                                 required=True)
