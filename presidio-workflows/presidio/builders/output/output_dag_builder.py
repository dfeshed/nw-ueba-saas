import logging

from datetime import timedelta

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator

JAR_PATH = \
    '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/jars/test-mock-project-0.0.1-SNAPSHOT.jar'
MAIN_CLASS = 'com.fortscale.test.TestMockProjectApplication'

jvm_args = {
    'jar_path': JAR_PATH,
    'main_class': MAIN_CLASS
}


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
        :param output_dag: The DAG to which all relevant "output" operators should be added
        :type output_dag: airflow.models.DAG
        :return: The input DAG, after the "output" operators were added
        :rtype: airflow.models.DAG
        """

        logging.info("populating the output dag, dag_id=%s ", output_dag.dag_id)

        # Iterate all configured data sources
        for data_source in self.data_sources:
            java_args = {
                '--data_source': data_source,
            }

            # Create jar operator for each data source
            FixedDurationJarOperator(
                task_id='output_{}'.format(data_source),
                fixed_duration_strategy=timedelta(hours=1),
                jvm_args=jvm_args,
                java_args=java_args,
                dag=output_dag)

        return output_dag
