from elasticsearch import Elasticsearch


index_user = "presidio-output-user"
doc_type_user = "user"
index_entity = "presidio-output-entity"
doc_type_entity = "entity"
size = 1000
entity_type = "userId"
index_alert = "presidio-output-alert"
doc_type_alert = "alert"


# # Init Elasticsearch instance
es = Elasticsearch()


# Process hits - convert users table to generic one
def process_users_hits(hits):
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
                'entityType': entity_type
        }

        es.index(index=index_entity, doc_type=doc_type_entity, id=item["_id"], body=entity)


# Convert alert table to generic one that include
def process_alerts_hits(hits):
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
            'entityType': entity_type

        }
        es.update(index=index_alert, doc_type=doc_type_alert, id=item["_id"], body={"doc":alert})


# Check user index is exists
if not es.indices.exists(index=index_user):
    print("Index {} not exists".format(index_user))
    exit()

# Init scroll by search
data = es.search(
    index=index_user,
    doc_type=doc_type_user,
    scroll='2m',
    size=size
)

# Get the scroll ID
sid = data['_scroll_id']
scroll_size = len(data['hits']['hits'])


# Before scroll, process current batch of hits
process_users_hits(data['hits']['hits'])

while scroll_size > 0:
    "Scrolling..."
    data = es.scroll(scroll_id=sid, scroll='2m')

    # Process current batch of hits
    process_users_hits(data['hits']['hits'])

    # Update the scroll ID
    sid = data['_scroll_id']

    # Get the number of results that returned in the last scroll
    scroll_size = len(data['hits']['hits'])


# Check alert index is exists
if not es.indices.exists(index=index_alert):
    print("Index {} not exists".format(index_alert))
    exit()

# Init scroll by search
data = es.search(
    index=index_alert,
    doc_type=doc_type_alert,
    scroll='2m',
    size=size
)


# Before scroll, process current batch of hits
process_alerts_hits(data['hits']['hits'])

while scroll_size > 0:
    "Scrolling..."
    data = es.scroll(scroll_id=sid, scroll='2m')

    # Process current batch of hits
    process_alerts_hits(data['hits']['hits'])

    # Update the scroll ID
    sid = data['_scroll_id']

    # Get the number of results that returned in the last scroll
    scroll_size = len(data['hits']['hits'])