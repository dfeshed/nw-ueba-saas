from __future__ import generators
from presidio.builders.smart_model.smart_model_dag_builder import SmartModelDagBuilder
from presidio.factories.abstract_dag_factory import DAG_ID_SUFIX
from presidio.factories.dag_per_smart_factory import DagPerSmartFactory


class SmartModelDagFactory(DagPerSmartFactory):
    smart_model_conf_key = "smart_model"

    def build_dag(self, dag):
        SmartModelDagBuilder().build(dag)

    @staticmethod
    def get_dag_id(smart_conf_name):
        return "{0}_{1}_{2}".format(smart_conf_name, "model", DAG_ID_SUFIX)


