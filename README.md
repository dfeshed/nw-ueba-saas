# NetWitness UI

The NetWitness UI is split into three ember.js projects:
* **component-lib** is an [Ember Addon](https://ember-cli.com/extending/#developing-addons-and-blueprints) containing components that are intended for reuse across multiple parts of the **sa** application.
* **sa** is the primary web application. It is the web app delivered with the NetWitness product.
* **style-guide** is an app that showcases the components from **component-lib** and educates on their use. This web app is [hosted internally](https://libhq-ro.rsa.lab.emc.com/SA/SAStyle/production/) and is updated with each successful Jenkins build. 

## Setting up the Client Application

The `welcome.sh` script will get your entire front-end development environment ready to go.

```
./scripts/welcome.sh
```

Be patient, this will take some time and kill your processor. Hold your laptop down to keep it from flying away.

This script will setup:
* [NVM](https://github.com/creationix/nvm) for managing node versions
* The currently leveraged [Node.js](http://nodejs.org/) version along with [NPM](https://www.npmjs.com/).
* [Bower](http://bower.io/) for managing some client dependencies
* [Ember CLI](http://www.ember-cli.com/) for Ember-ing like a Boss.
* The **component-lib** addon's NPM/Bower dependencies
* The **style-guide** app's NPM/Bower dependencies
* The **sa** app's NPM/Bower dependencies

### Fixtured Mode

The client application can be run without any backend infrastructure.  The XHR and WebSocket requests are intercepted
and mock data is returned.  This is useful for doing front-end development as well as demoing parts of the UI.

To run in fixtured mode, simply start the Ember server without any arguments:
```
$ cd client/sa
$ ember serve
```

### Development Mode

Running the client application with a backend service, to develop and test the full application stack, you need 
to tell Ember where the backend service is located.  To do this, add the `--proxy` flag to the Ember command:
```
$ cd client/sa
$ ember serve --proxy http://localhost:8081
```

## Further Reading / Useful Links

* [ember.js](http://emberjs.com/)
* [ember-cli](http://www.ember-cli.com/)
* Development Browser Extensions
  * [ember inspector for chrome](https://chrome.google.com/webstore/detail/ember-inspector/bmdblncegkenkacieihfhpjfppoconhi)
  * [ember inspector for firefox](https://addons.mozilla.org/en-US/firefox/addon/ember-inspector/)