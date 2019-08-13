import os
from elasticsearch import Elasticsearch
from presidio.utils.airflow.upgrade_utils import run_reset_presidio_for_upgrade, get_dags_ids_by_prefix
from presidio.builders.rerun_ueba_flow_dag_builder import get_registered_presidio_dags, pause_dags, kill_dags_task_instances, \
    cleanup_dags_from_postgres, get_airflow_log_folders

# clean old full flow- logs and postgres
full_flow_dag_ids_to_clean = get_dags_ids_by_prefix("full_flow")
cleanup_dags_from_postgres(full_flow_dag_ids_to_clean)
airflow_log_folders_list = get_airflow_log_folders(full_flow_dag_ids_to_clean)
airflow_log_folder_str = ' '.join(airflow_log_folders_list)
os.system("rm -rf {}".format(airflow_log_folder_str))

# pause and kill tasks for new dags
dags = get_registered_presidio_dags()
dag_ids = map(lambda x: x.dag_id, dags)
pause_dags(dag_ids)
kill_dags_task_instances(dag_ids)


# Change elastic indexes that will be compatible with 11.4.0.0:
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

# Run reset_presidio dag for upgrade
run_reset_presidio_for_upgrade()