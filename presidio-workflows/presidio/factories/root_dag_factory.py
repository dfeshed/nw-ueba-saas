from __future__ import generators
from airflow import DAG

from presidio.builders.root.root_dag_builder import RootDagBuilder
import logging

from presidio.factories.abstract_dag_factory import AbstractDagFactory


class RootDagFactory(AbstractDagFactory):

    root_conf_key = "root"

    def build_dag(self, dag):
        RootDagBuilder().build(dag)

    def create_dags(self, dags_configs, conf_key):
        """
        create root dag
        """
        logging.debug("creating root dynamic dag")
        dag_config = dags_configs.get(conf_key)

        dagrun_timeout, description, end_date, full_filepath, interval, params, start_date, template_searchpath = \
            self._get_dag_params(dag_config, dags_configs)

        dag_id_start_date = str(start_date).replace(" ", "_").replace(":", "_")
        schemas = [item.strip() for item in dags_configs.get("data_schemas").split(',')]
        new_dag_id = "{0}_{1}".format("ueba", dag_id_start_date)
        new_dag = DAG(dag_id=new_dag_id, start_date=start_date, schedule_interval=interval,
                      default_args={"schemas": schemas},
                      end_date=end_date, full_filepath=full_filepath, description=description,
                      template_searchpath=template_searchpath, params=params, dagrun_timeout=dagrun_timeout,
                      max_active_runs=1)
        logging.debug("dag_id=%s successful initiated", new_dag_id)

        return [new_dag]

