import time

import argparse
from airflow.models import DagBag, DagRun
from airflow.operators.subdag_operator import SubDagOperator
from airflow.utils.state import State
import os
from signal import SIGTERM
import logging

SLEEP_DURATION = 3


class ProcessUtils:
    def __init__(self):
        pass

    @staticmethod
    def check_pid(pid):
        """ Check For the existence of a unix pid. """
        try:
            os.kill(pid, 0)
        except OSError:
            return False
        else:
            return True

    @staticmethod
    def kill_pid(pid):
        """ send a SIGTERM to unix pid """
        try:
            os.kill(pid, SIGTERM)
        except OSError:
            return False
        else:
            return True


class AirflowDALUtils:
    def __init__(self):
        self.dagbag = self.get_dagbag()

    @staticmethod
    def get_dagbag():
        return DagBag()

    def get_dags_not_subdags(self):
        """
    
        :return: dict of DAGs that are not a sub DAG (can be found in DAG's folder)  
        """
        all_dags = self.dagbag.dags
        non_subdags_dags = {k: v for k, v in all_dags.iteritems() if v.is_subdag == False}
        return non_subdags_dags

    def get_dag_by_prefix(self, prefix):
        """
    
        :param prefix: string describing the dag name 
        :return: first dag that has id the starts with prefix
        """
        dags = self.get_dags_not_subdags()
        for dag_id, dag_instance in dags.iteritems():
            if dag_id.startswith(prefix):
                return dag_instance


class TaskPunisher:
    """
    a class dedicated to punish tasks without retries configured
    """
    def __init__(self, dag_prefix):
        self.dag_prefix = dag_prefix
        self.airflowDal = AirflowDALUtils()

    def get_tasks_to_kill(self, dag, dag_to_tasks_list):
        """
    
        :param dag: dag to scan for tasks 
        :param dag_to_tasks_list: dict to be filled with key:dag_id (or subdag) value: list of task instances to kill
        """
        dag_to_tasks_list[dag.dag_id] = dag.tasks
        for task in dag.tasks:
            if isinstance(task, SubDagOperator):
                self.get_tasks_to_kill(dag=task.subdag, dag_to_tasks_list=dag_to_tasks_list)

    def punish(self,task_id=None):
        dag = self.airflowDal.get_dag_by_prefix(prefix=self.dag_prefix)
        dag_to_tasks_list = {}
        self.get_tasks_to_kill(dag=dag, dag_to_tasks_list=dag_to_tasks_list)
        killed_tasks = []

        filtered_task_id={}
        if task_id:
            for dag,tasks in dag_to_tasks_list.iteritems():
                for task in tasks:
                    if task_id == task.task_id:
                        filtered_task_array=[]
                        filtered_task_array.append(task_id)
                        filtered_task_id[dag] = filtered_task_array

        if filtered_task_id:
            dag_to_tasks_list=filtered_task_id

        # while we haven't killed them all
        while killed_tasks.__len__() != dag_to_tasks_list.values().__len__():
            for dag, tasks in dag_to_tasks_list.iteritems():
                # refresh dag run state. we can multiple runs in the same time
                running_dags = DagRun.find(state=State.RUNNING, dag_id=dag)
                for running_dag in running_dags:
                    # don't try to kill tasks that are already killed once
                    for task in [item for item in tasks if item not in killed_tasks]:
                        if isinstance(task,str):
                            task_instance = running_dag.get_task_instance(task_id=task)
                        else:
                            task_instance = running_dag.get_task_instance(task_id=task.task_id)
                        if task_instance.state == State.RUNNING:
                            if ProcessUtils.check_pid(task_instance.pid):
                                logging.info("poking for task_id=%s", task)
                                if ProcessUtils.kill_pid(task_instance.pid):
                                    logging.info("task_id=%s successfully killed at %s", task,
                                                 str(running_dag.execution_date))
                                    killed_tasks.append(task)
            logging.info("going to sleep for %d seconds", SLEEP_DURATION)
            time.sleep(SLEEP_DURATION)


parser = argparse.ArgumentParser(description='kills airflow tasks')
parser.add_argument('--dag_prefix', type=str, required=True,
                    help='prefix of dag name to kill')
parser.add_argument('--task_id', type=str, required=False,
                    help='filter specific task id to kill',default=None)
args = parser.parse_args()

tp = TaskPunisher(args.dag_prefix)
tp.punish(task_id=args.task_id)
