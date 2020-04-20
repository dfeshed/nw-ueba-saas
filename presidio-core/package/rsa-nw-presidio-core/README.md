# Presidio core 

## Installation

```sh
# Install presidio-core rpm 
yum -y install rsa-nw-presidio-core

echo "stoping airflow services"
systemctl stop airflow-webserver
systemctl stop airflow-scheduler

# Install presidio workflows py package
bash /var/lib/netwitness/presidio/install/pypackages-install/install_workflows.sh

echo "starting airflow services"
systemctl start airflow-webserver
systemctl start airflow-scheduler
```
