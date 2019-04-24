import datetime

from airflow.models import Variable


def get_schedule_interval(dag):
    """
    find dag schedule_interval.
    if dag was triggered (schedule_interval=None), read schedule_interval from variable.
    :param dag:
    :return: schedule_interval
    """
    schedule_interval = dag.schedule_interval
    if schedule_interval is None:
        interval_in_seconds = Variable.get(key=dag.dag_id, default_var='')
        if interval_in_seconds == '':
            raise ValueError("The schedule_interval was not defined for (%s) dag." % dag.dag_id)
        schedule_interval = datetime.timedelta(seconds=float(interval_in_seconds))
    return schedule_interval


def set_schedule_interval(dag_id, schedule_interval):
    interval = Variable.get(key=dag_id, default_var='')
    if interval == '':
        Variable.set(dag_id, schedule_interval.total_seconds())

