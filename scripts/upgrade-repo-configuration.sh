#!/usr/bin/env bash
set -e

NW_VERSION=$1
##### this file should be run only on UEBA machine

echo "  #############  Starting upgrade-repo-configuration.sh #############"
  ##### this part is done because retruning ueba machine to a snapshot the airflow/logs dirctory is fills to the end
  ##### Cleaning airflow logs:
	rm -rf /var/log/netwitness/presidio/3p/airflow/logs/*flow*
	rm -rf /var/log/netwitness/presidio/3p/airflow/logs/reset_presidio
	rm -rf /var/log/netwitness/presidio/3p/airflow/logs/scheduler/*
  systemctl restart airflow-webserver
  systemctl restart airflow-scheduler
  #####

echo "NW_VERSION=${NW_VERSION}"
sed -i "s|baseurl=.*|baseurl=https://nw-node-zero/nwrpmrepo/${NW_VERSION}/OS|g" /etc/yum.repos.d/nw-os-base.repo
sed -i "s|baseurl=.*|baseurl=https://nw-node-zero/nwrpmrepo/${NW_VERSION}/RSA|g" /etc/yum.repos.d/nw-rsa-base.repo
OWB_ALLOW_NON_FIPS=on yum clean all
rm -rf /var/cache/yum

echo "  #############  upgrade-repo-configuration.sh DONE #############"
