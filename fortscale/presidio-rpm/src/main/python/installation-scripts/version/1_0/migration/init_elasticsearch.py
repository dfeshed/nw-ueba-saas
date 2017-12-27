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
    response = requests.put(url, data=data, headers=HEADERS)
    if response.status_code not in [200,201]:
        msg = "got response={} reason={} content={} data={}".format(response.status_code,response.reason,response.content,data)
        raise Exception(msg)


def create_aliases(folder):
    for indexJson in os.listdir(folder):
        with open(folder + '/' + indexJson) as json_data:
            obj = json.load(json_data)
            data = json.dumps(obj)
            response = requests.post(URL_ALIASES, data=data, headers=HEADERS)
            if response.status_code not in [200,201]:
                msg = "got response={} reason={} content={} data={}".format(response.status_code, response.reason,
                                                                           response.content,data)
                raise Exception(msg)


def create_default_pattern(file):
    try:
        with open(file) as json_data:
            data = json.load(json_data)
            put_request(URL_KIBANA_DEFAULT, json.dumps(data))
    except Exception as e:
        print ("ERROR: failed to send file={} to elastic search url={}".format(file, URL_KIBANA_DEFAULT))
        print(e)


def create_indexes(folder):
    for indexJson in os.listdir(folder):
        try:
            if not SETTING in indexJson:
                name = indexJson.split(".")[0]
                with open(folder + '/' + indexJson) as json_data:
                    obj = json.load(json_data)
                    print("INFO: creating index:" + name)
                    data = json.dumps(dict.values(obj)[0])
                    url = MACHINE_URL + name + '/_mappings/' + dict.keys(obj)[0]
                    put_request(url, data)
        except Exception as e:
            print ("ERROR: failed to send file={} to elastic search url={}".format(indexJson,url))
            print(e)


def update_kibana_index_from_file(folder, url):
    for indexJson in os.listdir(folder):
        jsonfilepath = os.path.join(folder , indexJson)
        name = ''
        try:
            with open(jsonfilepath) as json_data:
                obj = json.load(json_data)
                if(type(obj) is list):
                    for item in obj:
                        data, name = convert_el_item(item)
                        requesturl = (url + name)
                        put_request(requesturl, data)
                else:
                    data, name = convert_el_item(obj)
                    requesturl = (url + name)
                    put_request(requesturl, data)
        except Exception as e:
            print ("ERROR: failed to send file={} to elastic search url={}".format(jsonfilepath,url))
            print(e)


def convert_el_item(item):
    name = str(item["_id"])
    data = json.dumps(item["_source"])
    return data, name

def create_elastic_mapping_from_file(folder, url):
    for indexJson in os.listdir(folder):
        jsonfilepath = os.path.join(folder , indexJson)
        name = ''
        try:
            with open(jsonfilepath) as json_data:
                obj = json.load(json_data)
                name = dict.keys(obj)[0]
                data = json.dumps(dict.values(obj)[0])
                requesturl = (url + name).replace(" ","")
                put_request(requesturl, data)
        except Exception as e:
            print ("ERROR: failed to send file={} to elastic search url={}".format(jsonfilepath,url))
            print(e)


# when creating indexes important to start with the settings than mapping and finish with the aliases
create_elastic_mapping_from_file(SETTINGS, MACHINE_URL)
create_elastic_mapping_from_file(TEMPLATES, URL_TEMPLATES)
create_indexes(INDEXES)
#create_aliases(ALIASES)
update_kibana_index_from_file(INDEX_PATTERN, URL_KIBANA_PATTERNS)
create_default_pattern(DEFAULT)
update_kibana_index_from_file(SEARCHES, URL_KIBANA_SEARCHES)
update_kibana_index_from_file(VISUALIZATION, URL_KIBANA_VISUALIZATIONS)
update_kibana_index_from_file(DASHBOARDS, URL_KIBANA_DASHBOARDS)
