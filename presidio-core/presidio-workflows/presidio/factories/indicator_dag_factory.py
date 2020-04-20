from __future__ import generators
from presidio.builders.indicator.indicator_dag_builder import IndicatorDagBuilder
from presidio.factories.dag_per_schema_factory import DagPerSchemaFactory
from presidio.utils.decorators.ueba_flow_decorator import ueba_flow_decorator_wrapper


class IndicatorDagFactory(DagPerSchemaFactory):

    indicator_conf_key = "indicator"

    def build_dag(self, dag):
        IndicatorDagBuilder().build(dag)

    @staticmethod
    @ueba_flow_decorator_wrapper
    def get_dag_id(schema):
        return '{0}_{1}'.format(schema, IndicatorDagFactory.indicator_conf_key)

