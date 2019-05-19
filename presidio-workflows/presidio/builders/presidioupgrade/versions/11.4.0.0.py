from elasticsearch import Elasticsearch


INDEX_USER = "presidio-output-user"
DOC_TYPE_USER = "user"
INDEX_ENTITY = "presidio-output-entity"
DOC_TYPE_ENTITY = "entity"
SIZE = 1000
ENTITY_TYPE = "userId"
INDEX_ALERT = "presidio-output-alert"
DOC_TYPE_ALERT = "alert"


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

        es.update(index=INDEX_ALERT, doc_type=DOC_TYPE_ALERT, id=item["_id"], body={"doc":alert})


# Check user index is exists
if not es.indices.exists(index=INDEX_USER):
    print("Index {} not exists".format(INDEX_USER))
    exit()

# Init scroll by search
data = es.search(
    index=INDEX_USER,
    doc_type=DOC_TYPE_USER,
    scroll='2m',
    size=SIZE
)

# Get the scroll ID
sid = data['_scroll_id']
scroll_size = len(data['hits']['hits'])

# Before scroll, process current batch of hits
convert_users_to_entities(data['hits']['hits'])

while scroll_size > 0:
    "Scrolling users..."
    data = es.scroll(scroll_id=sid, scroll='2m')

    # Process current batch of hits
    convert_users_to_entities(data['hits']['hits'])

    # Update the scroll ID
    sid = data['_scroll_id']

    # Get the number of results that returned in the last scroll
    scroll_size = len(data['hits']['hits'])


# Remove user index
es.indices.delete(index=INDEX_USER)

# Check alert index is exists
if not es.indices.exists(index=INDEX_ALERT):
    print("Index {} not exists".format(INDEX_ALERT))
    exit()

# Init scroll by search
data = es.search(
    index=INDEX_ALERT,
    doc_type=DOC_TYPE_ALERT,
    scroll='2m',
    size=SIZE
)

# Get the scroll ID
sid = data['_scroll_id']
scroll_size = len(data['hits']['hits'])

# Before scroll, process current batch of hits
update_alerts_hits(data['hits']['hits'])

while scroll_size > 0:
    "Scrolling alerts..."
    data = es.scroll(scroll_id=sid, scroll='2m')

    # Process current batch of hits
    update_alerts_hits(data['hits']['hits'])

    # Update the scroll ID
    sid = data['_scroll_id']

    # Get the number of results that returned in the last scroll
    scroll_size = len(data['hits']['hits'])
