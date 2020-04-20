# Respond

Respond is a routable [ember-engine](https://github.com/ember-engines/ember-engines) that is [mounted](https://github.com/ember-engines/ember-engines.com/blob/66759f39726617b3a17f1f0088ccd78ac73380ce/markdown/guide/mounting-engines.md#routable-engines) inside the sa application at `/respond`.

You can treat the Respond engine as its own application for development purposes. If you are developing Respond, all work can take place in the engine without ever needing to run the sa application.

## Developing

There are two ways to start and run Respond.

### VS Micro-service

Running against a real micro-service can be an effective way to write and test code because it provides a real back-end for the Respond UI.

This method requires you to have an environment already set up with Mongo, Rabbit, and the Respond micro-service running locally. This is very quickly accomplished by running [Docker](https://www.docker.com/).

* Install [Docker](https://www.docker.com/) if you do not have it running on your system
* Navigate a shell to the sa-ui/respond folder
* Execute the following command to pull the latest docker images based on the `docker-compose.yml` file in the `respond` folder (NOTE: you must be on Reston 2FA). This will pull down all of the requisite images (including data).
```
docker-compose pull
```
* Once the Docker images have been downloaded, execute this command to spin up the environment:
```
docker-compose up
```
* Finally, open a new command shell and navigate to the `sa-ui/respond folder`, then execute the following command:

```
NOMOCK=1 ember serve --proxy http://localhost:7003
```

This starts and serves Ember and ensures that all requests from the UI are proxied to `7003` which is where the Respond micro-service is running. `NOMOCK=1` ensures that the UI will not automatically run against the built-in mock server, but instead will run against the live micro-service.


##### Getting the latest micro-service updates
As service-side developers make changes to the `respond-server` micro-service and push those changes into master, new versions of the respond-server docker images will be created. The [Launch Pipeline Job](https://github.rsa.lab.emc.com/asoc/launch-pad/tree/master/jenkins#build-pipeline) will automatically build the docker images for any PR that is opened. Those images are only used for the integration tests on that PR. Once the PR is merged, the docker image is tagged using the 11.0.0-latest tag, and pushed to [DTR](https://asoc-dtr2.rsa.lab.emc.com/).

To include those changes in your development environment, you should regularly run `docker-compose pull`, which will update the images locally.

If you want to support development branches, a developer can create their own repository on [DTR](https://asoc-dtr2.rsa.lab.emc.com/), and push any images they want. Then locally, this compose file should be changed to point to that image instead. These temporary changes should not be committed, however.

### VS mock-server

You can also run vs the sa [`mock-server`](https://github.rsa.lab.emc.com/asoc/sa-ui/tree/master/mock-server). This method will start a node server that will serve up static responses to requests from the UI. This is the most lightweight option as it does not require anything else to be set up.

This requires two terminal windows.

```
node mockserver.js
```

This starts the mock-server.

```
ember s
```

This starts ember-cli in such a way that it will route all requests to the mock-server.

