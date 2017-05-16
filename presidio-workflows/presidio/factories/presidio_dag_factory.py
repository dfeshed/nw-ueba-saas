import dateutil.parser
import logging
from airflow import DAG

from presidio.factories.abstract_dag_factory import AbstractDagFactory
from presidio.factories.dag_factories_exceptions import DagsConfigurationContainsOverlappingDates


class PresidioDag:
    class Factory(AbstractDagFactory):

        def create(self, **dag_params):
            """
            :param dag_params: should contain a configuration reader, in order to build our dags by it 
            :return: list of created dags 
            """
            configuration_reader = dag_params.get('conf_reader')
            dags_configs = configuration_reader.read(conf_key='dags_configs')
            logging.info("creating dynamic dags")
            created_dags = self.create_dags(dags_configs=dags_configs)
            self.validate(created_dags)
            return created_dags

        @staticmethod
        def create_dags(dags_configs):
            """
            iterates over dags configurations and initiates dags for them
            """
            dags = []
            for dag_config in dags_configs:
                args = dag_config.get("args")
                new_dag_id = dag_config.get("dag_id")
                interval = dag_config.get("schedule_interval")
                start_date = dateutil.parser.parse(dag_config.get("start_date"))
                end_date = dateutil.parser.parse(dag_config.get("end_date"))
                full_filepath = dag_config.get("full_filepath")
                description = dag_config.get("description")
                template_searchpath = dag_config.get("template_searchpath")
                params = dag_config.get("params")
                dagrun_timeout = dag_config.get("dagrun_timeout")

                new_dag = DAG(dag_id=new_dag_id, start_date=start_date, schedule_interval=interval, default_args=args,
                              end_date=end_date, full_filepath=full_filepath, description=description,
                              template_searchpath=template_searchpath, params=params, dagrun_timeout=dagrun_timeout)
                logging.info("dag_id=%s successful initiated",new_dag_id)
                dags.append(new_dag)

            return dags

        @staticmethod
        def validate(created_dags):
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
                if last_start_date is None:
                    last_start_date = dag.start_date
                    last_end_date = dag.end_date
                    last_dag_id = dag.dag_id
                    continue
                if dag.start_date < last_end_date:
                    raise DagsConfigurationContainsOverlappingDates(dag.dag_id, last_dag_id)
