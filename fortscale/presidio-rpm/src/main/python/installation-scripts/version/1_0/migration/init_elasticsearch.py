#!/usr/local/bin/python


import json
import os
import requests
import sys
import logging
import argparse
import ast

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


def put_request(url, data):
    logging.info("executing put request %s ", url)
    response = requests.put(url, data=data, headers=HEADERS)
    if response.status_code not in [200, 201]:
        content =ast.literal_eval(response.content)
        if content['error']['type'] != INDEX_ALREADY_EXISTS_EXCEPTION:
            msg = "got response={} reason={} content={} data={}".format(response.status_code, response.reason,
                                                                        response.content, data)
            raise Exception(msg)
        else:
            logging.info("Index = %s already exists.", url)


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
            url = MACHINE_URL + name + '/_mappings/' +  obj.keys()[0]
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


def create_index_from_template(file):
    try:
        with open(file) as json_data:
            obj = json.load(json_data)
            name = obj.keys()[ELASTICSEARCH_TEMPLATE_NAME_POSITION_IN_TEMPLATE]
            data = json.dumps(obj[obj.keys()[ELASTICSEARCH_TEMPLATE_NAME_POSITION_IN_TEMPLATE]])
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
    if rpm is CORE_ELASTIC_INIT:
        init_elasticsearch(path + INDEXES)
        update_kibana_index_from_file(path + INDEX_PATTERN, URL_KIBANA_PATTERNS)
        create_default_pattern(path + DEFAULT)
    update_kibana_index_from_file(path + SEARCHES, URL_KIBANA_SEARCHES)
    update_kibana_index_from_file(path + VISUALIZATION, URL_KIBANA_VISUALIZATIONS)
    update_kibana_index_from_file(path + DASHBOARDS, URL_KIBANA_DASHBOARDS)


parser = argparse.ArgumentParser(description='init elasticseatch and kibana')
parser.add_argument('--resources_path', type=str, required=True,help='path of resources files for elasticsearch and kibana (core/vendor)')
args = parser.parse_args()


if __name__ == "__main__":
    if args.resources_path == CORE_ELASTIC_INIT:
        logging.info("Running core logic.")
        main(ELASTICSEARCH_PATH, CORE_ELASTIC_INIT)
    if args.resources_path == VENDOR_ELASTIC_INIT:
        logging.info("Running specific vendor %s logic.", VENDOR_ELASTIC_INIT)
        main(VENDOR_ELASTICSEARCH_PATH, VENDOR_ELASTIC_INIT)
