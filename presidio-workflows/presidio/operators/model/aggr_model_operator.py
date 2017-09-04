from datetime import timedelta
from airflow.utils.decorators import apply_defaults
from presidio.utils.exceptions import UnsupportedFixedDurationStrategyError
from presidio.operators.model.model_operator import ModelOperator


AGGR_MODEL_GROUP_NAME = "feature-aggregation-record-models"

class AggrModelOperator(ModelOperator):
    """
    Runs a "Raw Model" task (JAR). 
    the jar build the raw model after the feature buckets have been updated.
    """

    # Color configurations for the Airflow UI
    ui_color = '#1abc9c'
    ui_fgcolor = '#000000'

    def __init__(self, command, data_source, session_id, task_id=None, *args, **kwargs):
        """
        C'tor.
        :param data_source: The data source whose models are going to be built
        :type data_source: string
        :param task_id: The task ID of this operator - If None, the ID is generated automatically
        :type task_id: string
        """

        self.data_source = data_source

        super(AggrModelOperator, self).__init__(command=command,session_id=session_id, group_name=self.get_group_name(),task_id=task_id,*args,**kwargs)

    def get_group_name(self):
        return '{}.{}'.format(AGGR_MODEL_GROUP_NAME, self.data_source)

    def get_task_id(self):
        """
        :return: The task id 
        """
        return '{}_{}'.format(self.data_source, 'aggr_model')
