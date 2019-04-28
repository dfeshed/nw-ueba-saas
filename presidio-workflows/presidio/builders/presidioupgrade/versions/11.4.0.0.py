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


def process_alerts_hits(hits):
    for item in hits:
        alert = {
        }
        es.update(index=index_alert, doc_type=doc_type_alert, id=item["_id"], body=alert)


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
    doc_type=index_alert,
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