#!/usr/local/bin/python


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
URL_KIBANA_DEFAULT = URL_KIBANA + 'config/5.4.0'
INDEX_PATTERN = ELASTICSEARCH_PATH + '/patterns'
DASHBOARDS = ELASTICSEARCH_PATH + '/dashboards'
SEARCHES = ELASTICSEARCH_PATH + '/searches'
VISUALIZATION = ELASTICSEARCH_PATH + '/visualizations'
INDEXES = ELASTICSEARCH_PATH + '/mappings'
SETTINGS = ELASTICSEARCH_PATH + '/settings'
TEMPLATES = ELASTICSEARCH_PATH + '/templates'
ALIASES = ELASTICSEARCH_PATH + '/aliases'
DEFAULT = ELASTICSEARCH_PATH + '/default/kibana-default-pattern.json'
HEADERS = {"Content-Type": "application/json"}
URL_ALIASES = MACHINE_URL + "_aliases"
URL_TEMPLATES = MACHINE_URL + "_template/"
SETTING = "settings"


def put_request(url, data):
    print(url)
    print(data)
    responce = requests.put(url, data=data, headers=HEADERS)
    print(responce)
    return;


def create_aliases(folder):
    for indexJson in os.listdir(folder):
        with open(folder + '/' + indexJson) as json_data:
            obj = json.load(json_data)
            data = json.dumps(obj)
            print(URL_ALIASES)
            print(data)
            responce = requests.post(URL_ALIASES, data=data, headers=HEADERS)
            print(responce)
    return;


def create_default_pattern(file):
    with open(file) as json_data:
        data = json.load(json_data)
        put_request(URL_KIBANA_DEFAULT, json.dumps(data))
    return;


def create_indexes(folder):
    for indexJson in os.listdir(folder):
        if not SETTING in indexJson:
            name = indexJson.split(".")[0]
            with open(folder + '/' + indexJson) as json_data:
                obj = json.load(json_data)
                print(name)
                data = json.dumps(dict.values(obj)[0])
                url = MACHINE_URL + name + '/_mappings/' + dict.keys(obj)[0]
                put_request(url, data)
    return;


def send_request_to_elastic_from_file(folder, url):
    for indexJson in os.listdir(folder):
        with open(folder + '/' + indexJson) as json_data:
            obj = json.load(json_data)
            name = dict.keys(obj)[0]
            data = json.dumps(dict.values(obj)[0])
            put_request(url + name, data)
    return;


# when creating indexes important to start with the settings than mapping and finish with the aliases
send_request_to_elastic_from_file(SETTINGS, MACHINE_URL)
send_request_to_elastic_from_file(TEMPLATES, URL_TEMPLATES)
create_indexes(INDEXES)
#create_aliases(ALIASES)
send_request_to_elastic_from_file(INDEX_PATTERN, URL_KIBANA_PATTERNS)
create_default_pattern(DEFAULT)
send_request_to_elastic_from_file(SEARCHES, URL_KIBANA_SEARCHES)
send_request_to_elastic_from_file(VISUALIZATION, URL_KIBANA_VISUALIZATIONS)
send_request_to_elastic_from_file(DASHBOARDS, URL_KIBANA_DASHBOARDS)
