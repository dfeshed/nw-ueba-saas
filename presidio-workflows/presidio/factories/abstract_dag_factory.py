from abc import ABCMeta, abstractmethod
import logging

from presidio.utils.airflow.dag.dag_factory import DagFactories


class AbstractDagFactory:
    __metaclass__ = ABCMeta

    def __init__(self):
        super(AbstractDagFactory, self).__init__()
        DagFactories.add_factory(self.get_id(),self)

    def create_and_register_dags(self, **dag_params):
        dags = self.create(**dag_params)
        self.validate(dags)
        for dag in dags:
            dag_builder = dag_params.get('dag_builder')
            self.generate_dag_tasks(dag, dag_builder=dag_builder)
            name_space = dag_params.get('name_space')
            self.register_dag(dag=dag, name_space=name_space)

        return dags

    @staticmethod
    def register_dag(dag, name_space):
        """
        :param name_space: the global scope which the DAG would be assigned in to. notice: should be the globals() 
        method from the file that airflow scheduler scans (at the dag directory) 
        :type dag: DAG instance
        :param dag: dag to be registered to airflow
         once DAG is registered -> it can be scheduled and executed  
        """
        dag_id = dag.dag_id
        logging.debug("registering dag_id=%s", dag_id)
        name_space[dag_id] = dag


    @abstractmethod
    def create(self, **dag_params):
        """
        :rtype: list of DAG
        :param dag_params: params by which dags will be created
        :returns created DAG array
        """
        pass

    @abstractmethod
    def validate(self,dags):
        """
        validates that all created dags have start and end time that does not overlap
        :param dags: array of airflow dags to be validated
        """
        pass

    @abstractmethod
    def get_id(self):
        """The factory id"""
        pass

    @staticmethod
    def generate_dag_tasks(dag, dag_builder):
        """
        add task to given dag
        :param dag_builder: a service that adds tasks to a given dag
        :param dag: the airflow dag, containing scheduling params etc.
        """
        logging.debug("building dag_id=%s tasks", dag.dag_id)
        dag_builder.build(dag)
