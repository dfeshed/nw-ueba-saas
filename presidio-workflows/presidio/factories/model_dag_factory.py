from __future__ import generators

from presidio.builders.model.model_dag_builder import ModelDagBuilder
from presidio.factories.dag_per_schema_factory import DagPerSchemaFactory


class ModelDagFactory(DagPerSchemaFactory):
    model_conf_key = "model"

    def build_dag(self, dag):
        ModelDagBuilder().build(dag)

    @staticmethod
    def get_dag_id(schema):
        return '{0}_{1}'.format(schema, ModelDagFactory.model_conf_key)

