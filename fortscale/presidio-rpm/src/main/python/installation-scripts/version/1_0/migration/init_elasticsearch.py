import json
import os
import requests

ELASTICSEARCH_PATH = '/home/presidio/elastisearch'
MACHINE_URL = 'http://localhost:9200/'
URL_KIBANA = MACHINE_URL+'.kibana/index-pattern/'
INDEX_PATTERN =ELASTICSEARCH_PATH + '/index-pattern'
DASHBOARD = ELASTICSEARCH_PATH + '/dashboard'
INDEXES = ELASTICSEARCH_PATH+ '/indexs'
HEADERS={"Content-Type": "application/json"}


def elastic_put_request(folder , url):
    for indexJson in os.listdir(folder):
        name = indexJson.split(".")[0]
        with open(folder + '/' + indexJson) as json_data:
            index = json.load(json_data)
            if "mapping" in name:
                index = json.dumps({"mappings": index})
                name = indexJson.split("_")[0]
            else:
                index = json.dumps(index)
            requests.put(url + name, data=index, headers=HEADERS)
    return;


elastic_put_request(INDEXES , MACHINE_URL)
elastic_put_request(INDEX_PATTERN , URL_KIBANA)
elastic_put_request(DASHBOARD , URL_KIBANA)