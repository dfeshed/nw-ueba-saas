import json
import os
import requests

ELASTICSEARCH_PATH = '/home/presidio/presidio-core/el-extensions'
MACHINE_URL = 'http://localhost:9200/'
URL_KIBANA = MACHINE_URL + '.kibana/'
URL_KIBANA_PATTERNS = URL_KIBANA + 'index-pattern/'
URL_KIBANA_DASHBOARDS = URL_KIBANA + 'dashboard/'
URL_KIBANA_SEARCHES = URL_KIBANA + 'search/'
URL_KIBANA_VISUALIZATIONS = URL_KIBANA + 'visualization/'
INDEX_PATTERN = ELASTICSEARCH_PATH + '/patterns'
DASHBOARD = ELASTICSEARCH_PATH + '/dashboards'
SEARCHES = ELASTICSEARCH_PATH + '/searches'
VISUALIZATION = ELASTICSEARCH_PATH + '/visualizations'
INDEXES = ELASTICSEARCH_PATH + '/mappings'
ALIASES = ELASTICSEARCH_PATH + '/aliases'
HEADERS = {"Content-Type": "application/json"}
MAPPINGS = "mappings"
DASHBOARDS = "dashboards"
URL_ALIASES = MACHINE_URL + "_aliases"


def elastic_put_request(folder, url):
    for indexJson in os.listdir(folder):
        name = indexJson.split(".")[0]
        with open(folder + '/' + indexJson) as json_data:
            index = json.load(json_data)
            if MAPPINGS in folder:
                index = json.dumps({MAPPINGS: index})
            else:
                index = json.dumps(index)
            requests.put(url + name, data=index, headers=HEADERS)
    return;


elastic_put_request(INDEXES, MACHINE_URL)
elastic_put_request(INDEX_PATTERN, URL_KIBANA_PATTERNS)
elastic_put_request(DASHBOARD, URL_KIBANA_DASHBOARDS)
elastic_put_request(SEARCHES, URL_KIBANA_SEARCHES)
elastic_put_request(VISUALIZATION, URL_KIBANA_VISUALIZATIONS)
elastic_put_request(ALIASES, URL_ALIASES)
