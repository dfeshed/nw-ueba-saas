import datetime
import logging
from airflow.models import Variable

from presidio.utils.airflow.variable.variable_store import read_from_variable, set_to_variable


def get_schedule_interval(dag):
    """
    find dag schedule_interval.
    if dag was triggered (schedule_interval=None), read schedule_interval from variable.
    :param dag:
    :return: schedule_interval
    """
    # try:
    schedule_interval = dag.schedule_interval
    if schedule_interval is None:
        # interval_in_seconds = Variable.get(key=dag.dag_id, default_var='')
        interval_in_seconds = read_from_variable(key=dag.dag_id, default_var='')
        if interval_in_seconds == '':
            raise ValueError("The schedule_interval was not defined for (%s) dag." % dag.dag_id)
        schedule_interval = datetime.timedelta(seconds=float(interval_in_seconds))
    return schedule_interval


# except Exception:
# logging.debug("Fails on Filling up the DagBag while airflow resetdb, before tables created")


def set_schedule_interval(dag_id, schedule_interval):
    # try:
    interval = read_from_variable(key=dag_id, default_var='')
    if interval == '':
        set_to_variable(dag_id, schedule_interval.total_seconds())
# except Exception:
#     logging.debug("Fails on airflow resetdb, before tables created")
