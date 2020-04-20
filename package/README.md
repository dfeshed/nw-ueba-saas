# Presidio Netwitness Package

## Installation

```sh
yum -y install rsa-nw-presidio-ext-netwitness

echo "stoping airflow services"
systemctl stop airflow-webserver
systemctl stop airflow-scheduler

# Install presidio workflows py package
su presidio
bash /var/lib/netwitness/presidio/install/pypackages-install/install_workflows_ext.sh

su
echo "starting airflow services"
systemctl start airflow-webserver
systemctl start airflow-scheduler
```