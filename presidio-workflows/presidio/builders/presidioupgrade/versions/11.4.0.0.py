import json
import requests
import time
import urllib
from datetime import datetime, timedelta
from elasticsearch import Elasticsearch

# Set the execution date of the "Reset Presidio" DAG run to the current system time.
exec_date = datetime.utcnow()
# Do not clean the Elasticsearch data:
# "Reset Presidio" should clean all the data, EXCEPT for the Alerts that were already created.
clean_data = False
# Reset the start time to 27 days before the current system time, at the beginning of the day:
# Since there shouldn't be duplicate Alerts, every logical hour that was already processed should be skipped, including
# hours of the current system day. Presidio starts triggering Alerts 28 logical days after the start time. So if the
# start time is reset to 27 days before the current system time, the first Alerts Presidio triggers will be of the
# following day (i.e. of tomorrow). This means that there will be a maximum of 24 hours without Alerts (those of the
# current system day), which is acceptable by Product.
start_time = exec_date - timedelta(days=27)

exec_date = exec_date.strftime("%Y-%m-%dT%H:%M:%S")
start_time = start_time.strftime("%Y-%m-%dT00:00:00Z")

conf = {
    "elasticsearch": {"cleanData": clean_data},
    "presidio": {"dataPipeline": {"startTime": start_time}}
}

query = {
    "api": "trigger_dag",
    "dag_id": "reset_presidio",
    "exec_date": exec_date,
    "conf": json.dumps(conf, separators=(",", ":"))
}

response = requests.get("http://localhost:8100/admin/rest_api/api?%s" % urllib.urlencode(query))

if response.status_code != 200:
    raise ValueError("Get request to trigger a 'Reset Presidio' DAG run failed.")

# Wait for the "Reset Presidio" DAG run to finish.
url = "http://localhost:8100/admin/rest_api/api?%s" % urllib.urlencode({
    "api": "dag_state",
    "dag_id": "reset_presidio",
    "execution_date": exec_date
})

while True:
    time.sleep(60)
    response = requests.get(url)
    state = response.json()["output"]["stdout"].strip()

    if state == "success":
        break
    elif state == "failed":
        raise ValueError("The triggered 'Reset Presidio' DAG run failed.")
    elif state != "running":
        raise ValueError("The triggered 'Reset Presidio' DAG run is in an unknown state (%s)." % state)

# Change elastic indexes that will be compatible with 11.4.0:

INDEX_USER = "presidio-output-user"
DOC_TYPE_USER = "user"
INDEX_ENTITY = "presidio-output-entity"
DOC_TYPE_ENTITY = "entity"
SIZE = 1000
ENTITY_TYPE = "userId"
INDEX_ALERT = "presidio-output-alert"
DOC_TYPE_ALERT = "alert"
INDEX_USER_SEVERITY_RANGE = "presidio-output-user-severities-range"
INDEX_ENTITY_SEVERITY_RANGE = "presidio-output-entity-severities-range"
DOC_TYPE_USER_SEVERITY_RANGE = "user-severities-range"
DOC_TYPE_ENTITY_SEVERITY_RANGE = "entity-severities-range"


# # Init Elasticsearch instance
es = Elasticsearch()


# Convert users table to entities table
def convert_users_to_entities(hits):
    for item in hits:
        entity = {
                'createdDate': item["_source"]["createdDate"],
                'updatedDate': item["_source"]["updatedDate"],
                'updatedBy': item["_source"]["updatedBy"],
                'entityId': item["_source"]["userId"],
                'entityName': item["_source"]["userName"],
                'score': item["_source"]["score"],
                'alertClassifications': item["_source"]["alertClassifications"],
                'indicators': item["_source"]["indicators"],
                'severity': item["_source"]["severity"],
                'tags': item["_source"]["tags"],
                'alertsCount': item["_source"]["alertsCount"],
                'lastUpdateLogicalStartDate': item["_source"]["updatedByLogicalStartDate"],
                'lastUpdateLogicalEndDate': item["_source"]["updatedByLogicalEndDate"],
                'entityType': ENTITY_TYPE
        }

        es.index(index=INDEX_ENTITY, doc_type=DOC_TYPE_ENTITY, id=item["_id"], body=entity)


# Update the alert table in elastic with the new field names
def update_alerts_hits(hits):
    for item in hits:
        alert = {
            'createdDate': item["_source"]["createdDate"],
            'updatedDate': item["_source"]["updatedDate"],
            'updatedBy': item["_source"]["updatedBy"],
            'classifications': item["_source"]["classifications"],
            'entityName': item["_source"]["userName"],
            'indexedEntityName': item["_source"]["indexedUserName"],
            'smartId': item["_source"]["smartId"],
            'entityDocumentId': item["_source"]["userId"],
            'startDate': item["_source"]["startDate"],
            'endDate': item["_source"]["endDate"],
            'score': item["_source"]["score"],
            'indicatorsNum': item["_source"]["indicatorsNum"],
            'timeframe': item["_source"]["timeframe"],
            'severity': item["_source"]["severity"],
            'indicatorsNames': item["_source"]["indicatorsNames"],
            'entityTags': item["_source"]["userTags"],
            'contributionToEntityScore': item["_source"]["contributionToUserScore"],
            'feedback': item["_source"]["feedback"],
            'entityType': ENTITY_TYPE

        }

        es.update(index=INDEX_ALERT, doc_type=DOC_TYPE_ALERT, id=item["_id"], body=dict(alert))


def scroll_and_update_data(index, doc_type, update_function):
    # Check index is exists
    if not es.indices.exists(index=index):
        print("Index {} not exists".format(index))
        exit()

    # Init scroll by search
    data = es.search(
        index=index,
        doc_type=doc_type,
        scroll='2m',
        size=SIZE
    )

    # Get the scroll ID
    sid = data['_scroll_id']
    scroll_size = len(data['hits']['hits'])

    # Before scroll, process current batch of hits
    update_function(data['hits']['hits'])

    while scroll_size > 0:
        data = es.scroll(scroll_id=sid, scroll='2m')

        # Process current batch of hits
        update_function(data['hits']['hits'])

        # Update the scroll ID
        sid = data['_scroll_id']

        # Get the number of results that returned in the last scroll
        scroll_size = len(data['hits']['hits'])


# Scrolling users
scroll_and_update_data(INDEX_USER, DOC_TYPE_USER, convert_users_to_entities)

# Remove user index
es.indices.delete(index=INDEX_USER)

# Scrolling alerts
scroll_and_update_data(INDEX_ALERT, DOC_TYPE_ALERT, update_alerts_hits)

doc = es.get(index=INDEX_USER_SEVERITY_RANGE, doc_type=DOC_TYPE_USER_SEVERITY_RANGE, id='user-severities-range-doc-id')
doc["_source"]["id"] = 'userId-severities-range-doc-id'
es.index(index=INDEX_ENTITY_SEVERITY_RANGE, doc_type=DOC_TYPE_ENTITY_SEVERITY_RANGE,
                id='userId-severities-range-doc-id', body=dict(doc["_source"]))

# Remove user severity range index
es.indices.delete(index=INDEX_USER_SEVERITY_RANGE)

