import logging

from datetime import timedelta

import sys

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.connector.sensor.hour_is_ready_sensor_operator import HourIsReadySensorOperator
from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton

ADAPTER_JVM_ARGS_CONFIG_PATH = 'components.adapter.jvm_args'


class AdapterDagBuilder(PresidioDagBuilder):
    """
    A "Adapter DAG" builder - The "Adapter DAG" consists of multiple tasks / operators one per data source.
    The builder accepts the DAG's attributes through the c'tor, and the "build" method builds and
    returns the DAG according to the given attributes.
    """

    conf_reader = ConfigServerConfigurationReaderSingleton().config_reader

    def __init__(self, data_sources):
        """
        C'tor.
        :param data_source: The data source whose events we should work on
        :type data_source: str
        """

        self.data_sources = data_sources
        self.jvm_args = AdapterDagBuilder.conf_reader.read(conf_key=ADAPTER_JVM_ARGS_CONFIG_PATH)

    def build(self, adapter_dag):
        """
        Builds jar operators for each data source and adds them to the given DAG.
        :param adapter_dag: The DAG to which all relevant "input" operators should be added
        :type adapter_dag: airflow.models.DAG
        :return: The input DAG, after the "input" operators were added
        :rtype: airflow.models.DAG
        """

        logging.info("populating the adapter dag, dag_id=%s ", adapter_dag.dag_id)

        if 'FLUME_HOME' not in os.environ:
            msg = "Adapter DAG build has failed. Environment variable FLUME_HOME doesn't exist"
            logging.error("%s. exiting..." % msg)
            sys.exit(msg)

        flume_home = os.environ.get("FLUME_HOME")

        # Iterate all configured data sources
        for data_source in self.data_sources:
            java_args = {
                'schema': data_source,
            }

            hour_is_ready_sensor = HourIsReadySensorOperator(dag=adapter_dag,
                                                             task_id='adapter_sensor_{}'.format(data_source),
                                                             poke_interval=60,  # 1 minute
                                                             timeout=60*60*24*7,  # 1 week
                                                             schema_name=data_source)

            # Create jar operator for each data source
            jar_operator = FixedDurationJarOperator(
                task_id='adapter_{}'.format(data_source),
                fixed_duration_strategy=timedelta(hours=1),
                command=PresidioDagBuilder.presidio_command,
                jvm_args=self.jvm_args,
                java_args=java_args,
                dag=adapter_dag)

            hour_is_ready_sensor >> jar_operator

        return adapter_dag
