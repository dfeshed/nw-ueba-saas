import re
import subprocess


def are_tasks_running(logger, task_names):
    logger.info('making sure all relevant Samza tasks are up and running (' +
                ', '.join(task_names) + ')...')
    ps_output = subprocess.Popen('ps -ef', shell=True, env={'LANG': 'C'}, stdout=subprocess.PIPE).communicate()[0]
    running_tasks = re.findall(r'STREAM_TASK_NAME=([a-zA-z0-9_-]+)', ps_output)
    not_running_tasks = set(task_names).difference(running_tasks)
    if len(not_running_tasks) > 0:
        logger.error('please start the following Samza tasks and then try again: ' + ', '.join(not_running_tasks))
        return False
    logger.info('OK')
    return True
