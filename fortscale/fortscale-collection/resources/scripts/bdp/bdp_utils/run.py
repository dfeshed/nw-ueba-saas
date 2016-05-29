import subprocess
import time
import os
import sys
from overrides import overrides as overrides_file

from mongo import get_collections_time_boundary
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from automatic_config.common.utils import time_utils


class Runner:
    def __init__(self, name, logger, host, block):
        self._name = name
        self._logger = logger
        self._host = host
        self._block = block
        self._start = None
        self._end = None

    def set_start(self, start):
        self._start = start
        return self

    def set_end(self, end):
        self._end = end
        return self

    def get_start(self):
        return self._start

    def get_end(self):
        return self._end

    def infer_start_and_end(self, collection_names_regex):
        self._start = get_collections_time_boundary(host=self._host,
                                                    collection_names_regex=collection_names_regex,
                                                    is_start=True)
        self._end = get_collections_time_boundary(host=self._host,
                                                  collection_names_regex=collection_names_regex,
                                                  is_start=False)
        return self

    @staticmethod
    def _get_duration_hours(start, end):
        duration_hours = time_utils.get_epochtime(end) - time_utils.get_epochtime(start)
        if duration_hours % (60 * 60) != 0:
            raise Exception('end time must be a round number of hours after start time')
        return duration_hours / 60 * 60

    def run(self, overrides_key=None, overrides=[]):
        if (self._start is None and self._end is not None) or (self._start is not None and self._end is None):
            raise Exception('start and end must both be None or not None')
        call_args = ['nohup',
                     'java',
                     '-Duser.timezone=UTC',
                     '-jar',
                     'bdp-0.0.1-SNAPSHOT.jar']
        if self._start is not None:
            # make sure we're dealing with integer hours
            start = time_utils.get_epochtime(self._start)
            end = time_utils.get_epochtime(self._end)
            end += (start - end) % (60 * 60)
            duration_hours = self._get_duration_hours(start, end)
            call_args += ['bdp_start_time=' + time_utils.get_datetime(start).strftime("%Y-%m-%dT%H:%M:%S"),
                          'bdp_duration_hours=' + str(duration_hours),
                          'batch_duration_size=' + str(duration_hours)]
        call_args += overrides_file['common'] + \
                     (overrides_file[overrides_key] if overrides_key is not None else []) + \
                     overrides
        output_file_name = self._name + '.out'
        self._logger.info('running ' + ' '.join(call_args) + ' > ' + output_file_name)
        with open(output_file_name, 'w') as f:
            p = (subprocess.call if self._block else subprocess.Popen)(call_args,
                                                                       cwd='/home/cloudera/fortscale/BDPtool/target',
                                                                       stdout=f)
        if not self._block:
            return lambda: p.poll() is None and p.kill()


def validate_by_polling(logger, progress_cb, is_done_cb, no_progress_timeout, polling):
    progress = progress_cb()
    last_progress_time = time.time()
    while not is_done_cb(progress):
        if 0 <= no_progress_timeout < time.time() - last_progress_time:
            logger.error('timeout reached')
            return False
        logger.info('validation failed. going to sleep for ' + str(polling / 60) +
                    ' minute' + ('s' if polling / 60 > 1 else '') + ' and then will try again...')
        time.sleep(polling)
        p = progress_cb()
        if p != progress:
            progress = p
            last_progress_time = time.time()

    return True
