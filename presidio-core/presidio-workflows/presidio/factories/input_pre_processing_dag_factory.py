from __future__ import generators
from airflow import DAG
import logging

from presidio.builders.input.input_pre_processing_dag_builder import InputPreProcessingDagBuilder
from presidio.factories.abstract_dag_factory import AbstractDagFactory
from presidio.utils.decorators.ueba_flow_decorator import ueba_flow_decorator_wrapper


SCHEMAS_KEY = "schemas"
SCHEMA_NAME_KEY = "name"
SCHEMAS_REGISTERED = []


class InputPreProcessingDagFactory(AbstractDagFactory):
    INPUT_PRE_PROCESSING_CONF_KEY = "input_pre_processing"

    def build_dag(self, dag):
        InputPreProcessingDagBuilder().build(dag)

    def create_dags(self, dags_configs, conf_key):
        """
        Create input pre-processing DAG.
        """
        logging.debug("Create input pre-processing DAG.")
        dag_config = dags_configs.get(conf_key)

        dagrun_timeout, description, end_date, full_filepath, interval, params, start_date, template_searchpath = \
            self._get_dag_params(dag_config, dags_configs)

        dags = []
        schemas = dag_config.get(SCHEMAS_KEY)
        if schemas == None:
            return dags
        
        for schema in schemas:
            schema_name = schema.get(SCHEMA_NAME_KEY)
            if schema_name not in SCHEMAS_REGISTERED:
                SCHEMAS_REGISTERED.append(schema_name)
            new_dag_id = self.get_dag_id(schema_name)
            new_dag = DAG(dag_id=new_dag_id,
                          start_date=start_date,
                          schedule_interval=interval,
                          default_args=schema,
                          end_date=end_date,
                          full_filepath=full_filepath,
                          description=description,
                          template_searchpath=template_searchpath,
                          params=params,
                          dagrun_timeout=dagrun_timeout)
            logging.debug("dag_id=%s successful initiated", new_dag_id)
            dags.append(new_dag)

        return dags

    @staticmethod
    def get_registered_schemas():
        return SCHEMAS_REGISTERED

    @staticmethod
    @ueba_flow_decorator_wrapper
    def get_dag_id(schema):
        return "input_pre_processing_{0}".format(schema)

