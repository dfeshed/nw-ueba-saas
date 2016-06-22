import re
import subprocess
from cm_api.api_client import ApiResource


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


def restart_task(logger, host, task_name):
    logger.info('restarting samza task ' + task_name + '...')
    api = ApiResource(host, username='admin', password='admin')
    cluster = filter(lambda c: c.name == 'cluster', api.get_all_clusters())[0]
    fsstreaming = filter(lambda service: service.name == 'fsstreaming', cluster.get_all_services())[0]
    task = [s for s in fsstreaming.get_all_roles() if s.type == task_name][0]
    if fsstreaming.restart_roles(task.name)[0].wait().success:
        logger.info('task restarted successfully')
        return True
    else:
        logger.error('task failed to restart')
        return False
