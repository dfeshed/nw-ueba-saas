from __future__ import generators
from airflow import DAG
import logging

from presidio.builders.retention.retention_dag_builder import RetentionDagBuilder
from presidio.factories.abstract_dag_factory import AbstractDagFactory
from presidio.utils.decorators.ueba_flow_decorator import ueba_flow_decorator_wrapper


class RetentionDagFactory(AbstractDagFactory):
    retention_conf_key = "retention"

    def build_dag(self, dag):
        RetentionDagBuilder().build(dag)

    def create_dags(self, dags_configs, conf_key):
        """
        create retention dag
        """
        logging.debug("creating retention dynamic dag")
        dag_config = dags_configs.get(conf_key)

        dagrun_timeout, description, end_date, full_filepath, interval, params, start_date, template_searchpath = \
            self._get_dag_params(dag_config, dags_configs)

        new_dag_id = self.get_dag_id()
        schemas = [item.strip() for item in dags_configs.get("data_schemas").split(',')]
        new_dag = DAG(dag_id=new_dag_id, start_date=start_date, schedule_interval=interval,
                      default_args={"schemas": schemas},
                      end_date=end_date, full_filepath=full_filepath, description=description,
                      template_searchpath=template_searchpath, params=params, dagrun_timeout=dagrun_timeout)
        logging.debug("dag_id=%s successful initiated", new_dag_id)

        return [new_dag]

    @staticmethod
    @ueba_flow_decorator_wrapper
    def get_dag_id():
        return "retention"

