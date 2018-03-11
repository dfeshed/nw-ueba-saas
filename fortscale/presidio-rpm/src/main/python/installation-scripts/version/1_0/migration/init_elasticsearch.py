#!/usr/local/bin/python


import argparse
import ast
import json
import logging
import os
import requests
import string

BASE_PATH = '/home/presidio/presidio-core/'
ELASTICSEARCH_PATH = BASE_PATH + 'el-extensions'
VENDOR_ELASTICSEARCH_PATH = BASE_PATH + 'el-extensions-vendor'
MACHINE_URL = 'http://localhost:9200/'
URL_KIBANA = MACHINE_URL + '.kibana/'
URL_KIBANA_PATTERNS = URL_KIBANA + 'index-pattern/'
URL_KIBANA_DASHBOARDS = URL_KIBANA + 'dashboard/'
URL_KIBANA_SEARCHES = URL_KIBANA + 'search/'
URL_KIBANA_VISUALIZATIONS = URL_KIBANA + 'visualization/'
URL_KIBANA_DEFAULT = URL_KIBANA + 'config/5.4.0'
INDEX_PATTERN = '/patterns'
DASHBOARDS = '/dashboards'
SEARCHES = '/searches'
VISUALIZATION = '/visualizations'
INDEXES = '/indexes'
DEFAULT = '/default/kibana-default-pattern.json'
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
CORE_ELASTIC_INIT = 'core'
VENDOR_ELASTIC_INIT = 'vendor'
ELASTICSEARCH_TEMPLATE_NAME_POSITION_IN_TEMPLATE = 0
INDEX_ALREADY_EXISTS_EXCEPTION = 'index_already_exists_exception'
EVENT_TIME = {"presidio-monitoring": "timestamp", "presidio-monitoring-logical": "logicTime",
              "presidio-output-alert": "startDate",
              "presidio-output-event": "eventTime", "presidio-output-indicator": "startDate",
              "presidio-monitoring": "timestemp", "presidio-output-logical": "logicTime",
              "presidio-output-user": "createdDate"}


def put_request(url, data):
    logging.info("executing put request %s ", url)
    response = requests.put(url, data=data, headers=HEADERS)
    if response.status_code not in [200, 201]:
        content = ast.literal_eval(response.content)
        if content['error']['type'] != INDEX_ALREADY_EXISTS_EXCEPTION:
            msg = "got response={} reason={} content={} data={}".format(response.status_code, response.reason,
                                                                        response.content, data)
            raise Exception(msg)
        else:
            logging.info("Index = %s already exists.", url)
    return response


def set_alias(indexJson):
    with open(indexJson) as json_data:
        obj = json.load(json_data)
        data = json.dumps(obj)
        logging.info("executing post request %s ", URL_ALIASES)
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
        logging.error("failed to send file=%s to elastic search url=%s", file, URL_KIBANA_DEFAULT)
        raise e


def set_mapping(indexJson, name):
    try:
        with open(indexJson) as json_data:
            obj = json.load(json_data)
            data = json.dumps(obj[obj.keys()[0]])
            if name != "presidio-output-user-severities-range":
                create_kibana_pattern_from_mapping(name, obj[obj.keys()[0]]["properties"])
            url = MACHINE_URL + name + '/_mappings/' + obj.keys()[0]
            put_request(url, data)
            logging.info("Set index %s mappings", name)
    except Exception as e:
        logging.error("failed to send file=%s to elastic search url=%s", indexJson, url)
        raise e


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
            logging.error("failed to send file=%s to elastic search url=%s", jsonfilepath, url)
            raise e


def fields_from_property(name, dic):
    field = {}
    if dic.has_key("type"):
        type = str(dic["type"])
        field = {"name": name, "type": type, "count": 0, "scripted": False, "indexed": True, "analyzed": False,
                 "doc_values": True, "searchable": True, "aggregatable": True}
        if type == "string" and (dic.has_key("analyzer") or not dic.has_key("index")):
            field = {"name": name, "type": type, "count": 0, "scripted": False, "indexed": True, "analyzed": True,
                     "doc_values": False, "searchable": True, "aggregatable": False}
    return field


def enter_field_to_list(dic, field):
    if len(field) > 0:
        dic.append(field)


