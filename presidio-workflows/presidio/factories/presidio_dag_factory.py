import dateutil.parser
from airflow import DAG
from airflow.api.common.experimental import pool

from presidio.factories.abstract_dag_factory import AbstractDagFactory
from presidio.factories.dag_factories_exceptions import DagsConfigurationContainsOverlappingDatesException


class PresidioDagFactory(AbstractDagFactory):
    def get_id(self):
        return "PresidioDag"

    def create(self, **dag_params):
        """
        :param dag_params: should contain a configuration reader, in order to build our dags by it
        :return: list of created dags
        """
        configuration_reader = dag_params.get('conf_reader')

        pool_configs = configuration_reader.read(conf_key='dags.pools_config')
        if pool_configs is not None:
            self.create_spring_boot_jar_pools_for_dag(pool_configs, self.log)

        dags_configs = configuration_reader.read(conf_key='dags.dags_configs')
        created_dags = self.create_dags(dags_configs, self.log)
        return created_dags

    @staticmethod
    def create_spring_boot_jar_pools_for_dag(pool_configs, logger):
        """
        iterates over pools configurations and initiates pools for them
        in the future we might want to add the pools per dag
        """
        logger.debug("creating pools")
        for pool_config in pool_configs:
            pool_name = pool_config.get("name")

            if not any(p.pool == pool_name for p in pool.get_pools()):
                pool.create_pool(name=pool_name,
                                 slots=pool_config.get("slots"),
                                 description=pool_config.get("description"))
                logger.debug("pool_name=%s successfully initiated", pool_name)
            else:
                logger.debug("pool_name=%s already exist", pool_name)

    @staticmethod
    def create_dags(dags_configs, logger):
        """
        iterates over dags configurations and initiates dags for them
        """
        logger.debug("creating dynamic dags")
        dags = []
        for dag_config in dags_configs:
            args = dag_config.get("args")
            temp_interval = dag_config.get("schedule_interval")
            if temp_interval.startswith("timedelta"):
                interval = eval(temp_interval)
            else:
                interval = temp_interval
            start_date = dateutil.parser.parse(dag_config.get("start_date"), ignoretz=True)
            if dag_config.get("end_date"):
                end_date = dateutil.parser.parse(dag_config.get("end_date"), ignoretz=True)
            else:
                end_date = None
            full_filepath = dag_config.get("full_filepath")
            description = dag_config.get("description")
            template_searchpath = dag_config.get("template_searchpath")
            params = dag_config.get("params")
            dagrun_timeout = dag_config.get("dagrun_timeout")
            dag_id_start_date = str(start_date).replace(" ","_").replace(":","_")
            new_dag_id = "{0}_{1}".format(dag_config.get("dag_id"), dag_id_start_date)
            if args.get("data_sources"):
                new_dag = DAG(dag_id=new_dag_id, start_date=start_date, schedule_interval=interval, default_args=args,
                              end_date=end_date, full_filepath=full_filepath, description=description,
                              template_searchpath=template_searchpath, params=params, dagrun_timeout=dagrun_timeout)
                logger.debug("dag_id=%s successful initiated", new_dag_id)
                dags.append(new_dag)

        return dags

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
            if last_start_date is not None:
                if dag.start_date <= last_end_date:
                    raise DagsConfigurationContainsOverlappingDatesException(dag.dag_id, last_dag_id)
            last_start_date = dag.start_date
            last_end_date = dag.end_date
            last_dag_id = dag.dag_id