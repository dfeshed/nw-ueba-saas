import datetime

import six
from airflow.operators.dagrun_operator import TriggerDagRunOperator


class ExpandedTriggerDagRunOperator(TriggerDagRunOperator):


    def execute(self, context):
        self._get_execution_date(context)
        super(ExpandedTriggerDagRunOperator, self).execute(context)

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
