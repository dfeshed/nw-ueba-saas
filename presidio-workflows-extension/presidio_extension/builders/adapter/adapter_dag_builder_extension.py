ADAPTER_JVM_ARGS_CONFIG_PATH = 'components.adapter.jvm_args'


class AdapterDagBuilderExtension:
    """
    A "Adapter DAG" builder - The "Adapter DAG" consists of multiple tasks / operators one per data source.
    The builder accepts the DAG's attributes through the c'tor, and the "build" method builds and
    returns the DAG according to the given attributes.
    """

    def __init__(self):
        pass

    def build(self, adapter_dag, data_source, jar_operator):
        """
        Builds jar operators for each data source and adds them to the given DAG.
        :param data_source:
        :param jar_operator:
        :param adapter_dag: The DAG to which all relevant "input" operators should be added
        :type adapter_dag: airflow.models.DAG
        :return: The input DAG, after the "input" operators were added
        :rtype: airflow.models.DAG
        """
        pass
