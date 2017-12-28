import logging
from datetime import datetime, timedelta

from airflow.jobs import BaseJob
from airflow.models import DagRun, TaskInstance, Log, XCom, SlaMiss
from airflow.models import settings
from airflow.operators.python_operator import PythonOperator

from presidio.builders.maintenance.maintenance_dag_builder import MaintenanceDagBuilder


class AirflowDbCleanupDagBuilder(MaintenanceDagBuilder):

    DEFAULT_MAX_DB_ENTRY_AGE_IN_DAYS = 30
    ENABLE_DELETE = True

    def __init__(self):
        self.database_objects = [
            # List of all the objects that will be deleted. Comment out the DB objects you want to skip.
            {"airflow_db_model": DagRun, "age_check_column": DagRun.start_date},
            {"airflow_db_model": TaskInstance, "age_check_column": TaskInstance.start_date},
            {"airflow_db_model": Log, "age_check_column": Log.dttm},
            {"airflow_db_model": XCom, "age_check_column": XCom.timestamp},
            {"airflow_db_model": BaseJob, "age_check_column": BaseJob.latest_heartbeat},
            {"airflow_db_model": SlaMiss, "age_check_column": SlaMiss.timestamp},
        ]

    def build(self, dag):
        print_configuration = PythonOperator(
            task_id='print_configuration',
            python_callable=AirflowDbCleanupDagBuilder.print_configuration_function,
            provide_context=True,
            dag=dag)

        for db_object in self.database_objects:
            cleanup = PythonOperator(
                task_id='cleanup_' + str(db_object["airflow_db_model"].__name__),

                python_callable=AirflowDbCleanupDagBuilder.cleanup_function,
                params=db_object,
                provide_context=True,
                dag=dag
            )

            print_configuration.set_downstream(cleanup)

        return dag

    @staticmethod
    def print_configuration_function(**context):
        session = settings.Session()
        logging.info("Loading Configurations...")
        dag_run_conf = context.get("dag_run").conf
        logging.info("dag_run.conf: " + str(dag_run_conf))
        max_db_entry_age_in_days = None
        if dag_run_conf:
            max_db_entry_age_in_days = dag_run_conf.get("maxDBEntryAgeInDays", None)
        logging.info("maxDBEntryAgeInDays from dag_run.conf: " + str(dag_run_conf))
        if max_db_entry_age_in_days is None:
            max_db_entry_age_in_days = AirflowDbCleanupDagBuilder.DEFAULT_MAX_DB_ENTRY_AGE_IN_DAYS
            logging.info("maxDBEntryAgeInDays conf variable isn't included. Using Default '" + str(
                max_db_entry_age_in_days) + "'")

        max_date = datetime.now() + timedelta(-max_db_entry_age_in_days)
        logging.info("Finished Loading Configurations")
        logging.info("")

        logging.info("Configurations:")
        logging.info("max_db_entry_age_in_days: " + str(max_db_entry_age_in_days))
        logging.info("max_date:                 " + str(max_date))
        logging.info("enable_delete:            " + str(AirflowDbCleanupDagBuilder.ENABLE_DELETE))
        logging.info("session:                  " + str(session))
        logging.info("")

        logging.info("Setting max_execution_date to XCom for Downstream Processes")
        context["ti"].xcom_push(key="max_date", value=max_date)

    @staticmethod
    def cleanup_function(**context):
        session = settings.Session()
        logging.info("Retrieving max_execution_date from XCom")
        max_date = context["ti"].xcom_pull(task_ids='print_configuration', key="max_date")

        airflow_db_model = context["params"].get("airflow_db_model")
        age_check_column = context["params"].get("age_check_column")

        logging.info("Configurations:")
        logging.info("max_date:                 " + str(max_date))
        logging.info("enable_delete:            " + str(AirflowDbCleanupDagBuilder.ENABLE_DELETE))
        logging.info("session:                  " + str(session))
        logging.info("airflow_db_model:         " + str(airflow_db_model))
        logging.info("age_check_column:         " + str(age_check_column))
        logging.info("")

        logging.info("Running Cleanup Process...")

        entries_to_delete = session.query(airflow_db_model).filter(
            age_check_column <= max_date,
        ).all()
        logging.info("Process will be Deleting the following " + str(airflow_db_model.__name__) + "(s):")
        for entry in entries_to_delete:
            logging.info("\tEntry: " + str(entry) + ", Date: " + str(entry.__dict__[str(age_check_column).split(".")[1]]))
            logging.info(
                "Process will be Deleting " + str(len(entries_to_delete)) + " " + str(airflow_db_model.__name__) + "(s)")

        if AirflowDbCleanupDagBuilder.ENABLE_DELETE:
            logging.info("Performing Delete...")
            for entry in entries_to_delete:
                session.delete(entry)
            logging.info("Finished Performing Delete")
        else:
            logging.warn("You're opted to skip deleting the db entries!!!")

        logging.info("Finished Running Cleanup Process")
