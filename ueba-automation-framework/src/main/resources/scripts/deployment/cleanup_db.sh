#!/bin/bash

NEW_RPM_VERSION=$1
OLD_RPM_VERSION=$2

echo "old_rpms_version=" $OLD_RPM_VERSION"; new_rpms_version=" $NEW_RPM_VERSION ";"
OLD_RPM_VERSION=`echo $OLD_RPM_VERSION | sed 's/[\.]//g'`
NEW_RPM_VERSION=`echo $NEW_RPM_VERSION | sed 's/[\.]//g'`

cleaningRedis(){
echo "############## cleaningRedis started ##############"
echo "Going to clean Redis DB"
redis-cli FLUSHALL
echo "############## cleaningRedis finished ##############"
}

cleanningMongoCollections(){
echo "############## cleanningMongoCollections started ##############"
CORE_ENC_PASS=$(cat /etc/netwitness/presidio/configserver/configurations/application.properties | sed -E -n 's/.*\mongo\.db\.password=(.*)$/\1/p' )
UI_ENC_PASS=$(cat /etc/netwitness/presidio/configserver/configurations/presidio-uiconf.properties | sed -E -n 's/.*\mongo\.db\.password=(.*)$/\1/p' )
CORE_DEC_PASS=$(java -jar /var/lib/netwitness/presidio/install/configserver/EncryptionUtils.jar decrypt "$CORE_ENC_PASS")
UI_DEC_PASS=$(java -jar /var/lib/netwitness/presidio/install/configserver/EncryptionUtils.jar decrypt "$UI_ENC_PASS")
mongo presidio -u presidio -p $CORE_DEC_PASS --eval "db.getCollectionNames().forEach(function(t){db.getCollection(t).drop()});"
mongo presidio-ui -u presidio -p $UI_DEC_PASS --eval "db.application_configuration.remove({})"
echo "############## cleanningMongoCollections finished ##############"
}

cleanningElasticSearch(){
echo "############## cleanningElasticSearch started ##############"
curl -X DELETE http://localhost:9200/_all
####### Workaround to solve issue when replacing between master and hourly-output branches
####### presidio-output-entity added to 11.4 not exist on 11.3
#if [[ "$NEW_RPM_VERSION" -lt 11400 ]]&&[[  "$OLD_RPM_VERSION" -ge 11400 ]]&&[[ "$( ls -a /var/lib/netwitness/presidio/elasticsearch/init/data/indexes/presidio-output*)" ]]; then
#echo "Cleanning Elastic indexes - presidio-output*"
#sudo rm -rf /var/lib/netwitness/presidio/elasticsearch/init/data/indexes/presidio-output*
#fi
echo "############## cleanningElasticSearch finished ##############"
}

setupElasticSearch(){
echo "############## setupElasticSearch started ##############"
#Init Elasitc
source /etc/sysconfig/airflow
source $AIRFLOW_VENV/bin/activate
OWB_ALLOW_NON_FIPS=on python /var/lib/netwitness/presidio/elasticsearch/init/init_elasticsearch.py --resources_path /var/lib/netwitness/presidio/elasticsearch/init/data/ --elasticsearch_url http://localhost:9200/
deactivate
echo "############## setupElasticSearch finished ##############"
}

echo "%%%%%%%%%%%%%%%%%%%%%%%%%%%%% DBs & Logs Cleaning %%%%%%%%%%%%%%%%%%%%%%%%%%%%%"

if [[ $(systemctl is-active airflow-scheduler) == 'active' ]]||[[ $(systemctl is-active airflow-webserver) == 'active' ]]; then
	sudo systemctl stop airflow-webserver
	sudo systemctl stop airflow-scheduler
fi

cleanningMongoCollections
cleanningElasticSearch
cleaningRedis
setupElasticSearch

echo "%%%%%%%%%%%%%%%%%%%%%%%%%%%%% DBs & Logs Cleaning Completed Successfully %%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
