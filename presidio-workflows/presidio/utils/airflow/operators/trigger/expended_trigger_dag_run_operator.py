import datetime
import logging
import six
from airflow.operators.dagrun_operator import TriggerDagRunOperator
from sqlalchemy.exc import IntegrityError


class ExpandedTriggerDagRunOperator(TriggerDagRunOperator):

    def execute(self, context):
        self._get_execution_date(context)
        try:
            super(ExpandedTriggerDagRunOperator, self).execute(context)
        except IntegrityError as e:
            # https://issues.apache.org/jira/browse/AIRFLOW-2219
            # https://issues.apache.org/jira/browse/AIRFLOW-1370
            # This occurs because Dag.create_dagrun commits a the dag_run entry to the database and then runs verify_
            # integrity to add the task_instances immediately. However, the scheduler already picks up a dag run before
            # all task_instances are created and also calls verify_integrity to create task_instances at the same time.
            logging.info(
                "Race condition occurs between DagRun.verify_integrity and task_instances.verify_integrity")

    def _get_execution_date(self, context):
        execution_date = context["execution_date"]
        if isinstance(execution_date, datetime.datetime):
            self.execution_date = execution_date.isoformat()
        elif isinstance(execution_date, six.string_types):
            self.execution_date = execution_date
        elif execution_date is None:
            self.execution_date = execution_date
        else:
            raise TypeError(
                'Expected str or datetime.datetime type '
                'for execution_date. Got {}'.format(
                    type(execution_date)))
