import dateutil.parser
from airflow import DAG
from airflow.operators.dummy_operator import DummyOperator

from presidio.factories.abstract_dag_factory import AbstractDagFactory


class AbstractDynamicDag(object): pass


class PresidioDag(AbstractDynamicDag):
    def __init__(self):
        super(PresidioDag, self).__init__()

    class Factory(AbstractDagFactory):

        def create(self, **dag_params):
            configuration_reader = dag_params.get('conf_reader')
            dags_configs = configuration_reader.read(conf_key='dags_configs')
            created_dags = self.create_dags(dags_configs=dags_configs)
            return created_dags

        def create_dags(self,dags_configs):
            """
            iterates over dags configurations and initiates dags for them
            """
            dags = []
            for dag_config in dags_configs:
                args = dag_config.get("args")
                new_dag_id = dag_config.get("dag_id")
                interval = dag_config.get("schedule_interval")
                start_date = dateutil.parser.parse(dag_config.get("start_date"))
                #     todo: add all relvant params
                new_dag = DAG(dag_id=new_dag_id,start_date=start_date,schedule_interval=interval,default_args=args)
                presidio_main_dag_id = "{}.presidio_main".format(new_dag_id)
                dags.append(new_dag)

            return dags
