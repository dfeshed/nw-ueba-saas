#!/bin/bash

NEW_RPM_VERSION=$1
OLD_RPM_VERSION=$2

echo "old_rpms_version=" $OLD_RPM_VERSION"; new_rpms_version=" $NEW_RPM_VERSION ";"
OLD_RPM_VERSION=$(echo $OLD_RPM_VERSION | sed 's/[\.]//g')
NEW_RPM_VERSION=$(echo $NEW_RPM_VERSION | sed 's/[\.]//g')

cleanningMongoCollections() {
  echo "############## cleanningMongoCollections started ##############"
  MONGO_HOST=$(cat /etc/netwitness/presidio/configserver/configurations/application.properties | sed -E -n 's/.*\mongo\.host\.name=(.*)$/\1/p')
  CORE_ENC_PASS=$(cat /etc/netwitness/presidio/configserver/configurations/application.properties | sed -E -n 's/.*\mongo\.db\.password=(.*)$/\1/p')
  UI_ENC_PASS=$(cat /etc/netwitness/presidio/configserver/configurations/presidio-uiconf.properties | sed -E -n 's/.*\mongo\.db\.password=(.*)$/\1/p')
  CORE_DEC_PASS=$(java -jar /var/lib/netwitness/presidio/install/configserver/EncryptionUtils.jar decrypt "$CORE_ENC_PASS")
  UI_DEC_PASS=$(java -jar /var/lib/netwitness/presidio/install/configserver/EncryptionUtils.jar decrypt "$UI_ENC_PASS")

  echo "mongo -host $MONGO_HOST presidio -u presidio -p $CORE_DEC_PASS"
  mongo -host $MONGO_HOST presidio -u presidio -p $CORE_DEC_PASS --eval "db.getCollectionNames().forEach(function(t){db.getCollection(t).drop()});"
  mongo -host $MONGO_HOST presidio-ui -u presidio -p $UI_DEC_PASS --eval "db.application_configuration.remove({})"
  echo "############## cleanningMongoCollections finished ##############"
}

cleanningAirflowState() {
  echo "############## cleanningAirflowState started ##############"
  rm -f /etc/netwitness/presidio/configserver/configurations/airflow/workflows-default.json
  rm -f /etc/netwitness/presidio/configserver/configurations/application-presidio.json
  source /etc/sysconfig/airflow
  export AIRFLOW_VENV
  export OWB_ALLOW_NON_FIPS
  export AIRFLOW_HOME
  export AIRFLOW_CONFIG
  source ${AIRFLOW_VENV}/bin/activate
  if [[ "$NEW_RPM_VERSION" -lt 11400 ]] && [[ "$OLD_RPM_VERSION" -ge 11400 ]]; then
    echo "drop airflow tables"
    #drop table dag_pickle; drop table dag_stats; drop table connection;drop table chart; drop table users; drop table dag_run; drop table dag; drop table import_error ;drop table job ;drop table known_event ;drop table known_event_type ;drop table kube_resource_version ;drop table kube_worker_uuid ;drop table log ;drop table sla_miss ;drop table slot_pool ;drop table task_fail;drop table task_instance; drop table task_reschedule;drop table users;drop table variable;drop table xcom;  drop table task_instance; drop table alembic_version;
  fi
  airflow resetdb -y
  python -c "from presidio.charts import deploy_charts; deploy_charts.deploy_charts()"
  echo "############## cleanningAirflowState finished ##############"
}

cleanningFlumePropetrieFiles() {
  echo "############## cleanningFlumePropetrieFiles started ##############"
  #echo "cleanningFlumePropetrieFiles"
  cd /var/netwitness/presidio/flume/conf/adapter/
  rm -f file_* authentication_* active_directory_* registry_* process_* tls_*

  echo "############## cleanningFlumePropetrieFiles finished ##############"
}

cleanningLogs() {
  echo "############## cleanningLogs started ##############"
  if [ "$(ls -a /var/log/netwitness/presidio/3p/airflow/logs/*flow*)" ]; then
    #echo "Cleaning Airflow Logs"
    rm -rf /var/log/netwitness/presidio/3p/airflow/logs/*flow*
    rm -rf /var/log/netwitness/presidio/3p/airflow/logs/reset_presidio
    rm -rf /var/log/netwitness/presidio/3p/airflow/logs/scheduler/*
  fi
  if [ "$(ls -a /tmp/presidio*.log)" ]; then
    #echo "Cleaning Test Logs"
    rm -f /tmp/presidio*.log
  fi
  echo "############## cleanningLogs finished ##############"
}

echo "%%%%%%%%%%%%%%%%%%%%%%%%%%%%% DBs & Logs Cleaning %%%%%%%%%%%%%%%%%%%%%%%%%%%%%"

if [[ $(systemctl is-active airflow-scheduler) == 'active' ]] || [[ $(systemctl is-active airflow-webserver) == 'active' ]]; then
  sudo systemctl stop airflow-webserver
  sudo systemctl stop airflow-scheduler
fi
cleanningLogs
cleanningFlumePropetrieFiles
cleanningAirflowState
cleanningMongoCollections

echo "%%%%%%%%%%%%%%%%%%%%%%%%%%%%% DBs & Logs Cleaning Completed Successfully %%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
