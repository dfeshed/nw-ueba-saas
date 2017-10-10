# Preferences Add-on

This addon is under construction

"Preferences" is a generic add-on that is meant to host any kind of preferences for various modules of NW.

It is still under development and at first it will contain preferences only for `Investigate` module. But it is being developed as a container for any preferences.

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

