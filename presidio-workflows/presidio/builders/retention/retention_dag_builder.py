from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.builders.retention.input_retention_operator_builder import InputRetentionOperatorBuilder
from presidio.builders.retention.output_retention_operator_builder import OutputRetentionOperatorBuilder
from presidio.operators.retention.ade_manager_operator import AdeManagerOperator


class RetentionDagBuilder(PresidioDagBuilder):
    """
        The "Retention" builder consists of input_retention, ade manager and output_retention
        This dag run once a day.
    """

    def build(self, dag):
        schemas = dag.default_args['schemas']
        self._build_ade_manager_operator(dag)
        for schema in schemas:
            self._build_input_retention(dag, schema)
            self._build_output_retention_operator(dag, [schema])
        return dag

    def _build_output_retention_operator(self, dag, schemas_list):
        """
        Create OutputRetentionOperator in order to output documents after all tasks finished to use it.

        :param dag: The retention DAG
        :type dag: airflow.models.DAG
        :param schemas_list: The schema to process the retention
        :type schemas_list: String
        """
        OutputRetentionOperatorBuilder(schemas_list).build(dag)

    def _build_input_retention(self, dag, schema):
        InputRetentionOperatorBuilder(schema).build(dag)

    def _build_ade_manager_operator(self, dag):
        """
        Create AdeManagerOperator in order to clean enriched data after all enriched data customer tasks finished to use it.

        :param dag: The retention DAG
        :type dag: airflow.models.DAG
        """

        AdeManagerOperator(command=AdeManagerOperator.enriched_ttl_cleanup_command, dag=dag)
