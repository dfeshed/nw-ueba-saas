# Presidio Elasticsearch init script

Presidio requires a set of elasticsearch indexes puplated with index-patterns, dashboards, searches, indexes and visulizations.



### Installation

Presidio elk init requires [Python](https://www.python.org/download/releases/2.7.5/) v2.7.5 to run.

```sh
yum -y install rsa-nw-presidio-elasticsearch-init

source /etc/sysconfig/airflow
source $AIRFLOW_VENV/bin/activate
OWB_ALLOW_NON_FIPS=on python /var/lib/netwitness/presidio/elasticsearch/init/init_elasticsearch.py --resources_path /var/lib/netwitness/presidio/elasticsearch/init/data/ --elasticsearch_url http://localhost:9200/
deactivate
```

### Development

Want to add a kibana dashboard? Great!

Exoport the [saved objects](https://discuss.elastic.co/t/how-to-save-dashboard-as-json-file/24561/2) and place them under the matching directory src/main/resources/data/...

