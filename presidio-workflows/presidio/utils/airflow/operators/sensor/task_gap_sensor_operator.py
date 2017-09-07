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
        self._root_external_dag_id = external_dag_id.split(".", 1)[0]
        self._gapped_root_dag_run = None
        self._gapped_dag_run = None
        self._external_task_id = external_task_id

    def poke(self, context):
        '''

        @return: bool - whether there are tasks to wait for.
        '''
        if(self._gapped_root_dag_run == None):
            self._init_gapped_dag_runs(context['execution_date'])
            if(self._gapped_root_dag_run == None):
                #The start time of the external gapped dag run is more recent than the needed gap.
                #so there is nothing to sense here.
                return True

        logging.info(
            'Poking for the following'
            '{self._external_dag_id}.'
            '{self._external_task_id} on '
            '{self._gapped_root_dag_run.execution_date} ... '.format(**locals()))

        session = settings.Session()
        self._gapped_root_dag_run.refresh_from_db(session=session)
        is_finished_wait_for_gapped_task = True
        if(self._gapped_root_dag_run.get_state() == State.RUNNING):
            is_finished_wait_for_gapped_task = False
            if(self._gapped_dag_run != None):
                self._gapped_root_dag_run.refresh_from_db(session=session)
                external_task_instance = self._gapped_dag_run.get_task_instance(task_id= self._external_task_id, session=session)
                if(external_task_instance != None and external_task_instance.end_date != None):
                    is_finished_wait_for_gapped_task = True

        return is_finished_wait_for_gapped_task

    def _init_gapped_dag_runs(self, execution_date):
        execution_date_lt = execution_date - self._execution_delta

        session = settings.Session()
        self._gapped_root_dag_run = session.query(DagRun).filter(
            DagRun.dag_id == self._root_external_dag_id,
            DagRun.execution_date < execution_date_lt,
            DagRun.state == State.RUNNING,
        ).order_by(
            DagRun.execution_date.desc()
        ).first()

        if(self._gapped_root_dag_run != None):
            self._gapped_dag_run = session.query(DagRun).filter(
                DagRun.dag_id == self._external_dag_id,
                DagRun.execution_date < execution_date_lt,
                DagRun.state == State.RUNNING,
            ).order_by(
                DagRun.execution_date.desc()
            ).first()

    def get_external_dag_id(self):
        return self._external_dag_id

    def get_external_task_id(self):
        return self._external_task_id

    def get_execution_delta(self):
        return self._execution_delta
