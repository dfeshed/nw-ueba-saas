from abc import ABCMeta, abstractmethod

from airflow import DAG
from airflow.operators.subdag_operator import SubDagOperator

class MaintenanceDagBuilder(object):

    """
    The Maintenance DAG Builder has 1 method that receives a DAG. The "build" method creates the DAG's operators, links
    them to the DAG and configures the dependencies between them. The inheritors are builders of different types of maintenance DAGs
    (log cleanups, zombie killer, etc.), but they all have a common functional interface that receives and populates
    an Airflow DAG. Each implementation does this according to the specific DAG type.
    """

    __metaclass__ = ABCMeta

    @abstractmethod
    def build(self, dag):

        """
        Create a maintenance task, creates its operators, links them to the DAG and configures the dependencies between them.
        :param dag: The DAG to populate
        :type dag: airflow.models.DAG
        :return: The given DAG, after it has been populated
        :rtype: airflow.models.DAG
        """

        pass

    def create_sub_dag_operator(self, task_id, dag):
        """
        create a sub dag of the recieved "dag" fill it with a flow using the sub_dag_builder
        and wrap it with a sub dag operator.
        :param task_id:
        :type task_id: str
        :param dag: The parent maintenance DAG
        :type dag: airflow.models.DAG
        :return: Maintenance sub dag, after it has been populated
        :rtype: airflow.models.DAG
        """

        sub_dag = DAG(
            dag_id='{}.{}'.format(dag.dag_id, task_id),
            schedule_interval=dag.schedule_interval,
            start_date=dag.start_date,
            default_args=dag.default_args
        )

        return SubDagOperator(
            subdag=self.build(sub_dag),
            task_id=task_id,
            dag=dag)
