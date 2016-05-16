import shutil
import subprocess
import time
import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from automatic_config.common.utils import time_utils


def run(logger, path_to_bdp_properties, start, end, block, additional_cmd_params=[]):
    if (start is None and end is not None) or (start is not None and end is None):
        raise Exception('start and end must both be None or not None')
    if start is not None:
        duration_hours = _get_duration_hours(end, start)
    shutil.copyfile(path_to_bdp_properties, '/home/cloudera/fortscale/BDPtool/target/resources/bdp.properties')
    call_args = ['nohup',
                 'java',
                 '-Duser.timezone=UTC',
                 '-jar',
                 'bdp-0.0.1-SNAPSHOT.jar']
    if start is not None:
        call_args += ['bdp_start_time=' + time_utils.get_datetime(start).strftime("%Y-%m-%d %H:%M:%S"),
                      'bdp_duration_hours=' + duration_hours,
                      'batch_duration_size=' + duration_hours]
    call_args += additional_cmd_params
    output_file_name = os.path.splitext(os.path.basename(path_to_bdp_properties))[0] + '.out'
    logger.info('running ' + ' '.join(call_args) + ' > ' + output_file_name)
    with open(output_file_name, 'w') as f:
        p = (subprocess.call if block else subprocess.Popen)(call_args,
                                                             cwd='/home/cloudera/fortscale/BDPtool/target',
                                                             stdout=f)
    if not block:
        return lambda: p.poll() is None and p.kill()


def _get_duration_hours(end, start):
    duration_hours = time_utils.get_epoch(end) - time_utils.get_epoch(start)
    if duration_hours % (60 * 60) != 0:
        raise Exception('end time must be a round number of hours after start time')
    return duration_hours / 60 * 60


def validate_by_polling(status_cb, status_target, no_progress_timeout, polling):
    status = status_cb()
    last_progress_time = time.time()
    while status != status_target:
        if time.time() - last_progress_time > no_progress_timeout:
            return False
        time.sleep(polling)
        s = status_cb()
        if s != status:
            status = s
            last_progress_time = time.time()

    return True
