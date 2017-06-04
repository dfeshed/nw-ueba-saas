import logging

from datetime import timedelta

from airflow.models import BaseOperator
from airflow.operators.sensors import BaseSensorOperator
from airflow.operators.python_operator import ShortCircuitOperator

from task_gap_sensor_operator import TaskGapSensorOperator


class TaskSensorService(object):


    '''
    TaskSensorService enable different kind of sensors on task
    In the future we might use this service to run these sensors sequentially.
    '''

    def __init__(self):
        self._task_sensors_dict = {}
        self._task_short_circuit_dict = {}

    def add_sensor_to_task(self,task, sensor):
        '''

        :param task: 
        :type task: BaseOperator
        :param sensor: 
        :type sensor: BaseSensorOperator
        :return: 
        '''

        task.set_upstream(sensor)
        self._add_sensor_to_task_list(task,sensor)
        self._wire_task_short_circuit_list_to_new_sensor(task,sensor)

    def add_task_sequential_sensor(self, task, poke_interval=1):
        '''
        ensure that task instances of a task are running sequentially.
        :param task:
        :type task: BaseOperator
        :param poke_interval: 
        :type poke_interval: int
        '''
        sensor_task_id = '%s_%s' % (task.task_id, 'sequential_sensor')
        sensor = TaskGapSensorOperator(dag=task.dag, task_id=sensor_task_id, external_dag_id=task.dag_id,
                                       external_task_id=task.task_id, execution_delta=timedelta(seconds=1),
                                       poke_interval=poke_interval)
        self.add_sensor_to_task(task,sensor)


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
        sensor_task_id = '%s_%s_%s' % (gapped_task.task_id, task.task_id, 'gap_sensor')
        sensor = TaskGapSensorOperator(dag=task.dag, task_id=sensor_task_id, external_dag_id=gapped_task.dag_id,
                                       external_task_id=gapped_task.task_id, execution_delta=execution_delta,
                                       poke_interval=poke_interval)
        self.add_sensor_to_task(task,sensor)

    def add_task_short_circuit(self, task, short_circuit_operator):
        '''

        :param task: 
        :type task: BaseOperator
        :param short_circuit_operator: 
        :type short_circuit_operator: ShortCircuitOperator
        :return: 
        '''
        self._add_short_circuit_to_task_list(task, short_circuit_operator)
        task_sensor_list = self._get_task_sensor_list(task)
        if task_sensor_list is None:
            logging.info(
                '{short_circuit_operator.task_id} set downstream the task '
                '{task.task_id}'.format(**locals()))
            short_circuit_operator.set_downstream(task)
        else:
            logging.info(
                '{short_circuit_operator.task_id} set downstream the sensors: '
                '{task_sensor_list}'.format(**locals()))
            short_circuit_operator.set_downstream(task_sensor_list)


    def _wire_task_short_circuit_list_to_new_sensor(self, task, sensor):
        '''

        :param task: 
        :type task: BaseOperator
        :param sensor: 
        :type sensor: BaseSensorOperator
        :return: 
        '''
        task_short_circuit_list = self._get_task_short_circuit_list(task)
        if task_short_circuit_list is not None:
            sensor.set_upstream(task_short_circuit_list)
            logging.info(
                'wire sensor '
                '{sensor.task_id} to task_short_circuit_list:'
                '{task_short_circuit_list}'.format(**locals()))
        else:
            logging.info(
                'no task_short_circuit_list to wire sensor'
                '{sensor.task_id} to.'.format(**locals()))


    def _get_task_sensor_list(self,task):
        '''

        :param task: 
        :type task: BaseOperator
        :return: the sensor list of the task
        '''
        return self._task_sensors_dict.get(task.task_id)

    def _add_sensor_to_task_list(self, task, sensor):
        '''

        :param task: 
        :type task: BaseOperator
        :param sensor: 
        :type sensor: BaseSensorOperator
        :return: the sensor list of the task
        '''
        return self._add_operator_to_task_list(task,sensor,self._task_sensors_dict)

    def _get_task_short_circuit_list(self,task):
        '''

        :param task: 
        :type task: BaseOperator
        :return: the sensor list of the task
        '''
        return self._task_short_circuit_dict.get(task.task_id)

    def _add_short_circuit_to_task_list(self, task, short_circuit_operator):
        '''

        :param task: 
        :type task: BaseOperator
        :param sensor: 
        :type short_circuit_operator: ShortCircuitOperator
        :return: the sensor list of the task
        '''
        return self._add_operator_to_task_list(task,short_circuit_operator,self._task_short_circuit_dict)

    def _add_operator_to_task_list(self, task, operator, task_to_operator_list_dict):
        '''

        :param task: 
        :type task: BaseOperator
        :param operator: 
        :type operator: BaseOperator
        :param task_to_operator_list_dict:
        :type task_to_operator_list_dict: dict
        :return: the sensor list of the task
        '''
        task_operator_list = task_to_operator_list_dict.get(task.task_id)
        if task_operator_list is None:
            task_operator_list = [operator]
            task_to_operator_list_dict[task.task_id] = task_operator_list
        else:
            task_operator_list.append(operator)
        return task_operator_list