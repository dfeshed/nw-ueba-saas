"""
initiate historical dags that are defined in variables
"""
from __future__ import generators

import json

import dateutil.parser
import pkg_resources
from airflow import DAG
from airflow.models import Variable
from airflow.operators.dagrun_operator import TriggerDagRunOperator


def create_presidio_main_dag(dag_id):
    """
    :param dag_id
    :type dag_id: str
    :return: instance of Presidio's main dag containing given    
    """
    dag = DAG(dag_id=dag_id)
    globals()[dag_id] = dag
    return dag


def conditionally_trigger(context, dag_run_obj):
    """This function decides whether or not to Trigger the remote DAG"""
    # todo add conditions if needed
    return dag_run_obj





class DagsFactory:
    factories = {}

    def addFactory(id, dagsFactory):
        DagsFactory.factories[id] = dagsFactory

    addFactory = staticmethod(addFactory)

    def createDags(id, **kwargs):
        if not DagsFactory.factories.has_key(id):
            DagsFactory.factories[id] = eval(id + '.Factory()')
        return DagsFactory.factories[id].create_and_register_dags(**kwargs)

    createDags = staticmethod(createDags)


class VariableReader(object):
    def __init__(self, var_key, default_var_file_path):
        self.__var_key = var_key
        self.__default_var_file_path = default_var_file_path

    def __get_default_variable(self):
        """
        reads default variable configured in resourced json
        :return: json dictionary containing default variable value
        """

        with open(self.__default_var_file_path) as default_dag_variables_file:
            default_dag_variables = json.load(default_dag_variables_file)
        return default_dag_variables

    def get_dag_variable(self):
        """
        :return: Try to get the value of the variable "variable_scheduler_dag_params" from airflow's variables key-value store
        if not exist, default value is returned 
        """
        return Variable.get(self.__var_key, default_var=self.__get_default_variable(),
                            deserialize_json=True)


class AbstractDynamicDag(object): pass


class AbstractDagFactory(object):
    def create_and_register_dags(self,**dag_params):
        dags = self.create(**dag_params)
        self.register_dags(dags=dags)

    @staticmethod
    def register_dags(dags):
        """
        :type dags: DAG array
        :param dags: dags array to be reggistred to airflow
         once DAG is registered -> it can be scheduled and executed  
        """
        for dag in dags:
            globals()[dag._dag_id] = dag

    def create(self, **dag_params):
        """
        :param dag_params: params by which dags will be created
        :returns created DAG array
        :rtype list of DAG 
         
        """
        pass


class PresidioDag(AbstractDynamicDag):
    def __init__(self):
        super(PresidioDag, self).__init__()

    class Factory(AbstractDagFactory):
        def create(self, **dag_params):
            variable_reader = dag_params.get('variable_reader')
            dags_configs = variable_reader.get_dag_variable().get('dags_configs')
            return self.create_dags(dags_configs=dags_configs)

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
                new_dag = DAG(new_dag_id)
                presidio_main_dag_id = "{}.presidio_main".format(new_dag_id)
                presidio_main_dag = create_presidio_main_dag(dag_id=presidio_main_dag_id)
                presidio_main_dag_run_operator = TriggerDagRunOperator(task_id=presidio_main_dag_id,
                                                                       start_date=start_date,
                                                                       trigger_dag_id=presidio_main_dag_id, dag=new_dag,
                                                                       python_callable=conditionally_trigger)
                dags.append(new_dag)

            return dags

        # def DagNameGen(n):


# types = DynamicDag.__subclasses__()
#     for i in range(n):
#         yield random.choice(types).__name__
# shapes = [DagsFactory.createDags(i)
#           for i in DagNameGen(7)]
# DagsFactory.addFactory("PresidioDag",PresidioDag)
DEFAULT_DAG_VARIABLES_FILE_PATH = pkg_resources.resource_filename('presidio',
                                                                  'resources/variables/dags/historical_workflow_creator.json')
dags = DagsFactory.createDags("PresidioDag", variable_reader=VariableReader(var_key="historical_workflow_creator",
                                                                            default_var_file_path=DEFAULT_DAG_VARIABLES_FILE_PATH))
#
# variable_historical_workflow_creator = get_dag_variable()
# params = variable_historical_workflow_creator.get("historical_dags")
# create_historical_dags(params)
