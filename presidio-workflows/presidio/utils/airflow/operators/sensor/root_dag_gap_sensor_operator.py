import logging

from airflow.models import DagRun
from airflow.operators.sensors import BaseSensorOperator
from airflow.utils.db import provide_session
from airflow.utils.state import State
from datetime import timedelta


class RootDagGapSensorOperator(BaseSensorOperator):
    """
    Sensor all root dag instances with the specified dag_id (or that contain the sub dag id) until all root dag instances with execution_date that has greater
    gap than the given delta time finished running (reached to one of the following states: success, failed)

    :param external_dag_id: The dag_id that you want to wait for
    :type external_dag_id: string
    :param execution_delta: the minimum time difference of all dag instances that should be sensored.
    :type execution_delta: datetime.timedelta
    """
    ui_color = '#19647e'
    ui_fgcolor = '#fff'

    def __init__(
            self,
            external_dag_id,
            execution_delta,
            *args, **kwargs):
        super(RootDagGapSensorOperator, self).__init__(
            retries=99999,
            retry_exponential_backoff=True,
            max_retry_delay=timedelta(seconds=300),
            retry_delay=timedelta(seconds=5),
            *args,
            **kwargs
        )

        self._execution_delta = execution_delta
        self._root_external_dag_id = external_dag_id.split(".", 1)[0]

    def poke(self, context):
        '''

        @return: bool - whether there are tasks to wait for.
        '''

        execution_date = context['execution_date']
        execution_date_lt = execution_date - self._execution_delta

        logging.info(
            'Poking for all dag instances of'
            '{self._root_external_dag_id} with time lt'
            'execution_date_lt ... '.format(**locals()))

        return self._is_finished_wait_for_gapped_dag(execution_date_lt)

    @provide_session
    def _is_finished_wait_for_gapped_dag(self, execution_date_lt,session=None):

        gapped_root_dag_run = session.query(DagRun).filter(
            DagRun.dag_id == self._root_external_dag_id,
            DagRun.execution_date < execution_date_lt,
            DagRun.state == State.RUNNING,
        ).order_by(
            DagRun.execution_date.asc()
        ).first()

        return gapped_root_dag_run == None