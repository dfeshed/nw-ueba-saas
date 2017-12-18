#!/usr/local/bin/python

import json
import os
import requests

MACHINE_URL = 'http://localhost:9200/'
URL_KIBANA = MACHINE_URL + '.kibana/'
URL_KIBANA_PATTERNS = URL_KIBANA + 'index-pattern/'
URL_KIBANA_DASHBOARDS = URL_KIBANA + 'dashboard/'
URL_KIBANA_SEARCHES = URL_KIBANA + 'search/'
URL_KIBANA_VISUALIZATIONS = URL_KIBANA + 'visualization/'
URL_KIBANA_DEFAULT = URL_KIBANA + '/config/5.4.0'
INDEX_PATTERN = '/patterns'
DASHBOARDS = '/dashboards'
SEARCHES = '/searches'
VISUALIZATION = '/visualizations'
INDEXES = '/mappings'
SETTINGS = '/settings'
SETTING = 'settings'
ALIASES = '/aliases'
DEFAULT = '/default'
HEADERS = {"Content-Type": "application/json"}
MAPPINGS = "mappings"
URL_ALIASES = MACHINE_URL + "_aliases"
ELASTICSEARCH_PATH = '/home/presidio/presidio-core/el-extensions'


def elastic_put_request(directory, url):
    path = ELASTICSEARCH_PATH + directory
    for indexJson in os.listdir(path):
        name = indexJson.split(".")[0]
        print (url + name)
        if not SETTING in name:
            with open(path + '/' + indexJson) as json_data:
                index = json.load(json_data)
                if MAPPINGS in path:
                    name = name + '/_mappings/' + dict.keys(index)[0]
                    index = json.dumps(dict.values(index)[0])
                else:
                    index = json.dumps(index)
                if ALIASES in path:
                    responce = requests.post(url, data=index, headers=HEADERS)
                    print(url)
                else:
                    responce = requests.put(url + name, data=index, headers=HEADERS)
                    print (url + name)
                print(index)
                print(responce)
    return;


def create_dashboard(directory, url):
    path = ELASTICSEARCH_PATH + directory
    for indexJson in os.listdir(path):
        with open(path + '/' + indexJson) as json_data:
            index = json.load(json_data)
            name = dict.values(index)[0]
            index = json.dumps(dict.values(index)[1])
            responce = requests.put(url + name, data=index, headers=HEADERS)
            print (url + name)
            print(index)
            print(responce)
    return;


# when creating indexes importent to start with the settings than mapping and finish with the aliases
elastic_put_request(SETTINGS, MACHINE_URL)
elastic_put_request(INDEXES, MACHINE_URL)
elastic_put_request(ALIASES, URL_ALIASES)
elastic_put_request(INDEX_PATTERN, URL_KIBANA_PATTERNS)
elastic_put_request(DEFAULT, URL_KIBANA_DEFAULT)
create_dashboard(DASHBOARDS, URL_KIBANA_DASHBOARDS)
create_dashboard(SEARCHES, URL_KIBANA_SEARCHES)
create_dashboard(VISUALIZATION, URL_KIBANA_VISUALIZATIONS)
