import shutil
import subprocess
import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from automatic_config.common.utils import time_utils


def run(logger, path_to_bdp_properties, start, end, block, additional_cmd_params=[]):
    duration_hours = _get_duration_hours(end, start)
    shutil.copyfile(path_to_bdp_properties, '/home/cloudera/fortscale/BDPtool/target/resources/bdp.properties')
    call_args = ['nohup',
                 'java',
                 '-Duser.timezone=UTC',
                 '-jar',
                 'bdp-0.0.1-SNAPSHOT.jar',
                 'bdp_start_time=' + time_utils.get_datetime(start).strftime("%Y-%m-%d %H:%M:%S"),
                 'bdp_duration_hours=' + duration_hours,
                 'batch_duration_size=' + duration_hours] + additional_cmd_params
    output_file_name = os.path.splitext(os.path.basename(path_to_bdp_properties))[0] + '.out'
    logger.info('running ' + ' '.join(call_args) + ' > ' + output_file_name)
    with open(output_file_name, 'w') as f:
        res = (subprocess.call if block else subprocess.Popen)(call_args,
                                                               cwd='/home/cloudera/fortscale/BDPtool/target',
                                                               stdout=f)
    if not block:
        return res.pid


def _get_duration_hours(end, start):
    duration_hours = time_utils.get_epoch(end) - time_utils.get_epoch(start)
    if duration_hours % (60 * 60) != 0:
        raise Exception('end time must be a round number of hours after start time')
    return duration_hours / 60 * 60
