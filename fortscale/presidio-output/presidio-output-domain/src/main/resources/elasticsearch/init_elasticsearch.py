import json
import os
import requests

path = '/home/presidio/elasticsearch'
url = 'http://localhost:9200/'
url_kibana = 'http://localhost:9200/.kibana/index-pattern/'
url_dashboards = 'http://localhost:9200/.kibana/dashboard/'
for indexJson in os.listdir(path + '/indexs'):
    name = indexJson.split(".")[0]
    with open(path + '/indexs/' + indexJson) as json_data:
        index = json.load(json_data)
        tempJson = json.dumps({"mappings": index})
        responce = requests.put(url + name, data=tempJson, headers={"Content-Type": "application/json"})
for indexJson in os.listdir(path + '/index-pattern'):
    name = indexJson.split(".")[0]
    with open(path + '/index-pattern/' + indexJson) as json_data:
        index = json.load(json_data)
        tempJson = json.dumps({"mappings": index})
        responce = requests.put(url_kibana + name, data=tempJson, headers={"Content-Type": "application/json"})
for indexJson in os.listdir(path + '/dashboard'):
    name = indexJson.split(".")[0]
    with open(path + '/dashboard/' + indexJson) as json_data:
        index = json.load(json_data)
        tempJson = json.dumps({"mappings": index})
        responce = requests.put(url_dashboards + name, data=tempJson, headers={"Content-Type": "application/json"})
