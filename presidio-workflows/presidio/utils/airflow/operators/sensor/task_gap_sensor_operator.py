import logging

from airflow.operators.sensors import BaseSensorOperator
from airflow.utils.state import State
from airflow import settings
from airflow.models import TaskInstance,DagRun


class TaskGapSensorOperator(BaseSensorOperator):
    """
    Sensor all task instances with the specified task_id until all task instances with execution_date that has greater 
    gap than the given delta time finished running (reached to one of the following states: success, failed, skipped)

    :param external_dag_id: The dag_id that contains the task you want to
        wait for
    :type external_dag_id: string
    :param external_task_id: The task_id that contains the task you want to
        wait for
    :type external_task_id: string
    :param execution_delta: the time difference that of all task instances that should be sensored.
    :type execution_delta: datetime.timedelta
    """
    ui_color = '#19647e'
    ui_fgcolor = '#fff'

    def __init__(
            self,
            external_dag_id,
            external_task_id,
            execution_delta,
            *args, **kwargs):
        super(TaskGapSensorOperator, self).__init__(*args, **kwargs)

        self._execution_delta = execution_delta
        self._external_dag_id = external_dag_id
        self._external_task_id = external_task_id

    def poke(self, context):
        '''

        @return: bool - whether there are tasks to wait for.
        '''

        execution_date_lt = context['execution_date'] - self._execution_delta

        logging.info(
            'Poking for the following'
            '{self._external_dag_id}.'
            '{self._external_task_id} on '
            '{execution_date_lt} ... '.format(**locals()))

        is_finished_wait_for_tasks = True
        if(self.get_num_of_dag_runs_to_wait_for(execution_date_lt) > 0):
            is_finished_wait_for_tasks = self.get_num_of_task_instances_to_wait_for(execution_date_lt) == 0

        return is_finished_wait_for_tasks

    def get_num_of_dag_runs_to_wait_for(self, execution_date_lt):
        session = settings.Session()
        num_of_dag_runs_to_wait_for = session.query(DagRun).filter(
            DagRun.dag_id == self._external_dag_id,
            DagRun.execution_date < execution_date_lt,
            DagRun.state == State.RUNNING,
        ).count()

        session.commit()
        session.close()
        return num_of_dag_runs_to_wait_for

    def get_num_of_task_instances_to_wait_for(self, execution_date_lt):
        TI = TaskInstance

        session = settings.Session()
        num_of_task_instances_to_wait_for = session.query(TI).filter(
            TI.dag_id == self._external_dag_id,
            TI.task_id == self._external_task_id,
            TI.end_date.is_(None),
            TI.execution_date < execution_date_lt,
        ).count()

        session.commit()
        session.close()

        return num_of_task_instances_to_wait_for

    def get_external_dag_id(self):
        return self._external_dag_id

    def get_external_task_id(self):
        return self._external_task_id

    def get_execution_delta(self):
        return self._execution_delta
