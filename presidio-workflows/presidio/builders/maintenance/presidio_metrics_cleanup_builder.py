import logging
import sys
import json

from airflow.operators.python_operator import PythonOperator
from presidio.builders.maintenance.maintenance_dag_builder import MaintenanceDagBuilder
from elasticsearch import Elasticsearch

class PresidioMetircsCleanupDagBuilder(MaintenanceDagBuilder):

    DEFAULT_MAX_APP_METRICS_AGE_IN_DAYS = 30
    DEFAULT_MAX_SYS_METRICS_AGE_IN_DAYS = 30

    def build(self, dag):

        clean_application_metrics_operator = PythonOperator(task_id='clean_presidio_application_metrics',
                                                            python_callable=PresidioMetircsCleanupDagBuilder.cleanup_app_metrics_function,
                                                            provide_context=True,
                                                            dag=dag)

        clean_system_metrics_operator = PythonOperator(task_id='clean_presidio_system_metrics',
                                                       python_callable=PresidioMetircsCleanupDagBuilder.cleanup_sys_metrics_function,
                                                       provide_context=True,
                                                       dag=dag)
        return dag


    @staticmethod
    def cleanup_app_metrics_function(**context):

        es = Elasticsearch()

        logging.info("Running App Metrics Cleanup Process...")
        dag_run_conf = context.get("dag_run").conf
        logging.info("dag_run.conf: " + str(dag_run_conf))
        max_app_metrics_age_in_days = None
        if dag_run_conf:
            max_app_metrics_age_in_days = dag_run_conf.get("maxAppMetricsAgeInDays", None)
        logging.info("maxDBEntryAgeInDays from dag_run.conf: " + str(dag_run_conf))
        if max_app_metrics_age_in_days is None:
            max_app_metrics_age_in_days = PresidioMetircsCleanupDagBuilder.DEFAULT_MAX_APP_METRICS_AGE_IN_DAYS
        logging.info("maxDBEntryAgeInDays conf variable isn't included. Using Default '" + str(max_app_metrics_age_in_days) + "'")

        try:
            response = es.indices.delete(index='<presidio-monitoring-{now/d-'+ str(max_app_metrics_age_in_days) + 'd}>', ignore=404)
            logging.info("response: " + json.dumps(response))

        except Exception as e:
            logging.error("failed to clean System Metrics")
            logging.exception(e)
            sys.exit(1)

        logging.info("Finished Performing Application Metrics Delete")


    @staticmethod
    def cleanup_sys_metrics_function(**context):

        es = Elasticsearch()

        logging.info("Running System Metrics Cleanup Process...")
        dag_run_conf = context.get("dag_run").conf
        logging.info("dag_run.conf: " + str(dag_run_conf))
        max_sys_metrics_age_in_days = None
        if dag_run_conf:
            max_sys_metrics_age_in_days = dag_run_conf.get("maxSysMetricsAgeInDays", None)
        logging.info("maxSysEntryAgeInDays from dag_run.conf: " + str(dag_run_conf))
        if max_sys_metrics_age_in_days is None:
            max_sys_metrics_age_in_days = PresidioMetircsCleanupDagBuilder.DEFAULT_MAX_SYS_METRICS_AGE_IN_DAYS
        logging.info("maxSysEntryAgeInDays conf variable isn't included. Using Default '" + str(max_sys_metrics_age_in_days) + "'")

        try:
            response = es.indices.delete(index='<metricbeat-6.0.0-{now/d-' + str(max_sys_metrics_age_in_days) + 'd}>', ignore=404)
            logging.info("response: " + json.dumps(response))

        except Exception as e:
            logging.error("failed to clean Metricbeat Metrics")
            logging.exception(e)
            sys.exit(1)

        try:
            response = es.indices.delete(index='<packetbeat-6.1.2-{now/d-' + str(max_sys_metrics_age_in_days) + 'd}>', ignore=404)
            logging.info("response: " + json.dumps(response))

        except Exception as e:
            logging.error("failed to clean Packetbeat Metrics")
            logging.exception(e)
            sys.exit(1)

        logging.info("Finished Performing System Metrics Delete")