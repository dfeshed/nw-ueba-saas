# HTTP vs WebSockets

## Background

As we continue to push the limits of client-side applications, streamlining 
communication with the backend services becomes more important.  AJAX/XHR 
requests were the primary method in the previous UI stack (10.x).  The  
issue we kept running into was whenever the backend service needed to 
communicate with another service (e.g., a NextGen broker), the HTTP connection 
from the client's browser would sit idle, consuming a thread in Jetty's 
connection pool, while it waited for the response from the broker.  When 
too many of these connections hit a slow service, the connection pool would 
fill-up and no new requests could be made.

## Guidelines

### WebSockets

WebSockets should be used for requests that require data from another 
remote backend service.  On the frontend the custom Ember Data 
WebSocketAdapter should be used.

**Examples:**
- Querying a broker.  WebSockets should be used since the broker may take
some time to complete the query.  The broker may also send "partial results" 
or give status updates/estimates on remaining time.
- Retrieving the list of services/endpoints from SMS
- Submitting a template to ESA
   
### HTTP   
HTTP should be used for requests that are only retrieving data from 
the UI backend server.  On the frontend the Ember Data RESTAdapter should 
be used.

**Examples:**
- Retrieving user preferences.  This can be done over HTTP since the
preferences will most likely be stored in a database, and won't have to 
make a call to another SA service.
  