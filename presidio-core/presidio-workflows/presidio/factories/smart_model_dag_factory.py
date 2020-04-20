from __future__ import generators
from presidio.builders.smart_model.smart_model_dag_builder import SmartModelDagBuilder
from presidio.factories.dag_per_smart_factory import DagPerSmartFactory
from presidio.utils.decorators.ueba_flow_decorator import ueba_flow_decorator_wrapper


class SmartModelDagFactory(DagPerSmartFactory):
    smart_model_conf_key = "smart_model"

    def build_dag(self, dag):
        SmartModelDagBuilder().build(dag)

    @staticmethod
    @ueba_flow_decorator_wrapper
    def get_dag_id(smart_conf_name):
        return "{0}_{1}".format(smart_conf_name, "model")


