# SA Mock Server

SA is using ember-cli-mirage to mock our JSON API.

## Creating mock data

Define route handlers for the api call that has to respond to AJAX requests. This can be done by creating
a javascript file under sa/app/mirage/routes directory. All the apis corresponding to each module goes into a
separate js file. For example, all incident related apis will go under routes/incidents.js.

Lets say we are creating mock data for "/api/incident/list" to GET the list of all incidents.

```
    // sa/app/mirage/routes/incidents.js
    this.get('/incident/list', function(db, request) {
        return db.incidents;
    });
```
This would return all the incidents stored in the db. We'll show how to add incidents to the db shortly.

We can also directly return the list of incidents by specifying the following

```
    // sa/app/mirage/routes/incidents.js
    this.get('/incodents/list', function() {
        return {
            incidents: [
                {id: 1, name: 'Incident 1'},
                {id: 2, name: 'Incident 2'},
                {id: 3, name: 'Incident 3'},
                ]
        }
    });
```

We can mock GET, POST, PUT, DELETE methods.

```
    this.verb(path, shorthand[, responseCode]);
```

## Adding records to the database

Next, we specify the schema of the api and add records to the database that follows the schema. To accomplish that,
create an appropriate javascript file under sa/app/mirage/factories.
In our example, we'll create an incidents.js under factories folder.

```
    // sa/app/mirage/factories/incidents.js
    export default Mirage.Factory.extend({
        id: i => `${i}`
        name: function(i) {
            return 'Incident ' + i;
        }
    });
```

Then, we specify the number of such records to be created. This is done by creating a javascript file under
sa/app/mirage/data. We create an incidents.js file here and specify the number of incidents to be created.

```
    // sa/app/mirage/data/incidents.js
    export default function(server) {
        //Create 15 incidents with the schema listed in factories/incidents.js
        server.createList('incidents', 15);
    }

```
##  Configuration

We specify the configuration for our server in sa/app/mirage/config.js. The most common configuration we use
is the namespace. Since all our api's begins with "/api", we specify the default namespace to "/api".

Another common configuration that can be overridden is the timing. This is the number of milliseconds in which
the response has to be returned. We can override the default value (400ms), by specifying
```
    this.timing = 1000;
```

Config file also holds the list of all the apis that would go through mirage. This is accomplished by importing the appropriate javascript file and calling the imported function.

```
    // sa/app/mirage/config.js
    import incidents from "sa/mirage/routes/incidents";
    export default function() {
        ...
        ...
        incidents(this);
    }
```

By default, mirage is enabled If we would like to disable mirage in development environment. To disable it,

```
    // config/environment.js
    ...
    if (environment === 'development') {
        ENV['ember-cli-mirage'] = {
            enabled: false
        }
    }
```

##  Passthroughs
If you would like certain xhr calls to not go through ember-cli-mirage, then we can specify them as passthroughs.
Simply add the api that needs to passthrough in the /sa/app/mirage/routes/passthrough.js. If you would like
to load local JSON/CSV files, specify them in the passthrough.js

For example, to passthrough "/vendor/testdata.csv", add the following

```
    // sa/app/mirage/routes/passthrough.js
    ...
    config.pretender.get('/vendor/testdata.csv', config.pretender.passthrough);
```

## More information
For more details please refer to the ember-cli-mirage page here - http://www.ember-cli-mirage.com/
Mirage uses pretender as its interceptor. To learn more about pretender go to https://github.com/pretenderjs/pretender
