from datetime import timedelta

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.builders.retention.input_retention_operator_builder import InputRetentionOperatorBuilder
from presidio.builders.retention.output_retention_operator_builder import OutputRetentionOperatorBuilder
from presidio.operators.retention.ade_manager_operator import AdeManagerOperator


class RetentionDagBuilder(PresidioDagBuilder):

    min_time_to_start_retention_in_days_conf_key = "retention.min_time_to_start_retention_in_days"
    min_time_to_start_retention_in_days_default_value = 2
    retention_interval_in_hours_conf_key = "retention.retention_interval_in_hours"
    retention_interval_in_hours_default_value = 24

    """
        The "Retention" builder consists of input_retention, ade manager and output_retention
        This dag run once a day.
    """

    def build(self, dag):
        schemas = dag.default_args['schemas']
        self._build_ade_manager_operator(dag)
        for schema in schemas:
            self._build_input_retention(dag, schema)
            self._build_output_retention_operator(dag, schema)
        return dag

    def _build_output_retention_operator(self, dag, schema):
        """
        Create OutputRetentionOperator in order to output documents after all tasks finished to use it.

        :param dag: The retention DAG
        :type dag: airflow.models.DAG
        :param schema: The schema to process the retention
        :type schema: String
        """
        OutputRetentionOperatorBuilder(schema).build(dag)

    def _build_input_retention(self, dag, schema):
        """
        Create InputRetentionOperatorBuilder in order to clean input data after all input data customer tasks finished
        to use it.
        :param dag: The retention DAG
        :param schema: The schema to process the retention
        :type schema: String
        """
        InputRetentionOperatorBuilder(schema).build(dag)

    def _build_ade_manager_operator(self, dag):
        """
        Create AdeManagerOperator in order to clean enriched data after all enriched data customer tasks finished
        to use it.
        :param dag: The retention DAG
        :type dag: airflow.models.DAG
        """

        AdeManagerOperator(dag=dag)

    @staticmethod
    def get_min_time_to_start_retention_in_days(conf_reader):
        return conf_reader.read(
            RetentionDagBuilder.min_time_to_start_retention_in_days_conf_key,
            RetentionDagBuilder.min_time_to_start_retention_in_days_default_value)

    @staticmethod
    def get_retention_interval_in_hours(conf_reader):
        return timedelta(hours=conf_reader.read(RetentionDagBuilder.retention_interval_in_hours_conf_key,
                                                RetentionDagBuilder.retention_interval_in_hours_default_value))

