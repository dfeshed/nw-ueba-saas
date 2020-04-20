#!/usr/bin/env bash

#NEW_RPM_VERSION=$1
#OLD_RPM_VERSION=$2
SCHEDULER_STATUS=$(systemctl is-active airflow-scheduler)
sudo  systemctl daemon-reload
echo "######################################## Running Initiate-presidio-services.sh ########################################" 
echo "####################################### Starting UEBA Services: #######################################"
counter=1
sudo systemctl restart presidio-configserver
while ! curl --output /dev/null --silent --head --fail http://localhost:8888/application-default.properties ; do
echo -n . && sleep 1 && counter=$[$counter+1]
if [[ $counter -gt 122 ]] ; then
    echo 'presidio-configserver service did not go up after 2 minutes'
    exit 1
fi
done;
sudo systemctl restart presidio-manager
sudo systemctl restart presidio-ui
sudo systemctl restart presidio-output

echo "######################################## Installing Airflow in a virtualenv #######################################"
if [[ $(systemctl is-active airflow-scheduler) == 'active' ]]||[[ $(systemctl is-active airflow-webserver) == 'active' ]]; then
	sudo systemctl stop airflow-webserver
	sudo systemctl stop airflow-scheduler
fi

#source /etc/sysconfig/airflow
#sudo -E OWB_ALLOW_NON_FIPS=on python -m pip install --no-index --find-links=$(dirname "$AIRFLOW_PKG_REQ")/../virtualenv virtualenv==15.2.0
#sudo -E OWB_ALLOW_NON_FIPS=on python -m virtualenv $AIRFLOW_VENV
#source $AIRFLOW_VENV/bin/activate
#OWB_ALLOW_NON_FIPS=on python -m pip install --no-index --find-links=$(dirname "$AIRFLOW_PKG_REQ") numpy

#echo "airflow install"
#OWB_ALLOW_NON_FIPS=on SLUGIFY_USES_TEXT_UNIDECODE=yes python -m pip install -U --no-index --find-links=$(dirname "$AIRFLOW_PKG_REQ") -r $AIRFLOW_PKG_REQ
#if [[ $NEW_RPM_VERSION == *"11.4"* ]] && [[ $OLD_RPM_VERSION == *"11.3"* ]] ; then
	#echo "running airflow upgradedb"
	#OWB_ALLOW_NON_FIPS=on && sudo -E /var/netwitness/presidio/airflow/venv/bin/airflow upgradedb
#fi
#deactivate
echo "######################################## Initiate Airflow #######################################"
sed -i 's!from hashlib import md5!from hashlib import sha256!g' /var/netwitness/presidio/airflow/venv/lib/python2.7/site-packages/werkzeug/contrib/cache.py
sed -i 's!md5(key).hexdigest()!sha256(key).hexdigest()!g' /var/netwitness/presidio/airflow/venv/lib/python2.7/site-packages/werkzeug/contrib/cache.py
flask_login='/var/netwitness/presidio/airflow/venv/lib/python2.7/site-packages/flask_login.py'
#flask_login file removed at 11.4
if [ "$( ls -a $flask_login)" ]; then
	sed -i 's!from hashlib import sha1, md5!from hashlib import sha1, sha256!g' $flask_login
	sed -i 's!h = md5()!h = sha256()!g' $flask_login
fi

echo "### Running install_workflows.sh ###"
bash /var/lib/netwitness/presidio/install/pypackages-install/install_workflows.sh
echo "### Running install_workflows_ext.sh ###"
bash /var/lib/netwitness/presidio/install/pypackages-install/install_workflows_ext.sh

sudo  systemctl daemon-reload
sudo systemctl start airflow-webserver
echo "######################################## IUpdate Services #######################################"

systemctl restart presidio-configserver.service
while ! curl --output /dev/null --silent --head --fail http://localhost:8888/application-default.properties; do sleep 1 && echo -n .; done;
sudo systemctl restart presidio-manager presidio-ui presidio-output

echo "######################################## Initiate-presidio-services.sh completed successfully ########################################" 
