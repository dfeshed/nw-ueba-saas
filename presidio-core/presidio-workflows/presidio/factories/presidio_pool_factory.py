from airflow import LoggingMixin
from airflow.api.common.experimental import pool


class PresidioPoolFactory(LoggingMixin):

    def create(self, config_reader):
        """
        :param config_reader: should contain a configuration reader, in order to build pool
        """
        pool_configs = config_reader.read(conf_key='dags.pools_config')
        if pool_configs is not None:
            self.create_spring_boot_jar_pools_for_dag(pool_configs, self.log)

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

