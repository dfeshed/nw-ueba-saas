/**
 * @file Manages the configuration for ember-cli-mirage
 * @description Lists all the APIs that would return mock data in non-production environment
 * @public
 */
import config from 'sa/config/environment';

import passthrough from 'sa/mirage/routes/passthrough';
import login from 'sa/mirage/routes/login';
import info from 'sa/mirage/routes/info';
import eula from 'sa/mirage/routes/eula';

import MockServer from 'sa/mirage/sockets/mock-server';
import connect from 'sa/mirage/sockets/routes/connect';
import disconnect from 'sa/mirage/sockets/routes/disconnect';
import test from 'sa/mirage/sockets/routes/test';
import incidents from 'sa/mirage/sockets/routes/incidents';
import alerts from 'sa/mirage/sockets/routes/alerts';
import categoryTags from 'sa/mirage/sockets/routes/category-tags';
import journalEntry from 'sa/mirage/sockets/routes/journal-entry';
import storyline from 'sa/mirage/sockets/routes/incident-storyline';
import context from 'sa/mirage/sockets/routes/context';
import relatedEntity from 'sa/mirage/sockets/routes/related-entity';
import events from 'sa/mirage/sockets/routes/events';
import users from 'sa/mirage/sockets/routes/users';

/*
  Helper for collecting an array of all the unique `socketUrl`s found in the app's `config/environment.js` file.
  @returns {string[]} An array of the found URLs, if any; possibly empty.
  @private
*/
function uniqueSocketUrls() {
  const urls = [];
  Object.keys(config.socketRoutes || {}).forEach((modelName) => {
    const modelConfig = config.socketRoutes[modelName];
    const { socketUrl } = modelConfig;
    if (socketUrl && socketUrl.indexOf('localhost') === -1) {
      urls.push(modelConfig.socketUrl);
    }
    Object.keys(modelConfig).forEach((method) => {
      const methodConfig = modelConfig[method];
      if ((typeof methodConfig === 'object') && methodConfig.socketUrl) {
        urls.push(methodConfig.socketUrl);
      }
    });
  });
  return urls.uniq();
}

export default function() {
  this.urlPrefix = '/';
  // initialize the list of all apis that doesn't need mirage
  passthrough(this);
  login(this);
  eula(this);

  this.namespace = '/api';

  info(this);

  // According to mock-socket docs, we must first create a mock server before creating any mock sockets.
  // So initialize a MockServer for each supported socket url.
  // For each MockServer we instantiate, configure any socket "routes" that we want to mock.
  window.MockServers = uniqueSocketUrls().map((url) => {
    const server = new MockServer(url);
    server.init();
    connect(server);
    disconnect(server);
    test(server);
    incidents(server);
    alerts(server);
    categoryTags(server);
    journalEntry(server);
    storyline(server);
    events(server);
    context(server);
    relatedEntity(server);
    users(server);
    server.mirageServer = this;
    return server;
  });

  // Substitute the mock socket class for the real socket class.
  // NOTE: for mock-server to work, this will need to go
  window.WebSocket = window.MockWebSocket;

  // @workaround Explicitly substitute mock socket for SockJS too, if SockJS is defined. Without this,
  // SockJS will not use our MockSocket even though we've substituted it for WebSocket, not sure why.
  // NOTE: for mock-server to work, this will need to go
  window.SockJS = window.SockJS && window.MockWebSocket;
}
