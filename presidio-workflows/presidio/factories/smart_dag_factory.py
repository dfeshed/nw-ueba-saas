from __future__ import generators
from presidio.builders.smart.smart_dag_builder import SmartDagBuilder
from presidio.factories.dag_per_smart_factory import DagPerSmartFactory
from presidio.utils.decorators.ueba_flow_decorator import ueba_flow_decorator_wrapper


class SmartDagFactory(DagPerSmartFactory):
    smart_conf_key = "smart"

    def build_dag(self, dag):
        SmartDagBuilder().build(dag)

    @staticmethod
    @ueba_flow_decorator_wrapper
    def get_dag_id(smart_conf_name):
        return "{0}".format(smart_conf_name)

    def get_default_args(self, dags_configs, smart_conf):
        schemas = [item.strip() for item in dags_configs.get("data_schemas").split(',')]
        smart_conf['depends_on_schemas'] = set(schemas) & set(smart_conf.get('depends_on_schemas'))
        return smart_conf


