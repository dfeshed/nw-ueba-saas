from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.utils.airflow.operators.jar_operator import JarOperator

JAR_PATH = '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/jars/test.jar'
MAIN_CLASS = 'HelloWorld.Main'

jvm_args = {

    'jar_path': JAR_PATH,
    'main_class': MAIN_CLASS
}

java_args = {}


class OutputDagBuilder(PresidioDagBuilder):
    """
    An "Output DAG" builder - The "Output DAG" consists of multiple tasks / operators one per data source.
    The builder accepts the DAG's attributes through the c'tor, and the "build" method builds and
    returns the DAG according to the given attributes.
    """

    def __init__(self, data_sources):
        """
        C'tor.
        :param data_source: The data source whose events we should work on
        :type data_source: str
        """

        self.data_sources = data_sources

    def build(self, output_dag):
        """
        Builds jar operators for each data source and adds them to the given DAG.
        :param input_dag: The DAG to which all relevant "output" operators should be added
        :type input_dag: airflow.models.DAG
        :return: The input DAG, after the "output" operators were added
        :rtype: airflow.models.DAG
        """

        # Iterate all configured data sources
        for data_source in self.data_sources:
            # Create jar operator for each data source
            JarOperator(
                task_id='output_{}'.format(data_source),
                jvm_args=jvm_args,
                java_args=java_args,
                dag=output_dag)

        return output_dag
