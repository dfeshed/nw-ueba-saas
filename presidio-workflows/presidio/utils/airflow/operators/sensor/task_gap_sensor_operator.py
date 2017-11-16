import logging

from airflow import settings
from airflow.models import DagRun
from airflow.operators.sensors import BaseSensorOperator
from airflow.utils.state import State


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
        super(TaskGapSensorOperator, self).__init__(retries=4,retry_exponential_backoff=True,max_retry_delay=300,retry_delay=5,*args, **kwargs)

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
        session = settings.Session()
        is_finished_wait_for_gapped_task = self._is_finished_wait_for_gapped_task(context,session)

        return is_finished_wait_for_gapped_task

    def _is_finished_wait_for_gapped_task(self, context, session):
        '''

        @return: bool - whether there are tasks to wait for.
        '''
        if (self._gapped_root_dag_run == None):
            self._init_gapped_root_dag_run(context['execution_date'], session)
            if (self._gapped_root_dag_run == None):
                # The start time of the external gapped dag run is more recent than the needed gap.
                # so there is nothing to sense here.
                return True

        logging.info(
            'Poking for the following'
            '{self._external_dag_id}.'
            '{self._external_task_id} on '
            '{self._gapped_root_dag_run.execution_date} ... '.format(**locals()))

        self._gapped_root_dag_run.refresh_from_db(session=session)
        is_finished_wait_for_gapped_task = True
        root_state = self._gapped_root_dag_run.get_state()
        if (root_state == State.RUNNING):
            is_finished_wait_for_gapped_task = False
            self._refresh_gapped_dag_run(session)
            if (self._gapped_dag_run != None):
                gapped_dag_run_state = self._gapped_dag_run.get_state()
                if (gapped_dag_run_state in State.unfinished()):
                    external_task_instance = self._gapped_dag_run.get_task_instance(task_id=self._external_task_id,
                                                                                    session=session)
                    if (external_task_instance == None):
                        logging.info(
                            'Still poking since the dag run has not finished and the gapped task instance still have not started: '
                            'dag_id: {self._gapped_dag_run.dag_id} '
                            'run_id: {self._gapped_dag_run.run_id} '
                            'state: {gapped_dag_run_state} '.format(**locals()))
                    elif (external_task_instance.state in State.unfinished()):
                        logging.info(
                            'Still poking since the gapped task instance has not finished: '
                            'dag_id: {self._gapped_dag_run.dag_id} '
                            'run_id: {self._gapped_dag_run.run_id} '
                            'start_date: {external_task_instance.start_date} '
                            'end_date: {external_task_instance.end_date} '
                            'task_state: {external_task_instance.state} '.format(**locals()))
                    else:
                        is_finished_wait_for_gapped_task = True
                        logging.info(
                            'Finish poking since the gapped task instance has finished: '
                            'dag_id: {self._gapped_dag_run.dag_id} '
                            'run_id: {self._gapped_dag_run.run_id} '
                            'start_date: {external_task_instance.start_date} '
                            'end_date: {external_task_instance.end_date} '
                            'task_state: {external_task_instance.state} '.format(**locals()))
                else:
                    is_finished_wait_for_gapped_task = True
                    logging.info(
                        'Finish poking since the gapped dag run is not running any more: '
                        'dag_id: {self._gapped_dag_run.dag_id} '
                        'run_id: {self._gapped_dag_run.run_id} '
                        'state: {gapped_dag_run_state} '.format(**locals()))
            else:
                logging.info(
                    'Still poking since the root dag is still running and the gapped dag run does not exist: '
                    'dag_id: {self._gapped_root_dag_run.dag_id} '
                    'run_id: {self._gapped_root_dag_run.run_id} '
                    'state: {root_state} '.format(**locals()))
        else:
            logging.info(
                'Finish poking since the root dag is not running any more: '
                'dag_id: {self._gapped_root_dag_run.dag_id} '
                'run_id: {self._gapped_root_dag_run.run_id} '
                'state: {root_state} '.format(**locals()))

        return is_finished_wait_for_gapped_task

    def _init_gapped_root_dag_run(self, execution_date, session):
        execution_date_lt = execution_date - self._execution_delta

        self._gapped_root_dag_run = session.query(DagRun).filter(
            DagRun.dag_id == self._root_external_dag_id,
            DagRun.execution_date < execution_date_lt,
        ).order_by(
            DagRun.execution_date.desc()
        ).first()

    def _refresh_gapped_dag_run(self, session):
        if(self._gapped_dag_run == None):
            self._gapped_dag_run = session.query(DagRun).filter(
                DagRun.dag_id == self._external_dag_id,
                DagRun.execution_date == self._gapped_root_dag_run.execution_date,
            ).first()
        else:
            self._gapped_dag_run.refresh_from_db(session=session)

    def get_external_dag_id(self):
        return self._external_dag_id

    def get_external_task_id(self):
        return self._external_task_id

    def get_execution_delta(self):
        return self._execution_delta
