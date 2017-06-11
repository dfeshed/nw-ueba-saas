from datetime import timedelta
from airflow.utils.decorators import apply_defaults
from presidio.utils.exceptions import UnsupportedFixedDurationStrategyError
from presidio.operators.model.model_operator import ModelOperator


class RawModelOperator(ModelOperator):
    """
    Runs a "Raw Model" task (JAR). 
    the jar build the raw model after the feature buckets have been updated.
    """

    # Color configurations for the Airflow UI
    ui_color = '#1abc9c'
    ui_fgcolor = '#000000'

    @apply_defaults
    def __init__(self, data_source, task_id=None, *args, **kwargs):
        """
        C'tor.
        :param data_source: The data source whose models are going to be built
        :type data_source: string
        :param task_id: The task ID of this operator - If None, the ID is generated automatically
        :type task_id: string
        """

        self.data_source = data_source

        super(RawModelOperator, self).__init__(task_id,*args,**kwargs)


    def get_task_id(self):
        """
        :return: The task id 
        """
        return '{}_{}'.format(self.data_source, 'raw_model')

    def get_java_args(self):
        """
        :return: The java args
        """
        java_args = {
            'data_source': self.data_source,
        }
        return java_args


    def get_jar_file_path(self):
        """
        :return: The full path to the JAR file
        """

        return '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/jars/test-mock-project-0.0.1-SNAPSHOT.jar'

    def get_main_class(self):
        """
       :return: The main class name of JAR file
       """

        return 'com.fortscale.test.TestMockProjectApplication'
