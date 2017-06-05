from datetime import timedelta

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.fixed_duration_operator import FixedDurationOperator
import logging

JAR_PATH = \
    '/home/presidio/dev-projects/presidio-core/fortscale/target/dependencies/presidio-collector-1.0.0-SNAPSHOT.jar'
MAIN_CLASS = 'presidio.collector.FortscaleCollectorApplication'

jvm_args = {
    'java_overriding_logback_conf_path':
        '/home/presidio/dev-projects/presidio-core/fortscale/presidio-collector/src/main/resources/logback-spring.xml',
    'jar_path': JAR_PATH,
    'main_class': MAIN_CLASS
}


class CollectorDagBuilder(PresidioDagBuilder):
    """
    A "Collector DAG" builder - The "Collector DAG" consists of multiple tasks / operators one per data source.
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

    def build(self, collector_dag):
        """
        Builds jar operators for each data source and adds them to the given DAG.
        :param collector_dag: The DAG to which all relevant "input" operators should be added
        :type collector_dag: airflow.models.DAG
        :return: The input DAG, after the "input" operators were added
        :rtype: airflow.models.DAG
        """

        logging.info("populating the collector dag, dag_id=%s ", collector_dag.dag_id)

        # Iterate all configured data sources
        for data_source in self.data_sources:
            java_args = {
                'data_source': data_source,
            }

            # Create jar operator for each data source
            FixedDurationOperator(
                task_id='collector_{}'.format(data_source),
                fixed_duration_strategy=timedelta(hours=1),
                jvm_args=jvm_args,
                java_args=java_args,
                dag=collector_dag)

        return collector_dag
