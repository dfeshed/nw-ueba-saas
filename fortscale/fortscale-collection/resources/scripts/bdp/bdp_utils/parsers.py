import argparse
import sys
import os

from data_sources import data_source_to_enriched_tables
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
            'e.g. - "8 may 1987 13:00" / "19870508" / "547477200"',
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
                                          'If not specified - all of the data sources will be validated '
                                          '(which include ' + ', '.join(data_source_to_enriched_tables.keys()) +
                                          '. To change that, please update data_sources.py)',
                                     default=data_source_to_enriched_tables.keys())

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
                                         default=3)

validation_interval = argparse.ArgumentParser(add_help=False)
validation_interval.add_argument('--start',
                                 action='store',
                                 dest='start',
                                 help='The start date (including) from which to make the validation, '
                                      'e.g. - "8 may 1987 13:00" / "19870508" / "547477200"',
                                 required=True)
validation_interval.add_argument('--end',
                                 action='store',
                                 dest='end',
                                 help='The end date (excluding) from which to make the validation, '
                                      'e.g. - "24 march 2016 15:00" / "20160324" / "1458824400"',
                                 required=True)


online_manager = argparse.ArgumentParser(add_help=False)
online_manager.add_argument('--online',
                            action='store_const',
                            dest='is_online_mode',
                            const=True,
                            help='pass this flag if running this step should never end: '
                                 'whenever there is no more data, just wait until more data arrives', )
online_manager.add_argument('--wait_between_batches',
                            action='store',
                            dest='wait_between_batches',
                            help='The minimum amount of time (in minutes) between successive batch runs',
                            type=int,
                            required=True)
online_manager.add_argument('--min_free_memory',
                            action='store',
                            dest='min_free_memory',
                            help='Whenever the amount of free memory in the system is below the given number (in GB), '
                                 'the script will block',
                            type=int,
                            required=True)
online_manager.add_argument('--polling_interval',
                            action='store',
                            dest='polling_interval',
                            help='The time (in minutes) to wait between successive polling of impala. Default is 3',
                            type=int,
                            default=3)
online_manager.add_argument('--max_delay',
                            action='store',
                            dest='max_delay',
                            help="The max delay (in hours) that the system should get to. If there's a bigger delay - the "
                                 "script will continue to run as usual, but error message will be printed. Default is 3",
                            type=int,
                            default=3)

throttling = argparse.ArgumentParser(add_help=False)
throttling.add_argument('--max_batch_size',
                        action='store',
                        dest='max_batch_size',
                        help="The maximal batch size (number of events) to read from impala. "
                             "This parameter is translated into BDP's forwardingBatchSizeInMinutes parameter",
                        required=True,
                        type=int)
throttling.add_argument('--force_max_batch_size_in_minutes',
                        action='store',
                        dest='force_max_batch_size_in_minutes',
                        help="The maximal batch size (in minutes) to read from impala. "
                             "This parameter overrides --max_batch_size. Use it only if you know what you're doing, "
                             "or if running the script without it results with too small batch size in minutes "
                             "(in this case a warning will be displayed)",
                        default=None,
                        type=int)
throttling.add_argument('--max_gap',
                        action='store',
                        dest='max_gap',
                        help="The maximal gap size (number of events) which is allowed before stopping and waiting. "
                             "This parameter is translated into BDP's maxSourceDestinationTimeGap parameter",
                        required=True,
                        type=int)
throttling.add_argument('--convert_to_minutes_timeout',
                        action='store',
                        dest='convert_to_minutes_timeout',
                        help="When calculating duration in minutes out of max batch size and max gap daily queries "
                             "are performed against impala. The more days we query - the better the duration estimate "
                             "is. If you want this process to take only a limited amount of time, impala queries will "
                             "stop by the end of the specified timeout (in minutes), and the calculation will begin. "
                             "If given negative number, no timeout will occur",
                        type=int,
                        required=True)
