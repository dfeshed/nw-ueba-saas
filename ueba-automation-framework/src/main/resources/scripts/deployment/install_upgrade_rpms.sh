#!/usr/bin/env bash
set -e

echo "######################################## install_upgrade_rpms.sh Script Started #######################################"
NEW_RPM_VERSION=$1
OLD_RPM_VERSION=$2
NUMBER_OF_PRESIDIO_RPMS=$(rpm -qa | grep presidio | wc -l)
SERVICES_TO_STOP=(airflow-webserver airflow-scheduler)
PRESIDIO_RPMS=(rsa-nw-presidio-configserver rsa-nw-presidio-airflow rsa-nw-presidio-ext-netwitness rsa-nw-presidio-elasticsearch-init rsa-nw-presidio-ui rsa-nw-presidio-manager rsa-nw-presidio-core rsa-nw-presidio-output rsa-nw-presidio-flume)
NW_LOG_PLAYER=$(rpm -qa | grep logplayer | wc -l)

if [ -z "$NEW_RPM_VERSION" ]; then
    echo "No New RPM Version selected"
    exit 1
fi

if [[ $(systemctl is-active airflow-scheduler) == 'active' ]]||[[ $(systemctl is-active airflow-webserver) == 'active' ]]; then
    sudo systemctl stop airflow-webserver
    sudo systemctl stop airflow-scheduler
fi


sudo systemctl stop iptables
echo "old_rpms_version=" $OLD_RPM_VERSION"; new_rpms_version=" $NEW_RPM_VERSION ";"
OLD_RPM_VERSION=`echo $OLD_RPM_VERSION | sed 's/[\.]//g'`
NEW_RPM_VERSION=`echo $NEW_RPM_VERSION | sed 's/[\.]//g'`

if [[ "$NEW_RPM_VERSION" -ge "$OLD_RPM_VERSION" ]]&&[[ "$NUMBER_OF_PRESIDIO_RPMS" -eq 9 ]]; then
        echo "Updating Presidio RPMs"
        for i in "${PRESIDIO_RPMS[@]}"
                do
                OWB_ALLOW_NON_FIPS=on && sudo -E yum update -y  $i
                done
else
        if [[ "$NUMBER_OF_PRESIDIO_RPMS" -ne 0 ]]; then
        echo "Removing Old Presidio RPMs"
        OWB_ALLOW_NON_FIPS=on && sudo -E yum -y remove $(rpm -qa | grep rsa-nw-presidio)
        fi
        echo "Installing Presidio RPMs"
        for i in "${PRESIDIO_RPMS[@]}"
                do
                OWB_ALLOW_NON_FIPS=on && sudo -E yum install -y $i
                done
fi

if [[ NW_LOG_PLAYER -eq 0 ]]; then
        echo "Installing nw-logplyer"
        sudo -E yum install -y rsa-nw-logplayer
fi

PRESIDIO_JARS_AMOUNT=$(ls /var/netwitness/presidio/batch/*jar | wc -l)

if [ NEW_RPM_VERSION == "11.4.0.0" ];then
    RPN_NUM=13
else
    RPN_NUM=12
fi

if [ "$PRESIDIO_JARS_AMOUNT" != "$RPN_NUM" ];then
    echo "/var/netwitness/presidio/batch/ directory does not contain the number of expected jar files"
    exit 1
fi

sudo  systemctl daemon-reload
echo "######################################## install_upgrade_rpms.sh Script Started #######################################"
