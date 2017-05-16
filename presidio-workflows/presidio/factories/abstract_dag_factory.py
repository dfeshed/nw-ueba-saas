from abc import ABCMeta, abstractmethod
import logging

class AbstractDagFactory:
    __metaclass__ = ABCMeta

    def create_and_register_dags(self, **dag_params):
        dags = self.create(**dag_params)
        for dag in dags:
            self.generate_dag_tasks(dag, dag_builder=dag_params.get('dag_builder'))
        self.register_dags(dags=dags, name_space=dag_params.get('name_space'))
        return dags

    @staticmethod
    def register_dags(dags, name_space):
        """
        :param name_space: the global scope which the DAG would be assigned in to. notice: should be the globals() 
        method from the file that airflow scheduler scans (at the dag directory) 
        :type dags: DAG array
        :param dags: dags array to be registered to airflow
         once DAG is registered -> it can be scheduled and executed  
        """
        for dag in dags:
            dag_id = dag._dag_id
            logging.info("registering dag_id=%s",dag_id)
            name_space[dag_id] = dag

    @abstractmethod
    def create(self, **dag_params):
        """
        :rtype: list of DAG
        :param dag_params: params by which dags will be created
        :returns created DAG array
        """
        pass

    def generate_dag_tasks(self, dag, dag_builder):
        """
        add task to given dag
        :param dag_builder: a service that adds tasks to a given dag
        :param dag: the airflow dag, containing scheduling params etc.
        """
        logging.info("building dag_id=%s tasks", dag._dag_id)
        dag_builder.build(dag)
