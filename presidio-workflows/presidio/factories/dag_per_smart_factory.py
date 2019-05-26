from __future__ import generators

from abc import abstractmethod

from airflow import DAG
import logging

from presidio.factories.abstract_dag_factory import AbstractDagFactory


class DagPerSmartFactory(AbstractDagFactory):
    """
    DagPerSmartFactory Creates dags per smart e.g (userId_hourly, ja3_hourly etc...)
    """

    def build_dag(self, dag):
        pass

    def create_dags(self, dags_configs, conf_key):
        """
        iterates over smart configurations and initiates dags for them
        """

        logging.debug("creating dynamic dags per smart")
        dag_config = dags_configs.get(conf_key)

        dagrun_timeout, description, end_date, full_filepath, interval, params, start_date, template_searchpath = \
            self._get_dag_params(dag_config, dags_configs)

        dags = []
        smart_confs = dag_config.get("smart_confs")
        for smart_conf in smart_confs:
            new_dag_id = self.get_dag_id(smart_conf.get("smart_conf_name"))
            new_dag = DAG(dag_id=new_dag_id, start_date=start_date, schedule_interval=interval,
                          default_args=self.get_default_args(dags_configs, smart_conf),
                          end_date=end_date, full_filepath=full_filepath, description=description,
                          template_searchpath=template_searchpath, params=params, dagrun_timeout=dagrun_timeout)
            logging.debug("dag_id=%s successful initiated", new_dag_id)
            dags.append(new_dag)

        return dags

    @staticmethod
    @abstractmethod
    def get_dag_id(smart_conf_name):
        pass

    def get_default_args(self, dags_configs, smart_conf):
        return smart_conf
