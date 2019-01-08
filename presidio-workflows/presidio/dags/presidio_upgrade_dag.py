from airflow import DAG
from datetime import datetime
from presidio.builders.presidioupgrade.presidio_upgrade_dag_builder import PresidioUpgradeDagBuilder

presidio_upgrade_dag = DAG(dag_id='presidio_upgrade_dag', schedule_interval=None, start_date=datetime(2019, 1, 1))
presidio_upgrade_dag = PresidioUpgradeDagBuilder.build(presidio_upgrade_dag)
