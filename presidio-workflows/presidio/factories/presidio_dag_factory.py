import dateutil.parser
from airflow import DAG
from airflow.api.common.experimental import pool

from presidio.factories.abstract_dag_factory import AbstractDagFactory
from presidio.factories.dag_factories_exceptions import DagsConfigurationContainsOverlappingDatesException

SPRING_BOOT_JAR_POOL_NAME = "spring_boot_jar_pool"
SPRING_BOOT_JAR_POOL_NUM_OF_SLOTS = 10
SPRING_BOOT_JAR_POOL_DESCRIPTION = "A pool for the spring boot jars that belong to the dag"


class PresidioDagFactory(AbstractDagFactory):
    def get_id(self):
        return "PresidioDag"

    def create(self, **dag_params):
        """
        :param dag_params: should contain a configuration reader, in order to build our dags by it
        :return: list of created dags
        """
        configuration_reader = dag_params.get('conf_reader')
        dags_configs = configuration_reader.read(conf_key='dags.dags_configs')
        self.log.debug("creating dynamic dags")
        created_dags = self.create_dags(dags_configs=dags_configs, logger=self.log)
        return created_dags

    @staticmethod
    def create_dags(dags_configs, logger):
        """
        iterates over dags configurations and initiates dags for them
        """
        dags = []
        for dag_config in dags_configs:
            args = dag_config.get("args")
            new_dag_id = dag_config.get("dag_id")
            temp_interval = dag_config.get("schedule_interval")
            if temp_interval.startswith("timedelta"):
                from datetime import timedelta
                interval = eval(temp_interval)
            else:
                interval = temp_interval
            start_date = dateutil.parser.parse(dag_config.get("start_date"), ignoretz=True)
            if (dag_config.get("end_date")):
                end_date = dateutil.parser.parse(dag_config.get("end_date"), ignoretz=True)
            else:
                end_date = None
            full_filepath = dag_config.get("full_filepath")
            description = dag_config.get("description")
            template_searchpath = dag_config.get("template_searchpath")
            params = dag_config.get("params")
            dagrun_timeout = dag_config.get("dagrun_timeout")
            dag_id_start_date = str(start_date).replace(" ","_").replace(":","_")
            new_dag_id = "{0}_{1}".format(dag_config.get("dag_id"),dag_id_start_date)
            PresidioDagFactory.create_spring_boot_jar_pool_for_dag()
            if args.get("data_sources"):
                new_dag = DAG(dag_id=new_dag_id, start_date=start_date, schedule_interval=interval, default_args=args,
                              end_date=end_date, full_filepath=full_filepath, description=description,
                              template_searchpath=template_searchpath, params=params, dagrun_timeout=dagrun_timeout)
                logger.debug("dag_id=%s successful initiated", new_dag_id)
                dags.append(new_dag)

        return dags

    @staticmethod
    def create_spring_boot_jar_pool_for_dag():
        pool_name = PresidioDagFactory.get_spring_boot_jar_pool_name_for_dag()
        is_pool_exist = len([p for p in pool.get_pools() if p.pool == pool_name]) == 1

        if not is_pool_exist:
            pool.create_pool(name=pool_name,
                             slots=SPRING_BOOT_JAR_POOL_NUM_OF_SLOTS,
                             description=SPRING_BOOT_JAR_POOL_DESCRIPTION)

    @staticmethod
    def get_spring_boot_jar_pool_name_for_dag():
        return SPRING_BOOT_JAR_POOL_NAME

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