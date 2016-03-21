import datetime
import json

import config


def interval_to_str(start_time, end_time):
    return timestamp_to_str(start_time) + ' -> ' + timestamp_to_str(end_time)

def timestamp_to_str(time):
    return str(datetime.datetime.fromtimestamp(time))

class Printer:
    def __init__(self, verbose):
        self.verbose = verbose

    def __call__(self, *args):
        if self.verbose:
            print ' '.join([str(s) for s in args])

print_verbose = Printer(config.verbose)

def print_json(j, force = True):
    s = json.dumps(j, indent = 4, sort_keys = True)
    if force:
        print s
    else:
        print_verbose(s)