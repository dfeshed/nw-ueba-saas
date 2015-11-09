# WebSocket Protocol for Security Analytics UI

## Overview
The SA UI client can fetch data from the SA UI server using
[STOMP over WebSockets](http://jmesnil.net/stomp-websocket/doc/). There are two delivery styles for such calls:

1. **RPC:** The client makes a request for data, and the server sends the entire data payload in a single socket
 message response.
1. **Streaming:**  The client makes a request, and the server sends the data incrementally across multiple
socket messages.

The details of these two protocols are discussed below.

## RPC Protocol

### Connecting
1) The UI client requests a connection to the UI server by sending a `CONNECT` STOMP frame:
```text
CONNECT
X-CSRF-TOKEN:..
accept-version:..
heart-beat:..

^@
```

2) The server sends confirmation to the client once the connection has been established:
```text
CONNECTED
version:..
heart-beat:..

^@
```

### Requesting Data
1) The client sends a message to the server in order to subscribe to a destination of interest. As shown in
the sample message below, the message will include an `id` header with a subscription ID, and a `destination` header.
To illustrate, suppose that the client is interested in fetching a list of Incidents.  In that case, the client
would subscribe to the destination `/user/queue/threats/incidents`:
```text
SUBSCRIBE
id:sub-0
destination:/user/queue/threats/incidents

^@
```

2) The client then sends a message to server to request data.  The message body should be the definition of
the request, in JSON syntax, with `id`, `page`, `sort`, `stream` and `filter` properties:
```text
SEND
destination:/ws/threats/incidents
content-length:..

{"id":"req-1","page":{"index":0,"size":300},"sort":[{"field":"created","descending":true}],"stream":{"limit":3000},"filter":[{"field":"created","range":{"from":1446653118808,"to":1446739518808}}]}
^@
```
The properties of the request body are documented in the [request object](#request-object) below.

3) When the server finishes handling the request, the server sends the data to the client in a single response.
```text
MESSAGE
destination:/user/queue/threats/incidents
content-type:application/json;charset=UTF-8
subscription:sub-0
content-length:..

{"code":0,"data":[..],"request":..,"meta":{"total":..}}
^@
```
The [STOMP headers](#stomp-headers) and [Response body](#response-object) are documented below.

4) If the client wishes to fetch another page of data for this request, the client should send another request
using the same subscription and a new request id:
```text
SEND
id:sub-0
destination:/ws/threats/incidents
content-length:..

{"id":"req-2","page":{"index":1,"size":300},"sort":[{"field":"created","descending":true}],"filter":[{"field":"created","range":{"from":1446653118808,"to":1446739518808}}]}
^@
```

5) If the client no longer intends to request any more data for a particular subscription, it should unsubscribe.
For example, if the app has only a single route/screen for fetching incidents and the user leaves that
route/screen, then that might be the appropriate time to unsubscribe:
```text
UNSUBSCRIBE
id:sub-0

^@
```

### Canceling Data Requests
In some scenarios, the client may wish to cancel a data request.  For example, if the user changes the paging or
filter criteria for the requested data while the client is still loading data, then the client should cancel the
initial request and submit a new one. Note that this is done over a single subscription; a new subscription is
not required.

This message below cancels the request "req-2":
```text
SEND
id:sub-0
destination:/ws/threats/cancel
content-length:..

{"id":"req-2","cancel":true}
^@
```

This message below submits a new request "req-3":
```text
SEND
id:sub-0
destination:/ws/threats/incidents
content-length:..

{"id":"req-3","page":{"index":0,"size":300},"sort":[{"field":"created","descending":true}],"filter":[{"field":"created","range":{"from":1445653118808,"to":1445739518808}}]}
^@
```

## Streaming Protocol

The streaming protocol is similar to the [RPC Protocol](#rpc-protocol).  However, instead of receiving a single
response to a request, the server will continue sending messages until no more results are found, or
the `stream.limit` has been hit.

### Connecting
1) The UI client requests a connection to the UI server by sending a `CONNECT` STOMP frame:
```text
CONNECT
X-CSRF-TOKEN:..
accept-version:..
heart-beat:..

^@
```

2) The server sends confirmation to the client once the connection has been established:
```text
CONNECTED
version:..
heart-beat:..

^@
```

### Requesting Data
1) The client sends a message to the server in order to subscribe to a destination of interest. As shown in
the sample message below, the message will include an `id` header with a subscription ID, and a `destination` header.
To illustrate, suppose that the client is interested in fetching a list of Incidents.  In that case, the client
would subscribe to the destination `/user/queue/threats/incidents`:
```text
SUBSCRIBE
id:sub-0
destination:/user/queue/threats/incidents

^@
```

