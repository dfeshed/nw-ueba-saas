# Security Analytics UI

## Running in Development

### SA Client

The SA UI is split into two EmberJS projects:
* **Dashboard** which contains all the reusable Ember components and CSS.
* **Application** which is the customer facing application.  It inherits the components and styling from the
dashboard project.

#### Running the Client Application

You will need the following things properly installed on your computer:
* [Node.js](http://nodejs.org/) (with NPM)
* [Bower](http://bower.io/)
* [Ember CLI](http://www.ember-cli.com/)

Once those are installed, you must setup the Node and Bower dependencies for both EmberJS projects:
```
$ cd client/sa
$ npm install
$ bower install
$ cd lib/dashboard
$ npm install
$ bower install
```

##### Fixtured Mode

The client application can be run without any backend infrastructure.  The XHR and WebSocket requests are intercepted
and mock data is returned.  This is useful for doing front-end development as well as demoing parts of the UI.

To run in fixtured mode, simply start the Ember server without any arguments:
```
$ cd client/sa
$ ember serve
```

##### Development Mode

Running the client application with a backend service, to develop and test the full application stack, you need 
to tell Ember where the backend service is located.  To do this, add the `--proxy` flag to the Ember command:
```
$ cd client/sa
$ ember serve --proxy http://localhost:8081
```

See below for running the backend services in development.

### Backend Services

There are currently two backend services:
* **Threats** - Used to communicate with the Incident Management service and handle incidents/threats
* **Investigation** - Used to communicate with NextGen core devices - **this service is not current used**

#### Development Mode

Gradle makes starting a service easy: 
```
$ cd server/threats
$ ../../gradlew run
```

That will start the service with all the defaults.  You can pass a `-D` flag to override the default properties. 
The list of properties can be found on the [configuration page](docs/server/configuration.md).

##### Threats Service

You can override the default host and port of the Incident Management service, by setting either or both of the 
following system properties:
```
$ cd server/threats
$ ../../gradlew run -D'appliances[0].host=111.222.333.444' -D'endpoints[0].port=50040'
```

The host will most likely be your Docker host that's running the IM service.  More information on Docker can 
be found [here](https://github.rsa.lab.emc.com/asoc/docker).

## Further Reading / Useful Links

### Frontend

* [ember.js](http://emberjs.com/)
* [ember-cli](http://www.ember-cli.com/)
* Development Browser Extensions
  * [ember inspector for chrome](https://chrome.google.com/webstore/detail/ember-inspector/bmdblncegkenkacieihfhpjfppoconhi)
  * [ember inspector for firefox](https://addons.mozilla.org/en-US/firefox/addon/ember-inspector/)

### Backend
* [Spring Framework](http://docs.spring.io/spring-framework/docs/current/spring-framework-reference/htmlsingle/)
* [Spring Boot](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
* [Docker](https://www.docker.com/)
