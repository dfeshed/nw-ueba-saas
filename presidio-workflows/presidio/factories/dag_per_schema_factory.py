from __future__ import generators

from abc import abstractmethod

from airflow import DAG
import logging

from presidio.factories.abstract_dag_factory import AbstractDagFactory


class DagPerSchemaFactory(AbstractDagFactory):
    """
    DagPerSchemaFactory Creates dags per schema e.g (AUTHENTICATION_indicator, FILE_indicator, ACTIVE_DIRECTORY_indicator etc...)
    """

    @abstractmethod
    def build_dag(self, dag):
        pass

    def create_dags(self, dags_configs, conf_key):
        """
        iterates over schemas and initiates dags for them
        """

        logging.debug("creating dynamic dags per schemas")
        dag_config = dags_configs.get(conf_key)
        dagrun_timeout, description, end_date, full_filepath, interval, params, start_date, template_searchpath = \
            self._get_dag_params(dag_config, dags_configs)

        dags = []
        schemas = [item.strip() for item in dags_configs.get("data_schemas").split(',')]
        for schema in schemas:
            new_dag_id = self.get_dag_id(schema)
            new_dag = DAG(dag_id=new_dag_id, start_date=start_date, schedule_interval=interval,
                          default_args={"schema": schema},
                          end_date=end_date, full_filepath=full_filepath, description=description,
                          template_searchpath=template_searchpath, params=params, dagrun_timeout=dagrun_timeout)
            logging.debug("dag_id=%s successful initiated", new_dag_id)
            dags.append(new_dag)

        return dags

    @staticmethod
    @abstractmethod
    def get_dag_id(schema):
        pass
