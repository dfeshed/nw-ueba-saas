from presidio.builders.presidio_dag_builder import PresidioDagBuilder




class RawModelDagBuilder(PresidioDagBuilder):
    """
        A "Raw Model DAG" builder - The "Raw Model DAG" consists of 2 tasks / operators
        (feature aggregation buckets and raw models) and can configured for any data source.
        The builder accepts the data source through the c'tor, and the "build" method builds and
        returns the DAG according to this data source.
        """

    def __init__(self, data_source):
        """
        C'tor.
        :param data_source: The data source whose events should be raw modeled
        :type data_source: str
        """

        self.data_source = data_source

    def build(self, raw_model_dag):
        """
        Builds the "Feature Aggregation Buckets" operator, the "Raw Models" operator and wire the second as downstream of the first.
        :param raw_model_dag: The DAG to which the operator flow should be added.
        :type aggregations_dag: airflow.models.DAG
        :return: The input DAG, after the operator flow was added
        :rtype: airflow.models.DAG
        """


        return raw_model_dag