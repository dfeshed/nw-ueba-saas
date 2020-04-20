
# Introduction

This Ember addon has the following components related to Netwitness Licensing:

* an **Ember Component** (`license-banner`) that fetches the latest license compliance information from license-server and shows either a persistent or a disposable banner;

*Note:* To include any component from this addon in your app/engine/addon, you must first define this addon as a dependency for your app/engine/addon as usual.


# The License Compliance Banner

_A standalone Ember Component to show license compliance information on any Ember page._

## How to use

In order to include this banner in your Ember UI,  insert the `{{license-banner}}` component in the `template.hbs` file of your Route (or Component) where you wish to show the banner

```hbs
{{license-banner}}
```

## Developing

There are two ways to start and run the License addon.

### VS Micro-service

This method requires you to have the correct [environment](https://wiki.na.rsa.net/pages/viewpage.action?spaceKey=~garalj&title=NW-UI) already set up with Mongo, Rabbit, and the "License Server" micro-service running locally.

```
NOMOCK=1 ember serve --proxy=http://localhost:7016
```

This starts and serves Ember and ensures that all requests from the UI are proxied to `7016` which is where (in most cases) the License server is running.

### VS mock-server

You can also run vs the sa [`mock-server`](https://github.rsa.lab.emc.com/asoc/sa-ui/tree/master/mock-server). This method will start a node server that will serve up static responses to requests from the UI. This is the most lightweight option as it does not require anything else to be set up.

This requires two terminal windows.

```
cd license
node mockserver.js
```

This starts the mock-server.

```
ember s
```

This starts ember-cli in such a way that it will route all requests to the mock-server.
