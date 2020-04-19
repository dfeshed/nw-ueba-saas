#!/bin/bash

NEW_RPM_VERSION=$1
OLD_RPM_VERSION=$2

echo "old_rpms_version=" $OLD_RPM_VERSION"; new_rpms_version=" $NEW_RPM_VERSION ";"
OLD_RPM_VERSION=$(echo $OLD_RPM_VERSION | sed 's/[\.]//g')
NEW_RPM_VERSION=$(echo $NEW_RPM_VERSION | sed 's/[\.]//g')

cleaningRedis() {
  echo "############## cleaningRedis started ##############"
  echo "Going to clean Redis DB"
  redis-cli FLUSHALL
  echo "############## cleaningRedis finished ##############"
}

cleanningElasticSearch() {
  echo "############## cleanningElasticSearch started ##############"
  curl -X DELETE http://localhost:9200/_all
  echo "############## cleanningElasticSearch finished ##############"
}

setupElasticSearch() {
  echo "############## setupElasticSearch started ##############"
  #Init Elasitc
  source /etc/sysconfig/airflow
  source $AIRFLOW_VENV/bin/activate
  OWB_ALLOW_NON_FIPS=on python /var/lib/netwitness/presidio/elasticsearch/init/init_elasticsearch.py --resources_path /var/lib/netwitness/presidio/elasticsearch/init/data/ --elasticsearch_url http://localhost:9200/
  deactivate
  systemctl restart elasticsearch kibana
  echo "############## setupElasticSearch finished ##############"
}

cleanningElasticSearch
cleaningRedis
setupElasticSearch

echo "%%%%%%%%%%%%%%%%%%%%%%%%%%%%% DBs & Logs Cleaning Completed Successfully %%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
