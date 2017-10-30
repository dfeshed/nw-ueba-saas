# NetWitness UI

The NetWitness UI is split into many directories, most of which contain ember.js projects.

## Ember Applications
* `sa` - `sa` is the UI for the NetWitness product. It contains code relevant to logging in. Otherwise `sa` is largely an empty skeleton that mounts several [Ember Engines](https://github.com/ember-engines/ember-engines). The vast majority of development does not take place in `sa`, instead it takes place in the many engines/addons that are brought together to create the product.
* `style-guide` - an app that showcases the components from `component-lib` and educates on their use. This web app is [hosted internally](https://libhq-ro.rsa.lab.emc.com/SA/SAStyle/production/) and is updated with each successful Jenkins sa-ui-master build.

## Ember Engines
* `respond` - an [Ember Engine](https://github.com/ember-engines/ember-engines) that is mounted inside `sa` at `/respond` and contains all the functionality to manage incidents.
* `investigate` - an [Ember Engine](https://github.com/ember-engines/ember-engines) that is mounted inside `sa` at `/investigate` and contains all the functionality to search events.

## Ember Addons
* `component-lib` -  an [Ember Addon](https://ember-cli.com/extending/#developing-addons-and-blueprints) containing components that are intended for reuse across all other applications, engines and addons that comprise the NetWitness UI.
* `recon` - an [Ember Addon](https://ember-cli.com/extending/#developing-addons-and-blueprints) containing components used for event reconstruction.
* `context` - an [Ember Addon](https://ember-cli.com/extending/#developing-addons-and-blueprints) containing components responsible for displaying a context panel for specific contextable fields in the NetWitness UI.
* `streaming-data` - an [Ember Addon](https://ember-cli.com/extending/#developing-addons-and-blueprints) that contains the code used for communicating with NetWitness microservices via websockets.

## Other
* `mock-server` - contains our mock-server, an Express server used by virtually all the Ember projects to mock a real back end.
* `scripts` - contains scripts for 1) setting up the environment locally and 2) running builds in Jenkins
* `docs` - contains various documentation about the NetWitness UI, however, **WARNING**, the documentation in this folder is woefully out of date.

# Setting up the Client Application

The `welcome.sh` script will get your entire front-end development environment ready to go.

```
./scripts/welcome.sh
```

This script will:
* Install [NVM](https://github.com/creationix/nvm) for managing node versions
* Install the currently leveraged [Node.js](http://nodejs.org/) version along with [NPM](https://www.npmjs.com/).
* Install [Ember CLI](http://www.ember-cli.com/) for Ember-ing like a Boss.
* Install [Yarn](https://yarnpkg.com/en/) for installing node modules
* Run Yarn against the root `package.json` for the entire project, this installs all of the `node_modules` required to develop any aspect of the NetWitness UI in one shot.

If you've already been dev-ing and something is screwed up in your environment, or maybe you are working with a new node and need a clean install of everything, or maybe you are just bouncing between branches and need a fresh start, the `welcome` script can be used and reused to start from scratch.

# Developing

If developing a specific app/addon/engine, check the README for that component first. The following generally applies to most components of the NetWitness UI.

All of the apps, addons and engines are designed for development in isolation. If you are developing `respond`, you need not worry about `investigate`, or `context`, or, for that matter, `sa`. You can focus entirely on the `respond` codebase by running it alone against either a mock-server or a real development back-end.

## Mock Server

Most of the components (apps, engines, addons) of the client application can be run without any backend infrastructure. An Express server can be run that can return canned data.

To run in mock-server mode, in a Terminal window, run `node mockserver.js` for the Ember project of your choice.

In a 2nd Terminal window, run `ember s` for the same project. Then open http://localhost:4200.

## Micro-services

You can also run an Ember project against a real set of NetWitness micro-services. To do this, add the `--proxy` flag to the Ember command and provide the `NOMOCK=1` flag so Ember knows you are not hitting a mock-server. For instance, for investigate:
```
$ cd investigate
$ NOMOCK=1 ember serve --proxy=http://localhost:7004
```

# Further Reading / Useful Links

* [Sonar Dashboard](http://asoc-sonar.rsa.lab.emc.com/projects/)
* [Style Guide](http://libhq-ro.rsa.lab.emc.com/SA/SAStyle/production/#/)
* [ember.js](http://emberjs.com/)
* [ember-cli](http://www.ember-cli.com/)
* Development Browser Extensions
  * [ember inspector for chrome](https://chrome.google.com/webstore/detail/ember-inspector/bmdblncegkenkacieihfhpjfppoconhi)
  * [ember inspector for firefox](https://addons.mozilla.org/en-US/firefox/addon/ember-inspector/)
  * [Redux DevTools](https://chrome.google.com/webstore/detail/redux-devtools/lmhkpmbekcpmknklioeibfkpmmfibljd?hl=en)