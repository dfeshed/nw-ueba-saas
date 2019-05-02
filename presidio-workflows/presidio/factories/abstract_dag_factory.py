from __future__ import generators

from abc import abstractmethod

from airflow import LoggingMixin
from airflow.models import Variable
import dateutil

from presidio.factories.dag_factories_exceptions import DagsConfigurationContainsOverlappingDatesException


class AbstractDagFactory(LoggingMixin):

    def create_and_register_dags(self, conf_key, name_space, config_reader):
        """
        Create and register all dags
        :param conf_key: conf_key
        :param name_space:  the global scope which the DAG would be assigned in to. notice: should be the globals()
        :param config_reader: config_reader
        :return: dags
        """
        dags_configs = config_reader.read(conf_key='dags.dags_configs')
        dags = self.create_dags(dags_configs, conf_key)
        self.validate(dags)
        for dag in dags:
            self.register_dag(dag=dag, name_space=name_space, logger=self.log)
        return dags

    def register_dag(self, dag, name_space, logger):
        """
        :param logger: logger
        :param name_space: the global scope which the DAG would be assigned in to. notice: should be the globals()
        method from the file that airflow scheduler scans (at the dag directory)
        :type dag: DAG instance
        :param dag: dag to be registered to airflow
         once DAG is registered -> it can be scheduled and executed
        """
        dag_id = dag.dag_id
        logger.info("register dag_id=%s", dag_id)
        name_space[dag_id] = dag

        dag_ids = self.get_registered_dag_ids()

        if dag_id not in dag_ids:
            dag_ids.append(dag_id)
            Variable.set(key="dags", value=(', '.join(str(e) for e in dag_ids)))

    def build_dags(self, dags):
        for dag in dags:
            self.build_dag(dag)

    @staticmethod
    def get_registered_dag_ids():
        dag_ids = Variable.get(key="dags", default_var=[])
        if dag_ids:
            dag_ids = str(dag_ids).split(", ")
        return dag_ids

    @abstractmethod
    def build_dag(self, dag):
        pass

    @abstractmethod
    def create_dags(self, dags_configs, conf_key):
        pass

    def validate(self, created_dags):
        """
        validates that all created dags have start and end time that does not overlap
        :param created_dags: array of airflow dags
        """
        if not created_dags:
            # empty list, nothing to validate
            pass

        created_dags.sort(key=lambda x: x.start_date)
        last_start_date = None
        last_end_date = None
        last_dag_id = None
        for dag in created_dags:
            if last_start_date is not None and last_end_date is not None:
                if dag.start_date <= last_end_date:
                    raise DagsConfigurationContainsOverlappingDatesException(dag.dag_id, last_dag_id)
            last_start_date = dag.start_date
            last_end_date = dag.end_date
            last_dag_id = dag.dag_id

    def _get_dag_params(self, dag_config, dags_configs):
        temp_interval = dag_config.get("schedule_interval")
        if temp_interval.startswith("timedelta") or temp_interval == "None":
            from datetime import timedelta
            interval = eval(temp_interval)
        else:
            interval = temp_interval
        start_date = dateutil.parser.parse(dags_configs.get("start_date"), ignoretz=True)
        if dags_configs.get("end_date"):
            end_date = dateutil.parser.parse(dags_configs.get("end_date"), ignoretz=True)
        else:
            end_date = None
        full_filepath = dag_config.get("full_filepath")
        description = dag_config.get("description")
        template_searchpath = dag_config.get("template_searchpath")
        params = dag_config.get("params")
        dagrun_timeout = dag_config.get("dagrun_timeout")
        return dagrun_timeout, description, end_date, full_filepath, interval, params, start_date, template_searchpath
