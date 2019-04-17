from datetime import datetime, timedelta

import os
from airflow import DAG

from presidio.builders.maintenance.airflow_db_cleanup_dag_builder import AirflowDbCleanupDagBuilder
from presidio.builders.maintenance.airflow_log_cleanup_dag_builder import AirflowLogCleanupDagBuilder
from presidio.builders.maintenance.presidio_metrics_cleanup_builder import PresidioMetircsCleanupDagBuilder
from presidio.builders.rerun_full_flow_dag_builder import build_clean_adapter_operator

ADAPTER_PROPERTIES_PATH = "/var/lib/netwitness/presidio/flume/conf/adapter/"
DEFAULT_NUM_HOURS_NOT_DELETE = 12

DAG_ID = os.path.basename(__file__).replace(".pyc", "").replace(".py", "")  # maintenance_flow_dag

START_DATE = datetime(year=2017, month=1, day=1)

SCHEDULE_INTERVAL = "@hourly"            # How often to Run. @daily - Once a day at Midnight (UTC)
DAG_OWNER_NAME = "operations"           # Who is listed as the owner of this DAG in the Airflow Web Server
ALERT_EMAIL_ADDRESSES = []              # List of email address to send email alerts to if this job fails

default_args = {
    'owner': DAG_OWNER_NAME,
    'email': ALERT_EMAIL_ADDRESSES,
    'email_on_failure': True,
    'email_on_retry': False,
    'start_date': START_DATE,
    'retries': 1,
    'retry_delay': timedelta(minutes=1)
}

dag = DAG(DAG_ID, default_args=default_args, schedule_interval=SCHEDULE_INTERVAL, start_date=START_DATE, catchup=False)

airflow_log_cleanup_operator = AirflowLogCleanupDagBuilder().create_sub_dag_operator("airflow-log-cleanup", dag)
airflow_db_cleanup_operator = AirflowDbCleanupDagBuilder().create_sub_dag_operator("airflow-db-cleanup", dag)
presidio_monitoring_maintenance_operator = PresidioMetircsCleanupDagBuilder().create_sub_dag_operator("presidio-metrics-cleanup", dag)
clean_adapter_operator = build_clean_adapter_operator(dag, DEFAULT_NUM_HOURS_NOT_DELETE, 'adapter-properties-cleanup')


