from __future__ import generators
from presidio.builders.indicator.indicator_dag_builder import IndicatorDagBuilder
from presidio.factories.dag_per_schema_factory import DagPerSchemaFactory


class IndicatorDagFactory(DagPerSchemaFactory):

    indicator_conf_key = "indicator"

    def build_dag(self, dag):
        IndicatorDagBuilder().build(dag)

    @staticmethod
    def get_dag_id(schema):
        return '{0}_{1}'.format(schema, IndicatorDagFactory.indicator_conf_key)

