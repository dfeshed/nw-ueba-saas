import subprocess
import time
import glob
import signal
import os
import sys
from overrides import overrides as overrides_file

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from automatic_config.common.utils import time_utils, io
from automatic_config.common.utils.mongo import get_collections_time_boundary


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
        self._end = end - 1  # subtract 1 because bdp uses inclusive end time
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
        duration_seconds = time_utils.get_epochtime(end) - time_utils.get_epochtime(start)
        if duration_seconds % (60 * 60) != 0:
            raise Exception('end time must be a round number of hours after start time')
        return int(duration_seconds / (60 * 60))

    def run(self, overrides_key=None, overrides=[]):
        if (self._start is None and self._end is not None) or (self._start is not None and self._end is None):
            raise Exception('start and end must both be None or not None')
        target_dir = '/home/cloudera/fortscale/BDPtool/target'
        call_args = ['nohup',
                     'java',
                     '-Duser.timezone=UTC',
                     '-jar',
                     os.path.basename(glob.glob(target_dir + '/bdp-*-SNAPSHOT.jar')[0])]
        call_overrides = []
        if self._start is not None:
            # make sure we're dealing with integer hours
            start = time_utils.get_epochtime(self._start)
            end = time_utils.get_epochtime(self._end)
            end += (start - end) % (60 * 60)
            duration_hours = self._get_duration_hours(start, end)
            call_overrides += [
                'bdp_start_time = ' + time_utils.get_datetime(start).strftime("%Y-%m-%dT%H:%M:%S"),
                'bdp_end_time = ' + time_utils.get_datetime(end).strftime("%Y-%m-%dT%H:%M:%S"),
                'bdp_duration_hours = ' + str(duration_hours),
                'batch_duration_size = ' + str(duration_hours)
            ]
        call_overrides += overrides_file['common'] + \
                          (overrides_file[overrides_key] if overrides_key is not None else []) + \
                          overrides
        self._update_overrides(call_overrides)
        output_file_name = self._name + '.out'
        self._logger.info('running ' + ' '.join(call_args) + ' >> ' + output_file_name)
        with open(output_file_name, 'a') as f:
            p = subprocess.Popen(call_args, stdin=subprocess.PIPE, cwd=target_dir, stdout=f)
            for call_override in call_overrides:
                if call_override.replace(' ', '') == 'single_step=Cleanup':
                    p.communicate('Yes')
                    break
            if self._block:
                p.wait()
        if not self._block:
            def kill():
                children_pids = subprocess.Popen(['ps', '-o', 'pid', '--ppid', str(p.pid), '--noheaders'],
                                                 stdout=subprocess.PIPE).communicate()[0]
                for child_pid in filter(lambda child_pid: child_pid.strip() != '', children_pids.split('\n')):
                    child_pid = int(child_pid)
                    self._logger.info("killing BDP's child process (pid %d)" % child_pid)
                    os.kill(child_pid, signal.SIGTERM)
                if p.poll() is None:
                    self._logger.info('killing BDP (pid %d)' % p.pid)
                    p.kill()
            return kill

    def _update_overrides(self, call_overrides):
        self._logger.info('updating overrides:' + '\n\t'.join([''] + call_overrides))
        bdp_overrides_file_path = '/home/cloudera/fortscale/BDPtool/target/resources/bdp-overriding.properties'
        io.backup(path=bdp_overrides_file_path)
        with open(bdp_overrides_file_path, 'w') as f:
            f.write('\n'.join(call_overrides))


def validate_by_polling(logger, progress_cb, is_done_cb, no_progress_timeout, polling):
    progress = progress_cb()
    last_progress_time = time.time()
    while not is_done_cb(progress):
        if 0 <= no_progress_timeout < time.time() - last_progress_time:
            logger.error('timeout reached')
            return False
        logger.info('current progress: ' + str(progress))
        logger.info('validation failed. going to sleep for ' + str(polling / 60) +
                    ' minute' + ('s' if polling / 60 > 1 else '') + ' and then will try again...')
        time.sleep(polling)
        p = progress_cb()
        if p != progress:
            progress = p
            last_progress_time = time.time()

    return True