2) The client then sends a message to server to request data.  The message body should be the definition of
the request, in JSON syntax, with `id`, `page`, `sort`, `stream` and `filter` properties:
```text
SEND
destination:/ws/threats/incidents/stream
content-length:..

{"id":"req-1","page":{"index":0,"size":300},"sort":[{"field":"created","descending":true}],"stream":{"limit":3000},"filter":[{"field":"created","range":{"from":1446653118808,"to":1446739518808}}]}
^@
```
The properties of the request body are documented in the [request object](#request-object) below.

3) As the server finds results, the server sends the data to the client.  There may be any number of messages
to satisfy a single request.  All messages will contain the same `request` property with the original request.
```text
MESSAGE
destination:/user/queue/threats/incidents
content-type:application/json;charset=UTF-8
subscription:sub-0
content-length:..

{"code":0,"data":[..],"request":..,"meta":{"total":..}}
^@
```
The [STOMP headers](#stomp-headers) and [Response body](#response-object) are documented below.

4) If the client no longer intends to request any more data for a particular subscription, it should unsubscribe.
For example, if the app has only a single route/screen for fetching incidents and the user leaves that
route/screen, then that might be the appropriate time to unsubscribe:
```text
UNSUBSCRIBE
id:sub-0

^@
```


## Destinations
Destinations are the routing information used to deliver a message to the correct place.  There are two types of
destinations.

1. **Topics:** Topics are shared destinations.  All users that are currently subscribed to a
*topic*, will receive any message published to that topic.  For example, one use of *topics* might be to notify all
users that the service is shutting down.  In Security Analytics, *topics* use the `/topic` destination prefix.
1. **Queues:** Queues are user-specific destinations.  It allows the server to target messages to a single user, or
even a single session for a given user (if the user has multiple browser sessions).  An example of *queues* would
be the user requests Incidents matching some criteria.  Only that user, and that specific session, should receive
the results.  In Security Analytics, *queues* use the `/user/queue` destination prefix.

## Common Message Attributes

### STOMP Headers

| Header | Description |
|:-----------|:------------|
| **X-CSRF-TOKEN** | Contains the CSRF protection token the server validates on incoming WebSocket connections.  This header is only valid on the initial `CONNECT` frame. |
| **id** | A unique (among the existing session only) identifier for a subscription.  This header is only valid on `SUBSCRIBE` frames. |
| **destination** | The topic or queue that the message should be routed to. See [destinations](#destinations) above. |
| **content-type** | Indicates the content-type of the message body. |
| **content-length** | Indicates the length, in bytes, of the message body. |
| **subscription** | The subscription ID that matches the original `id` header from the `SUBSCRIBE` frame. |
| **message-id** | A unique identifier for a `MESSAGE` frame. |

### Request Object
The request object is the common object used to retrieve data from the server.  It contains the follow properties:

| Property | Description |
|:-------------|:------------|
| **id** | Identifier string for this request. This "id" is optional and does not need to be unique across all clients. It can be used by client to track responses when it is making multiple requests. |
| **page** | An object used to paginate data, i.e. request an array of data records in chunks. |
| **page.index** | Zero-based index of the first data record requested. |
| **page.size** | Maximum count of records to be included in the response. |
| **sort** | An array of objects, used to specify the sort order of the data records in the response. Each object in the array represents a sort key, and is expected to have "field" and "descending" properties described below. |
| **sort[i].field** | Name of the data record property by which to sort the records. |
| **sort[i].descending** | If true, indicates descending sort order. |
| **stream** | An object used for streaming data. |
| **stream.limit** | The maximum number of data records that will be returned to the client. |
| **filter** | An array of objects, used to specify a criteria that data records must match in order to be included in the response. Each object in the array represents a filter condition, and is expected to have the properties described below. |
| **filter[i].field** | Name of the data record property to which this filter condition applies. |
| **filter[i].range** | Object with "from" and "to" properties, used to specify a range of requested values. Note that the from value is inclusive, while the to value is exclusive. |

Example:
```json
{
  "id": "req-1",
  "page": {
    "index": 0,
    "size": 300
  },
  "stream": {
    "limit": 300
  },
  "sort": [
    {
      "field": "created",
      "descending": true
    }
  ],
  "stream": {
    "limit": 3000
  },
  "filter": [
    {
      "field": "created",
      "range": {
        "from": 1446653118808,
        "to": 1446739518808
      }
    }
  ]
}
```

### Response Object
The records retrieved from the server are wrapped with a response object that contains the following properties:

| Property | Description |
|:--------------|:------------|
| **code** | SA UI server error code. (Currently, 0 = success, and anything else is an unexpected error.) |
| **data** | The array of records that matched the given request. |
| **request** | The request object that was given by the client. Can be used by client to track responses when multiple requests are being served. |
| **meta** | Additional context for this response. |
| **meta.total** | The total number of records that matched the given request's filter criteria (regardless of the request's page criteria). |

Example:
```json
{
  "code": 0,
  "data": [
    {
      "id": "INC-12627",
      "name": "High Risk Alerts"
    }
  ],
  "request": {
    "id": "req-1",
    "stream": {
      "limit": 3000
    },
    "page": {
      "index": 0,
      "size": 300
    },
    "sort": [
      {
        "field": "created",
        "descending": true
      }
    ],
    "filter": [
      {
        "field": "created",
        "range": {
          "from": 1446653118808,
          "to": 1446739518808
        },
        "isNull": false
      }
    ]
  },
  "meta": {
    "total": 289
  }
}
```