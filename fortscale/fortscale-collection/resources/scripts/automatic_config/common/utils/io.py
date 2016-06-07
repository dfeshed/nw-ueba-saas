import json
import os
import signal
import sys
import time
import datetime
import zipfile
from contextlib import contextmanager
import shutil
from .. import config


class FlushFile():
    def __init__(self, f):
        self.f = f
    def __getattr__(self,name):
        return object.__getattribute__(self.f, name)
    def write(self, x):
        self.f.write(x)
        self.f.flush()
    def flush(self):
        self.f.flush()
sys.stdout = FlushFile(sys.stdout)


class Printer:
    def __init__(self, verbose):
        self.verbose = verbose

    def __call__(self, *args):
        if self.verbose:
            args = [str(s) for s in args]
            if len(args) > 0:
                args.insert(0, time.strftime('%X') + ':')
            print ' '.join(args)

print_verbose = Printer(config.verbose)

def print_json(j, force = True):
    s = json.dumps(j, indent = 4, sort_keys = True)
    if force:
        print s
    else:
        print_verbose(s)

class DelayedKeyboardInterrupt:
    def __enter__(self):
        self._signal_received = None
        self._old_handler = signal.getsignal(signal.SIGINT)
        signal.signal(signal.SIGINT, self._handler)

    def _handler(self, signal, frame):
        self._signal_received = (signal, frame)

    def __exit__(self, *args):
        signal.signal(signal.SIGINT, self._old_handler)
        if self._signal_received is not None:
            self._old_handler(*self._signal_received)

class FileWriter:
    def __init__(self, path):
        self._path = path
        self._delayed_keyboard_interrupt = DelayedKeyboardInterrupt()

    def __enter__(self):
        self._delayed_keyboard_interrupt.__enter__()
        dir = os.path.dirname(self._path)
        if not os.path.exists(dir):
            os.makedirs(dir)
        self._f = open(self._path, 'w')
        return self._f.__enter__()

    def __exit__(self, *args):
        self._f.__exit__(*args)
        self._delayed_keyboard_interrupt.__exit__(*args)


def backup(path, suffix=None):
    now = str(datetime.datetime.now()).replace(' ', '_').replace(':', '-')
    now = now[:now.index('.')]
    backup_name = path + '.backup-' + (suffix + '-' if suffix is not None else '') + now
    shutil.copyfile(path, backup_name)
    return backup_name


@contextmanager
def open_overrides_file(overriding_path, jar_name, path_in_jar, create_if_not_exist=False):
    if os.path.isfile(overriding_path):
        f = open(overriding_path, 'r')
        yield f
        f.close()
    else:
        zf = zipfile.ZipFile('/home/cloudera/fortscale/streaming/lib/' + jar_name, 'r')
        f = zf.open(path_in_jar, 'r')
        if create_if_not_exist:
            with FileWriter(overriding_path) as overriding_f:
                shutil.copyfileobj(f, overriding_f)
        yield f
        f.close()
        zf.close()
