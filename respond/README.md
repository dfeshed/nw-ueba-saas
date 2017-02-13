# Respond

Respond is a routable [ember-engine](https://github.com/ember-engines/ember-engines) that is [mounted](https://github.com/ember-engines/ember-engines.com/blob/66759f39726617b3a17f1f0088ccd78ac73380ce/markdown/guide/mounting-engines.md#routable-engines) inside the sa application at `/respond`.

You can treat the Respond engine as its own application for development purposes. If you are developing Respond all work can take place in the engine without ever needing to run the sa application.

## Developing

There are two ways to start and run Respond.

### VS Micro-service

This method requires you to have the correct [environment](https://wiki.na.rsa.net/pages/viewpage.action?spaceKey=NextGenWeb&title=Respond+Workflow+-+Launching+SA+11+Respond+flow) already set up with Mongo, Rabbit, and the Respond micro-service running locally. Running against a real micro-service is the best possible way to develop as it it provides a real back-end for the Respond UI.

1. Create an ssl folder under asoc/sa-ui/sa and generate SSL certificate. Please note that this process requires the existence of a self-signed certificate which you can generate with these []instructions](https://devcenter.heroku.com/articles/ssl-certificate-self).
2. Navigate a shell to the sa-ui/respond folder and execute the following command:

```
NOMOCK=1 ember serve --proxy https://localhost:7003 --secure-proxy false --ssl true
```

This starts and serves Ember and ensures that all requests from the UI are proxied to `7003` which is where (in most cases) the Respond micro-service is running.

### VS mock-server

You can also run vs the sa [`mock-server`](https://github.rsa.lab.emc.com/asoc/sa-ui/tree/master/mock-server). This method will start a node server that will serve up static responses to requests from the UI. This is the most lightweight option as it does not require anything else to be set up.

This requires two terminal windows.

```
cd tests/server
node server.js
```

This starts the mock-server.

```
ember s
```

This starts ember-cli in such a way that it will route all requests to the mock-server.