def create_kibana_pattern_from_mapping(name, mapping):
    SOURCE = {"name": "_source", "type": "_source", "count": 0, "scripted": False, "indexed": False,
              "analyzed": False, "doc_values": False, "searchable": False, "aggregatable": False}
    ID = {"name": "_id", "type": "string", "count": 0, "scripted": False, "indexed": False, "analyzed": False,
          "doc_values": False, "searchable": False, "aggregatable": False}
    TYPE = {"name": "_type", "type": "string", "count": 0, "scripted": False, "indexed": False, "analyzed": False,
            "doc_values": False, "searchable": False, "aggregatable": False}
    INDEX = {"name": "_index", "type": "string", "count": 0, "scripted": False, "indexed": False,
             "analyzed": False, "doc_values": False, "searchable": False, "aggregatable": False}
    SCORE = {"name": "_score", "type": "number", "count": 0, "scripted": False, "indexed": False,
             "analyzed": False, "doc_values": False, "searchable": False, "aggregatable": False}
    fields = [SOURCE, ID, INDEX, SCORE, TYPE]
    for property in mapping:
        if (type(property) is dict):
            for property in mapping:
                enter_field_to_list(fields, fields_from_property(property, mapping[property]))
        else:
            enter_field_to_list(fields, fields_from_property(property, mapping[property]))
    fields_as_string = str(fields)
    fields_as_string_double_quote = string.replace(fields_as_string, "'", "\"")
    put_request(URL_KIBANA_PATTERNS + name,
                json.dumps({"title": name, "timeFieldName": EVENT_TIME[name],
                            "fields": fields_as_string_double_quote}))


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
        logging.info("creating index: %s ", name)
    except Exception as e:
        logging.error("failed to send file=%s to elastic search url=%s", file, requesturl)
        raise e


def create_index_with_settings(path, name):
    file = os.path.join(path, SETTINGS_FILE_NAME)
    if os.path.isfile(file):
        create_elasticsearch_index(file, name)
    else:
        create_elasticsearch_index({}, name)


def set_mappings_for_index(path, name):
    file = os.path.join(path, MAPPINGS_FILE_NAME)
    if os.path.isfile(file):
        set_mapping(file, name)
    else:
        msg = "Missing mappings for={}".format(path)
        raise Exception(msg)


def set_aliases_for_index(path):
    file = os.path.join(path, ALIASES_FILE_NAME)
    if os.path.isfile(file):
        set_alias(file)
    else:
        logging.info("No aliases for %s", path)


def create_pattern_from_template(aliases, mappings):
    for alias in aliases:
        create_kibana_pattern_from_mapping(alias, mappings)


def create_index_from_template(file):
    try:
        with open(file) as json_data:
            obj = json.load(json_data)
            name = obj.keys()[ELASTICSEARCH_TEMPLATE_NAME_POSITION_IN_TEMPLATE]
            data = json.dumps(obj[obj.keys()[ELASTICSEARCH_TEMPLATE_NAME_POSITION_IN_TEMPLATE]])
            create_pattern_from_template(obj[obj.keys()[0]]["aliases"],
                                         obj[obj.keys()[0]]["mappings"]["metric"]["properties"])
            requesturl = (URL_TEMPLATES + name).replace(" ", "")
            put_request(requesturl, data)
    except Exception as e:
        logging.error("failed to send file=%s to elastic search url=%s", file, URL_TEMPLATES)
        raise e


def create_index_by_order(path, name):
    try:
        create_index_with_settings(path, name)
        set_mappings_for_index(path, name)
        set_aliases_for_index(path)
    except Exception as e:
        logging.error("Failed to create index = %s.", name)
        raise e


def init_elasticsearch(path):
    for subfolder in os.listdir(path):
        newpath = os.path.join(path, subfolder)
        if os.path.isfile(os.path.join(newpath, TEMPLATE_FILE_NAME)):
            create_index_from_template(os.path.join(newpath, TEMPLATE_FILE_NAME))
        else:
            create_index_by_order(newpath, subfolder)


def main(path, rpm):
    if rpm == CORE_ELASTIC_INIT:
        init_elasticsearch(path + INDEXES)
        update_kibana_index_from_file(path + INDEX_PATTERN, URL_KIBANA_PATTERNS)
        create_default_pattern(path + DEFAULT)
    update_kibana_index_from_file(path + SEARCHES, URL_KIBANA_SEARCHES)
    update_kibana_index_from_file(path + VISUALIZATION, URL_KIBANA_VISUALIZATIONS)
    update_kibana_index_from_file(path + DASHBOARDS, URL_KIBANA_DASHBOARDS)


parser = argparse.ArgumentParser(description='init elasticseatch and kibana')
parser.add_argument('--resources_path', type=str, required=True,
                    help='path of resources files for elasticsearch and kibana')
parser.add_argument('--run_type', type=str, required=True, help='core/vendor')
args = parser.parse_args()

if __name__ == "__main__":
    if args.run_type in [CORE_ELASTIC_INIT, VENDOR_ELASTIC_INIT]:
        main(args.resources_path, args.run_type)
    else:
        msg = "Bad param={} ".format(args.run_type)
        raise Exception(msg)
