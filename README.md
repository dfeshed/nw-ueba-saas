# NetWitness UI

## Running in Development

### SA Client

The SA UI is split into two EmberJS projects:
* **Dashboard** which contains all the reusable Ember components and CSS.
* **Application** which is the customer facing application.  It inherits the components and styling from the
dashboard project.

#### Setting up the Client Application

The `welcome.sh` script will get your entire front-end development environment ready to go.

```
./client/scripts/welcome.sh
```

Be patient, this will take some time and kill your processor. Hold your laptop down to keep it from flying away.

This script will setup:
* [NVM](https://github.com/creationix/nvm) for managing node versions
* The currently leveraged [Node.js](http://nodejs.org/) version along with [NPM](https://www.npmjs.com/).
* [Bower](http://bower.io/) for managing some client dependencies
* [Ember CLI](http://www.ember-cli.com/) for Ember-ing like a Boss.
* The component-lib app's NPM/Bower dependencies
* The style-guide app's NPM/Bower dependencies
* The sa app's NPM/Bower dependencies

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

Spring Boot makes starting a service easy:
```
$ mvn -pl server/threats spring-boot:run
```

That will start the service with all the defaults.  You can modify the YAML files to override the default properties.
The list of properties can be found on the [configuration page](docs/server/configuration.md).

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
