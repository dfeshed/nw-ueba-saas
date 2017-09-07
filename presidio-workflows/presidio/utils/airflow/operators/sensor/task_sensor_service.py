import logging

from datetime import timedelta

from airflow.models import BaseOperator
from airflow.operators.sensors import BaseSensorOperator
from airflow.operators.python_operator import ShortCircuitOperator

from task_gap_sensor_operator import TaskGapSensorOperator


class TaskSensorService(object):


    '''
    TaskSensorService enable to add different kind of sensors to a task. These sensor only trigger the specific task.
    In addition it should be used to add short circuit to a task
    It wires the sensors as down stream of the short circuit operators.
    In the future we might use this service to run these sensors sequentially.
    '''

    def __init__(self):
        self._task_sensors_dict = {}
        self._task_short_circuit_operators_dict = {}

    def add_task_sequential_sensor(self, task, poke_interval=10):
        '''
        ensure that task instances of a task are running sequentially even when few dag instances may run in parallel.
        The sensor checks every poked interval if all the previous task instances ran already
        :param task: The task that should run sequentially
        :type task: BaseOperator
        :param poke_interval: The interval between 2 checks. 
        :type poke_interval: int
        '''
        sensor_task_id = '%s_%s' % (task.task_id, 'sequential_sensor')
        sensor = TaskGapSensorOperator(dag=task.dag, task_id=sensor_task_id, external_dag_id=task.dag_id,
                                       external_task_id=task.task_id, execution_delta=timedelta(seconds=1),
                                       poke_interval=poke_interval)
        self._add_sensor_to_task(task,sensor)


    def add_task_gap_sensor(self, task, gapped_task, execution_delta, poke_interval=60):
        '''
        throttle a task instance of 'task' if T1 - T2 > execution_delta where:
        1. T1 is the start time (execution_date) of the data that the task instance is going to run on.
        2. T2 is the start time (execution_date) of the data that a task instance of 'gapped_task' still didn't finish (or even didn't start)

        :param task: the task to throttle
        :type task: BaseOperator
        :param gapped_task: the task to sense
        :type gapped_task: BaseOperator
        :param execution_delta: a time delta which above it the task instance should be throttled.
        :type execution_delta: timedelta
        :param poke_interval: The interval between 2 checks. 
        :type poke_interval: int
        :return: 
        '''
        sensor_task_id = '%s_%s_%s' % (gapped_task.task_id, task.task_id, 'gap_sensor')
        sensor = TaskGapSensorOperator(dag=task.dag, task_id=sensor_task_id, external_dag_id=gapped_task.dag_id,
                                       external_task_id=gapped_task.task_id, execution_delta=execution_delta,
                                       poke_interval=poke_interval)
        self._add_sensor_to_task(task,sensor)

    def add_task_short_circuit(self, task, short_circuit_operator):
        '''
        add short circuit to a task and all its sensors.

        :param task: 
        :type task: BaseOperator
        :param short_circuit_operator: 
        :type short_circuit_operator: ShortCircuitOperator
        :return: 
        '''
        self._add_short_circuit_to_task_list(task, short_circuit_operator)
        logging.info(
            '{short_circuit_operator.task_id} set downstream the task '
            '{task.task_id}'.format(**locals()))
        short_circuit_operator.set_downstream(task)

        task_sensor_list = self._get_task_sensor_list(task)
        if task_sensor_list is not None:
            logging.info(
                '{short_circuit_operator.task_id} set downstream the sensors: '
                '{task_sensor_list}'.format(**locals()))
            short_circuit_operator.set_downstream(task_sensor_list)

    def _add_sensor_to_task(self, task, sensor):
        '''
        Adding a sensor to a task. 
        Axiom: The sensor is in the upstream of one task only.
        This method is private since there is no way to ensure the axiom if the sensor may be define outside this class.

        :param task: 
        :type task: BaseOperator
        :param sensor: 
        :type sensor: BaseSensorOperator
        :return: 
        '''

        task.set_upstream(sensor)
        self._add_sensor_to_task_list(task, sensor)
        self._wire_task_short_circuit_list_to_new_sensor(task, sensor)

    def _wire_task_short_circuit_list_to_new_sensor(self, task, sensor):
        '''
        wire a task sensor as down stream of all the short circuit operators of the task.

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
        :rtype: list
        '''
        return self._task_sensors_dict.get(task.task_id)

    def _add_sensor_to_task_list(self, task, sensor):
        '''

        :param task: 
        :type task: BaseOperator
        :param sensor: 
        :type sensor: BaseSensorOperator
        :return: the sensor list of the task
        :rtype: list
        '''
        return self._add_operator_to_task_list(task,sensor,self._task_sensors_dict)

    def _get_task_short_circuit_list(self,task):
        '''

        :param task: 
        :type task: BaseOperator
        :return: the short circuit list of the task
        :rtype: list
        '''
        return self._task_short_circuit_operators_dict.get(task.task_id)

    def _add_short_circuit_to_task_list(self, task, short_circuit_operator):
        '''

        :param task: 
        :type task: BaseOperator
        :param sensor: 
        :type short_circuit_operator: ShortCircuitOperator
        :return: the short circuit list of the task
        :rtype: list
        '''
        return self._add_operator_to_task_list(task, short_circuit_operator, self._task_short_circuit_operators_dict)

    @staticmethod
    def _add_operator_to_task_list(task, operator, task_to_operator_list_dict):
        '''

        :param task: 
        :type task: BaseOperator
        :param operator: 
        :type operator: BaseOperator
        :param task_to_operator_list_dict:
        :type task_to_operator_list_dict: dict
        :return: the list of operators that the operator was added to.
        :rtype: list
        '''
        task_operator_list = task_to_operator_list_dict.get(task.task_id)
        if task_operator_list is None:
            task_operator_list = [operator]
            task_to_operator_list_dict[task.task_id] = task_operator_list
        else:
            task_operator_list.append(operator)
        return task_operator_list