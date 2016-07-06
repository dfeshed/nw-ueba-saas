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


def _get_fsstreaming(host):
    api = ApiResource(host, username='admin', password='admin')
    cluster = filter(lambda c: c.name == 'cluster', api.get_all_clusters())[0]
    return filter(lambda service: service.name == 'fsstreaming', cluster.get_all_services())[0]


def _restart_tasks(logger, host, task_names=None):
    fsstreaming = _get_fsstreaming(host=host)
    restarts = {}
    for task in fsstreaming.get_all_roles():
        if task_names is None or task.type in task_names:
            logger.info('restarting samza task ' + task.type + '...')
            restarts[task.type] = fsstreaming.restart_roles(task.name)[0]
            if task_names is not None:
                task_names.remove(task.type)
    for task_name, restart in restarts.iteritems():
        if restart.wait().success:
            logger.info('task ' + task_name + ' restarted successfully')
        else:
            logger.error('task ' + task_name + ' failed to restart')
            return False
    if task_names is not None and len(task_names) > 0:
        logger.error('illegal task name: ' + ', '.join(task_names))
        return False
    return True


def restart_task(logger, host, task_name):
    return _restart_tasks(logger=logger, host=host, task_names=[task_name])


def restart_all_tasks(logger, host):
    logger.info('restarting all samza tasks...')
    return _restart_tasks(logger=logger, host=host)
