from abc import ABCMeta, abstractmethod

from airflow import DAG
from airflow.operators.subdag_operator import SubDagOperator


class PresidioDagBuilder(object):

    presidio_command = 'run'

    """
    The Presidio DAG Builder has 1 method that receives a DAG. The "build" method creates the DAG's operators, links
    them to the DAG and configures the dependencies between them. The inheritors are builders of different types of DAGs
    (Input DAG, ADE DAG, Output DAG, etc.), but they all have a common functional interface that receives and populates
    an Airflow DAG. Each implementation does this according to the specific DAG type.
    """

    __metaclass__ = ABCMeta

    @abstractmethod
    def build(self, dag):
        """
        Receives a DAG, creates its operators, links them to the DAG and configures the dependencies between them.
        :param dag: The DAG to populate
        :type dag: airflow.models.DAG
        :return: The given DAG, after it has been populated
        :rtype: airflow.models.DAG
        """

        pass

    def _create_sub_dag_operator(self, sub_dag_builder, sub_dag_id, dag):
        """
        create a sub dag of the recieved "dag" fill it with a flow using the sub_dag_builder
        and wrap it with a sub dag operator.
        :param sub_dag_builder: 
        :type sub_dag_builder: PresidioDagBuilder
        :param sub_dag_id:
        :type sub_dag_id: str
        :param dag: The ADE DAG to populate
        :type dag: airflow.models.DAG
        :return: The given ADE DAG, after it has been populated
        :rtype: airflow.models.DAG
        """

        sub_dag = DAG(

            dag_id='{}.{}'.format(dag.dag_id, sub_dag_id),
            schedule_interval=dag.schedule_interval,
            start_date=dag.start_date,
            default_args = dag.default_args
        )

        return SubDagOperator(
            subdag=sub_dag_builder.build(sub_dag),
            task_id=sub_dag_id,
            dag=dag
        )

    @staticmethod
    def validate_the_gap_between_dag_start_date_and_current_execution_date(dag, gap, execution_date):
        return (dag.start_date + gap) <= execution_date