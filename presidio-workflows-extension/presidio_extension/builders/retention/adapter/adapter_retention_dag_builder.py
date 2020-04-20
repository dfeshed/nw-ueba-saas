import logging
from presidio.builders.presidio_dag_builder import PresidioDagBuilder


class AdapterRetentionDagBuilder(PresidioDagBuilder):
    def __init__(self, data_sources):
        """
        C'tor.
        :param data_sources: The data source whose events we should work on
        :type data_sources: str
        """
        logging.debug("Running the stub implementation of adapter retention DAG init()")
        pass

    def build(self, retention_dag):
        """
        Builds jar operators that do retention for each data source and adds them to the given DAG.
        :param retention_dag: The DAG to which all relevant retention operators should be added
        :type retention_dag: airflow.models.DAG
        :return: The input DAG, after the retention operators were added
        :rtype: airflow.models.DAG
        """
        logging.debug("Running the stub implementation of adapter retention DAG build()")
        pass
