from datetime import timedelta

from airflow.models import BaseOperator

from task_gap_sensor_operator import TaskGapSensorOperator


class TaskSensorService(object):
    '''
    TaskSensorService enable different kind of sensors on task
    In the future we might use this service to run these sensors sequentially.
    '''

    def add_task_sequential_sensor(self, task, poke_interval=1):
        '''
        ensure that task instances of a task are running sequentially.
        :param task:
        :type task: BaseOperator
        :param poke_interval: 
        :type poke_interval: int
        '''
        task_id = '%s_%s' % (task.task_id, 'sequential_sensor')
        sensor = TaskGapSensorOperator(dag=task.dag, task_id=task_id, external_dag_id=task.dag_id,
                                       external_task_id=task.task_id, execution_delta=timedelta(seconds=1),
                                       poke_interval=poke_interval)
        task.set_upstream(sensor)

    def add_task_gap_sensor(self, task, gapped_task, execution_delta, poke_interval=60):
        '''

        :param task: 
        :type task: BaseOperator
        :param gapped_task: 
        :type gapped_task: BaseOperator
        :param execution_delta:
        :type execution_delta: timedelta
        :param poke_interval: 
        :type poke_interval: int
        :return: 
        '''
        task_id = '%s_%s_%s' % (gapped_task.task_id, task.task_id, 'gap_sensor')
        sensor = TaskGapSensorOperator(dag=task.dag, task_id=task_id, external_dag_id=gapped_task.dag_id,
                                       external_task_id=gapped_task.task_id, execution_delta=execution_delta,
                                       poke_interval=poke_interval)
        task.set_upstream(sensor)