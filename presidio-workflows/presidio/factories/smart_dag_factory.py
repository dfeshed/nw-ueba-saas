from __future__ import generators
from presidio.builders.smart.smart_dag_builder import SmartDagBuilder
from presidio.factories.dag_per_smart_factory import DagPerSmartFactory


class SmartDagFactory(DagPerSmartFactory):
    smart_conf_key = "smart"

    def build_dag(self, dag):
        SmartDagBuilder().build(dag)

    @staticmethod
    def get_dag_id(smart_conf_name):
        return smart_conf_name


