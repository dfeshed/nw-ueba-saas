import glob
import os
import re
import signal
import subprocess
import sys
from cm_api.api_client import ApiResource

import time
from overrides import overrides as overrides_file
from samza import restart_all_tasks

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from automatic_config.common.utils import time_utils, io
from automatic_config.common.utils.mongo import get_collections_time_boundary


def validate_bdp_flag(is_online_mode):
    with open('/home/cloudera/fortscale/streaming/config/fortscale-overriding-streaming.properties', 'r') as f:
        for l in f.readlines():
            match = re.findall('fortscale.bdp.run\W*=\W*(.+)', l)
            if match:
                if {'true': True, 'false': False}[match[0]] == is_online_mode:
                    raise Exception('/home/cloudera/fortscale/streaming/config/fortscale-overriding-streaming.properties has the line ' +
                                    l + ', while it should be the opposite')
                return
    raise Exception('/home/cloudera/fortscale/streaming/config/fortscale-overriding-streaming.properties doesn not '
                    'specify the fortscale.bdp.run flag. Please specify it and try again')


class Runner(object):
    def __init__(self, name, logger, host, block, block_until_log_reached=None, is_online_mode=False):
        if not block and block_until_log_reached:
            raise Exception('block_until_log_reached can be specified only when block=True')
        validate_bdp_flag(is_online_mode=is_online_mode)
        self._name = name
        self._logger = logger
        self._host = host
        self._block = block
        self._block_until_log_reached = block_until_log_reached
        self._start = None
        self._end = None

    def set_start(self, start):
        self._start = time_utils.get_epochtime(start)
        return self

    def set_end(self, end):
        self._end = time_utils.get_epochtime(end) - 1  # subtract 1 because bdp uses inclusive end time
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

    def _create_overrides(self, overrides_key, overrides):
        call_overrides = []
        if self._start is not None:
            call_overrides += self._create_time_interval_overrides()
        call_overrides += overrides_file['common'] + (overrides_file[overrides_key]
                                                      if overrides_key is not None
                                                      else []) + overrides
        return call_overrides

    def run(self, overrides_key=None, overrides=[]):
        if (self._start is None and self._end is not None) or (self._start is not None and self._end is None):
            raise Exception('start and end must both be None or not None')
        target_dir = '/home/cloudera/fortscale/BDPtool/target'
        call_args = ['nohup',
                     'java',
                     '-Duser.timezone=UTC',
                     '-jar',
                     os.path.basename(glob.glob(target_dir + '/bdp-*-SNAPSHOT.jar')[0])]
        call_overrides = self._create_overrides(overrides_key=overrides_key, overrides=overrides)
        self._update_overrides(call_overrides)
        output_file_name = self._name + '.out'
        self._logger.info('running ' + ' '.join(call_args) + ' >> ' + output_file_name)
        with open(output_file_name, 'a') as f:
            p = subprocess.Popen(call_args, stdin=subprocess.PIPE, cwd=target_dir, stdout=f)
            for call_override in call_overrides:
                if call_override.replace(' ', '') == 'single_step=Cleanup':
                    if type(self) == Cleaner:
                        p.communicate('Yes')
                        break
                    else:
                        raise Exception('if you want to perform a cleanup - use Cleaner instead of Runner')
            if self._block:
                if self._block_until_log_reached:
                    self._wait_for_log(bdp_output_file=output_file_name)
                    self._create_killer(p)()
                else:
                    p.wait()
            else:
                return self._create_killer(p)

    def _create_time_interval_overrides(self):
        start = time_utils.get_epochtime(self._start)
        end = time_utils.get_epochtime(self._end)
        # make sure we're dealing with integer hours
        start -= start % (60 * 60)
        end += (start - end) % (60 * 60)
        duration_seconds = time_utils.get_epochtime(end) - time_utils.get_epochtime(start)
        if duration_seconds % (60 * 60) != 0:
            raise Exception('end time must be a round number of hours after start time')
        duration_hours = int(duration_seconds / (60 * 60))
        return [
            'bdp_start_time = ' + time_utils.get_datetime(start).strftime("%Y-%m-%dT%H:%M:%S"),
            'bdp_end_time = ' + time_utils.get_datetime(end).strftime("%Y-%m-%dT%H:%M:%S"),
            'bdp_duration_hours = ' + str(duration_hours),
            'batch_duration_size = ' + str(duration_hours)
        ]

    def _create_killer(self, p):
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

    def _wait_for_log(self, bdp_output_file):
        args = ['tail', '-f', '-n', '0', bdp_output_file]
        self._logger.info('waiting for "' + self._block_until_log_reached + '" by running ' + ' '.join(args) + '...')
        tail_p = subprocess.Popen(args, stdout=subprocess.PIPE)
        while True:
            line = tail_p.stdout.readline()
            if self._block_until_log_reached in line:
                break
        tail_p.kill()
        self._logger.info('found it')

    def _update_overrides(self, call_overrides):
        self._logger.info('updating overrides:' + '\n\t'.join([''] + call_overrides))
        bdp_overrides_file_path = '/home/cloudera/fortscale/BDPtool/target/resources/bdp-overriding.properties'
        io.backup(path=bdp_overrides_file_path)
        with open(bdp_overrides_file_path, 'w') as f:
            f.write('\n'.join(call_overrides))


class Cleaner(Runner):
    def __init__(self, name, logger, host):
        super(Cleaner, self).__init__(name=name,
                                      logger=logger,
                                      host=host,
                                      block=True)

    def run(self, overrides_key=None, overrides=[]):
        has_single_step = False
        has_cleanup_step = False
        for call_override in self._create_overrides(overrides_key=overrides_key, overrides=overrides):
            call_override = call_override.replace(' ', '')
            if call_override == 'single_step=Cleanup':
                has_single_step = True
            if 'cleanup_step=' in call_override:
                has_cleanup_step = True
        if not has_cleanup_step:
            raise Exception('must specify cleanup_step')
        if not has_single_step:
            overrides.append('single_step = Cleanup')
        super(Cleaner, self).run(overrides_key=overrides_key, overrides=overrides)
        return self._start_services()

    def _start_services(self):
        self._logger.info('starting kafka...')
        api = ApiResource(self._host, username='admin', password='admin')
        cluster = filter(lambda c: c.name == 'cluster', api.get_all_clusters())[0]
        kafka = filter(lambda service: service.name == 'kafka', cluster.get_all_services())[0]
        if kafka.serviceState != 'STOPPED':
            raise Exception('kafka should be STOPPED, but it is ' + kafka.serviceState)
        if kafka.start().wait().success:
            self._logger.info('kafka started successfully')
        else:
            self._logger.error('kafka failed to start')
            return False
        return restart_all_tasks(logger=self._logger, host=self._host)


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
