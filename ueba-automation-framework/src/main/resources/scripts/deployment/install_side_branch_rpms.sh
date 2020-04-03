#!/usr/bin/env bash
set -e
############################### install side branch rpms from jenkins artifacts ###############################
BUILD_ID=$1
PRESIDIO_EXPECTED_RPMS=(rsa-nw-presidio-configserver rsa-nw-presidio-airflow rsa-nw-presidio-ext-netwitness rsa-nw-presidio-elasticsearch-init rsa-nw-presidio-ui rsa-nw-presidio-manager rsa-nw-presidio-core rsa-nw-presidio-output rsa-nw-presidio-flume)
NW_LOG_PLAYER=$(rpm -qa | grep logplayer | wc -l)
RPMS_DIR=/tmp/presidio_rpms/
ARTIFACTORY_LINK=http://asoc-esa-jenkins.rsa.lab.emc.com/view/UEBA/job/presidio-build-jars-and-packages/${BUILD_ID}/artifact/
PRESIDIO_RPMS=()

if [ "$( ls -a /tmp/presidio_rpms)" ]; then
	rm -f $RPMS_DIR/*
else 
	mkdir $RPMS_DIR
fi

########  Installing rsa-nw-logplayer
if [[ NW_LOG_PLAYER -eq 0 ]]; then
        echo "Installing nw-logplyer"
        sudo -E yum install -y rsa-nw-logplayer
fi
########  Stoping Airflow Services
if [[ $(systemctl is-active airflow-scheduler) == 'active' ]]||[[ $(systemctl is-active airflow-webserver) == 'active' ]]; then
	sudo systemctl stop airflow-webserver
	sudo systemctl stop airflow-scheduler
fi

########  Download Branch RPMS from Jenkins Artifacts
wget -q -O- "http://asoc-esa-jenkins.rsa.lab.emc.com/view/UEBA/job/presidio-build-jars-and-packages/${BUILD_ID}/api/json?tree=artifacts[relativePath]" | python -m json.tool > build_artifacts.json
grep 'noarch.rpm' build_artifacts.json > build_rpms.txt
if [ ! -s build_rpms.txt ]; then
	echo "There is no RPM files in the requsted build id"
	exit 2
else
	cat build_rpms.txt
fi

while IFS= read -r line
	do
		PRESIDIO_RPMS+=($(echo "$line" | awk -v FS="(relativePath\": \"|rpm\")" '{print $2}'))
done < build_rpms.txt

cd $RPMS_DIR
for i in "${PRESIDIO_RPMS[@]}"
    do
        url=$ARTIFACTORY_LINK$i"rpm"
        echo $(wget -q $url)
done

######## Removing and installing side branch RPMS
echo "Removing Old Presidio RPMs"
OWB_ALLOW_NON_FIPS=on && sudo -E yum -y remove $(rpm -qa | grep rsa-nw-presidio)
cd $RPMS_DIR ; ls -l
OWB_ALLOW_NON_FIPS=on && sudo -E yum -y install --nogpgcheck rsa-nw-presidio*.rpm

######## Completing RPMs Instllation (installing missing RPMs from the master branch)
for i in "${PRESIDIO_EXPECTED_RPMS[@]}"
    do
	if [[ ! $( find ${RPMS_DIR} -name "$i*") ]]; then
		echo "$(OWB_ALLOW_NON_FIPS=on && sudo -E yum install -y $i)"
	fi
done
sudo  systemctl daemon-reload
echo "######################################## install_upgrade_rpms.sh Script Started #######################################"
