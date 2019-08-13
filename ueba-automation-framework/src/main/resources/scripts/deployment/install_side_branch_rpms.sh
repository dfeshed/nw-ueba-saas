#!/usr/bin/env bash
set -e
############################### install side branch rpms from jenkins artifacts ###############################
BUILD_ID=$1

RPMS_DIR=/tmp/presidio_rpms/
if [ "$( ls -a /tmp/presidio_rpms)" ]; then
		rm -f $RPMS_DIR*
else 
		mkdir $RPMS_DIR
fi

########  Installing rsa-nw-logplayer
NW_LOG_PLAYER=$(rpm -qa | grep logplayer | wc -l)
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
ARTIFACTORY_LINK=http://asoc-esa-jenkins.rsa.lab.emc.com/view/UEBA/job/presidio-build-jars-and-packages/${BUILD_ID}/artifact/
PRESIDIO_RPMS=()
wget -q -O- "http://asoc-esa-jenkins.rsa.lab.emc.com/view/UEBA/job/presidio-build-jars-and-packages/${BUILD_ID}/api/json?tree=artifacts[relativePath]" | python -m json.tool > build_artifacts.json
grep 'noarch.rpm' build_artifacts.json > build_rpms.txt
while IFS= read -r line
	do
		PRESIDIO_RPMS+=($(echo "$line" | awk -v FS="(relativePath\": \"|rpm\")" '{print $2}'))
done < build_rpms.txt
if [ -d "$RPMS_DIR" ]; then
        rm -f $RPMS_DIR/*
else
        mkdir -p $RPMS_DIR
fi
cd $RPMS_DIR
for i in "${PRESIDIO_RPMS[@]}"
        do
        url=$ARTIFACTORY_LINK$i"rpm"
        echo $(wget $url)
        done

########   Removing and installing RPMS
echo "Removing Old Presidio RPMs"
OWB_ALLOW_NON_FIPS=on && sudo -E yum -y remove $(rpm -qa | grep rsa-nw-presidio)
cd $RPMS_DIR
OWB_ALLOW_NON_FIPS=on && sudo -E yum -y install --nogpgcheck rsa-nw-presidio*.rpm
sudo  systemctl daemon-reload
echo "######################################## install_upgrade_rpms.sh Script Started #######################################"
