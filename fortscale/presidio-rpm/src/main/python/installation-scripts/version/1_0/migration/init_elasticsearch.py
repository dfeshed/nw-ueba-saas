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
INDEXES = ELASTICSEARCH_PATH + '/indexes'
DEFAULT = ELASTICSEARCH_PATH + '/default/kibana-default-pattern.json'
HEADERS = {"Content-Type": "application/json"}
URL_ALIASES = MACHINE_URL + "_aliases"
URL_TEMPLATES = MACHINE_URL + "_template/"
SETTING = 'settings'
INDEX = 'index'
ID = "_id"
SOURCE = "_source"
MAPPINGS_FILE_NAME = 'mappings.json'
SETTINGS_FILE_NAME = 'settings.json'
ALIASES_FILE_NAME = 'aliases.json'
TEMPLATE_FILE_NAME = 'template.json'


def put_request(url, data):
    response = requests.put(url, data=data, headers=HEADERS)
    if response.status_code not in [200, 201]:
        msg = "got response={} reason={} content={} data={}".format(response.status_code, response.reason,
                                                                    response.content, data)
        raise Exception(msg)


def create_aliases(indexJson):
    with open(indexJson) as json_data:
        obj = json.load(json_data)
        data = json.dumps(obj)
        response = requests.post(URL_ALIASES, data=data, headers=HEADERS)
        if response.status_code not in [200, 201]:
            msg = "got response={} reason={} content={} data={}".format(response.status_code, response.reason,
                                                                        response.content, data)
            raise Exception(msg)


def create_default_pattern(file):
    try:
        with open(file) as json_data:
            data = json.load(json_data)
            put_request(URL_KIBANA_DEFAULT, json.dumps(data))
    except Exception as e:
        print ("ERROR: failed to send file={} to elastic search url={}".format(file, URL_KIBANA_DEFAULT))
        print(e)


def create_indexes(indexJson, name):
    try:
        with open(indexJson) as json_data:
            obj = json.load(json_data)
            print("INFO: creating index:" + name)
            data = json.dumps(dict.values(obj)[0])
            url = MACHINE_URL + name + '/_mappings/' + dict.keys(obj)[0]
            put_request(url, data)
    except Exception as e:
        print ("ERROR: failed to send file={} to elastic search url={}".format(indexJson, url))
        print(e)


def update_kibana_index_from_file(folder, url):
    for indexJson in os.listdir(folder):
        jsonfilepath = os.path.join(folder, indexJson)
        name = ''
        try:
            with open(jsonfilepath) as json_data:
                obj = json.load(json_data)
                if (type(obj) is list):
                    for item in obj:
                        data, name = convert_el_item(item)
                        requesturl = (url + name)
                        put_request(requesturl, data)
                else:
                    data, name = convert_el_item(obj)
                    requesturl = (url + name)
                    put_request(requesturl, data)
        except Exception as e:
            print ("ERROR: failed to send file={} to elastic search url={}".format(jsonfilepath, url))
            print(e)


def convert_el_item(item):
    name = str(item[ID])
    data = json.dumps(item[SOURCE])
    return data, name


def create_elasticsearch_settings_for_index(file):
    if not bool(file):
        return {}
    with open(file) as json_data:
        obj = json.load(json_data)
    index = {INDEX: obj}
    settings = {SETTING: index}
    return json.dumps(settings)


def create_elasticsearch_index(file, name):
    try:
        requesturl = (MACHINE_URL + name).replace(" ", "")
        data = create_elasticsearch_settings_for_index(file)
        put_request(requesturl, data)
        return True
    except Exception as e:
        print ("ERROR: failed to send file={} to elastic search url={}".format(file, requesturl))
        print(e)
        return False


def create_index_with_settings(path, name):
    file = os.path.join(path, SETTINGS_FILE_NAME)
    if os.path.isfile(file):
        return create_elasticsearch_index(file, name)
    else:
        return create_elasticsearch_index({}, name)


def set_mappings_for_index(path, name):
    file = os.path.join(path, MAPPINGS_FILE_NAME)
    if os.path.isfile(file):
        create_indexes(file, name)
    else:
        print ('missing mappings for ' + path)


def set_aliases_for_index(path):
    file = os.path.join(path, ALIASES_FILE_NAME)
    if os.path.isfile(file):
        create_aliases(file)
    else:
        print ('No aliases for' + path)


def create_index_from_template(file):
    try:
        with open(file) as json_data:
            obj = json.load(json_data)
            name = dict.keys(obj)[0]
            data = json.dumps(dict.values(obj)[0])
            requesturl = (URL_TEMPLATES + name).replace(" ", "")
            put_request(requesturl, data)
    except Exception as e:
        print ("ERROR: failed to send file={} to elastic search url={}".format(file, URL_TEMPLATES))
        print(e)


def create_index_by_order(path, name):
    if create_index_with_settings(path, name):
        set_mappings_for_index(path, name)
        set_aliases_for_index(path)
    else:
        print ("ERROR: failed to create index = {}".format(name))


def init_elasticsearch(path):
    for subfolder in os.listdir(path):
        newpath = os.path.join(path, subfolder)
        if os.path.isfile(os.path.join(newpath, TEMPLATE_FILE_NAME)):
            create_index_from_template(os.path.join(newpath, TEMPLATE_FILE_NAME))
        else:
            create_index_by_order(newpath, subfolder)


init_elasticsearch(INDEXES)
update_kibana_index_from_file(INDEX_PATTERN, URL_KIBANA_PATTERNS)
create_default_pattern(DEFAULT)
update_kibana_index_from_file(SEARCHES, URL_KIBANA_SEARCHES)
update_kibana_index_from_file(VISUALIZATION, URL_KIBANA_VISUALIZATIONS)
update_kibana_index_from_file(DASHBOARDS, URL_KIBANA_DASHBOARDS)
