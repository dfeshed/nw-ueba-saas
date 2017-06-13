from presidio.builders.presidio_dag_builder import PresidioDagBuilder


class SmartModelDagBuilder(PresidioDagBuilder):
    """
        A "Smart Model DAG" builder - 
        The "Smart Model DAG" consists of smart accumulating operator followed by smart model build operator
        The smart accumulating operator responsible for accumulating the smart events
        The smart model build operator is respobsible for building the models
        Accumulating the data will happen once a day whereas the models might be built once a day or less (i.e. once a week)

        There are 2 parameters that define the aggregation events to be built, accumulated and modeled:
        - smart_events_conf - The name of the configuration defining the smart events
        - build_model_interval - The interval that new models should be calculated.

        returns the DAG according to the given data source and fixed duration strategy
        """

    def __init__(self, smart_events_conf, build_model_interval):
        """
        C'tor.
        :param smart_events_conf: The name of the configuration defining the smart events.
        :type smart_events_conf: str
        :param build_model_interval: The interval that new models should be calculated.
        :type build_model_interval: datetime.timedelta
        """

        self._smart_events_conf = smart_events_conf
        self._build_model_interval = build_model_interval

    def build(self, smart_model_dag):
        """
        Fill the given "Smart Model DAG" with smart accumulating operator followed by smart model build operator
        The smart accumulating operator responsible for accumulating the smart events
        The smart model build operator is respobsible for building the models
        Accumulating the data will happen once a day whereas the models might be built once a day or less (i.e. once a week)

        :param smart_model_dag: The DAG to which the operator flow should be added.
        :type smart_model_dag: airflow.models.DAG
        :return: The input DAG, after the operator flow was added
        :rtype: airflow.models.DAG
        """

        return smart_model_dag