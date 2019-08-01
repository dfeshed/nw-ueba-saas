from __future__ import generators
from airflow import DAG
import logging

from presidio.builders.input.input_pre_processor_dag_builder import InputPreProcessorDagBuilder
from presidio.factories.abstract_dag_factory import AbstractDagFactory
from presidio.utils.decorators.ueba_flow_decorator import ueba_flow_decorator_wrapper


class InputPreProcessorDagFactory(AbstractDagFactory):
    input_pre_processor_conf_key = "input_pre_processing"

    def build_dag(self, dag):
        InputPreProcessorDagBuilder().build(dag)

    def create_dags(self, dags_configs, conf_key):
        """
        create input pre processor dag
        """
        logging.debug("creating input pre processor dag")
        dag_config = dags_configs.get(conf_key)

        dagrun_timeout, description, end_date, full_filepath, interval, params, start_date, template_searchpath = \
            self._get_dag_params(dag_config, dags_configs)

        dags = []
        schemas = dag_config.get("schemas")
        for schema in schemas:
            new_dag_id = self.get_dag_id(schema.get("schema_name"))
            new_dag = DAG(dag_id=new_dag_id, start_date=start_date, schedule_interval=interval,
                          default_args=schema,
                          end_date=end_date, full_filepath=full_filepath, description=description,
                          template_searchpath=template_searchpath, params=params, dagrun_timeout=dagrun_timeout)
            logging.debug("dag_id=%s successful initiated", new_dag_id)
            dags.append(new_dag)

        return dags

    @staticmethod
    @ueba_flow_decorator_wrapper
    def get_dag_id(schema):
        return "input_pre_processing_{0}".format(schema)

