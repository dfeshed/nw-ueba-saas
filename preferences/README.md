
# Introduction

This Ember addon enables the user to set personalize preferences for different modules. It provides the consuming app with the following:

* an **Ember Component** (`preferences-panel`) that fetches and displays all available preferences for given module. All preferences will have some default values, using this Component user can override any preference;

* an **Ember Service** (`preference`) that provides module related preferences from server.

The sections below discuss each of these topics above in more detail.

*Note:* To include any code from this addon in your app/engine/addon, you must first define this addon as a dependency for your app/engine/addon as usual.  For `sa-ui`, those changes were introduced in [this PR](https://github.rsa.lab.emc.com/asoc/sa-ui/pull/1146).


# The Preferences Panel Component

_A standalone Ember Component that allows user to customize preference for particular module.._

## How to use

In order to include the Preferences Panel in your Ember UI,  insert the `{{preferences-panel}}` component in the `template.hbs` file of your Route (or Component), and specify the `launchFor` attributes as strings:

```hbs
{{preferences-panel launchFor/source=launchFor}}
```

In the `sa` app, the `{{preferences-panel}}` is included in the `protected` route's template. In the `investigate-events` engine's `dummy` app, the `{{preferences-panel}}` is included in the `application` route's template.

# The Preferences Service

_An Ember Service for providing module related preferences from server._

## Overview

The Preferences Service (named `context`) is a client-side [Ember Service](http://emberjs.com/api/classes/Ember.Service.html) (not to be confused with a web-server-side Java service). That is, it is an Ember object that lives for the duration of the application and can be made available to many different parts of the application.

The Preferences service exposes two API for retrieving or persisting module preferences from server:

## How to use

The Service can be injected the same way any Ember Service can, by using the `service()` method from `ember-service/inject`, as illustrated below:

```js
// file: my-component.js
import service from `ember-service/inject`;

export default Component.extend({
  // Inject the preferences service:
  preferences: service(),
  ..
});
```

The `preferences` Service exposes the following API methods:

### `getPreferences(${moduleName})`: `Promise`

Requests the list of preferences exposed for requested module.  

The `getPreferences(${moduleName})` method returns a `Promise` which will resolve with an preferences object when the request completes successfully:

```js
// file: my-component.js
this.get('preferences').getPreferences('events')
  .then((preferenceObject) => {
    // do stuff here with the types array
    //console.log(preferenceObject);
    ...
  });
```

Note that the `getPreferences()` method caches its results (if successful), so it can be called repeatedly without requiring multiple round trips to server.  It can cache because it assumes that the `preferences` configuration cannot be changed for logged in user from some other user's session of Netwitness UI. If the configuration were to change in mid-session, the Netwitness UI would need to be refreshed in order for the change to take effect.


### `setPreferences(${moduleName},${preferencesObject})`: `Promise`

Overrides current preferences with new one.

Use this async call in order to persist new preferences. For this API required `${moduleName}` and `${preferencesObject}`.

```js
{
     "defaultLandingPage": null,
     "eventsPreferences": {
       "defaultAnalysesView": "Text"
     }
   }
```

The `setPreferences()` method returns a `Promise` which will resolve once preferences persisted successfully.

```js
// file: my-component.js
const myEntityType = 'IP';

this.get('preferences').setPreferences(${moduleName},${preferencesObject}).then(({ data }) => {
    // persist preferences successfully.
  });
```

This method is useful for UIs where preferences are saved along with actions, Like: changing reconPanelSize along with changing visualization , this API help in persisting for next time.  

# How to add preferences for any new module.

_Way to define preferences for new module._

## Adding new socket route

Preferences will be stored in application mongo. So there will be separate channel for different module preferences. Same need to be define as follows:

```js
// file: preferences/config/socketRoute.js
// Add new url for different service.
  const investigateSocketUrl = common.determineSocketUrl(environment, '/investigate/socket');
  {
    '${moduleName}-preferences': {
      socketUrl: investigateSocketUrl,
      getPreferences: {
        subscriptionDestination: '/user/queue/investigate/preferences/get',
        requestDestination: '/ws/investigate/preferences/get'
      },
      setPreferences: {
        subscriptionDestination: '/user/queue/investigate/preferences/set',
        requestDestination: '/ws/investigate/preferences/set'
      }
    }
  }
```
Adding new socket route details will enable service to call preferences for new module. Ex. after adding respond socket route, for respond preferences service call is going to be: `this.get('preferences').getPreferences('respond')` `this.get('preferences').setPreferences('respond',${preferencesObject})`

Note: Micro-service needs to expose channels for getting and setting preferences for new module.

## Adding support for new module's preferences panel
//@TODO Need to document based on new trigger.


## Developing

There are two ways to start and run Preferences addon.

### VS Micro-service

This method requires you to have the correct [environment](https://wiki.na.rsa.net/pages/viewpage.action?spaceKey=~garalj&title=NW-UI) already set up with Mongo, Rabbit, and the Investigate micro-service running locally. Running against a real micro-service is the best possible way to develop as it it provides a real back-end for the Investigate UI.

```
NOMOCK=1 ember serve --proxy=http://localhost:7004
```

This starts and serves Ember and ensures that all requests from the UI are proxied to `7004` which is where (in most cases) the Investigate micro-service is running.

### VS mock-server

You can also run vs the sa [`mock-server`](https://github.rsa.lab.emc.com/asoc/sa-ui/tree/master/mock-server). This method will start a node server that will serve up static responses to requests from the UI. This is the most lightweight option as it does not require anything else to be set up.

This requires two terminal windows.

```
cd preferences
node mockserver.js
```

This starts the mock-server.

```
ember s
```

This starts ember-cli in such a way that it will route all requests to the mock-server.
