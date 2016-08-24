/**
 * @file Manages the configuration for ember-cli-mirage
 * @description Lists all the APIs that would return mock data in non-production environment
 * @public
 */
import config from 'sa/config/environment';

import passthrough from 'sa/mirage/routes/passthrough';
import login from 'sa/mirage/routes/login';
import users from 'sa/mirage/routes/users';
import info from 'sa/mirage/routes/info';

import MockServer from 'sa/mirage/sockets/mock-server';
import connect from 'sa/mirage/sockets/routes/connect';
import disconnect from 'sa/mirage/sockets/routes/disconnect';
import test from 'sa/mirage/sockets/routes/test';
import incidents from 'sa/mirage/sockets/routes/incidents';
import coreServices from 'sa/mirage/sockets/routes/core-services';
import coreEvents from 'sa/mirage/sockets/routes/core-events';
import coreEventCounts from 'sa/mirage/sockets/routes/core-event-counts';
import categoryTags from 'sa/mirage/sockets/routes/category-tags';

/*
  Helper for collecting an array of all the unique `socketUrl`s found in the app's `config/environment.js` file.
  @returns {string[]} An array of the found URLs, if any; possibly empty.
  @private
*/
function uniqueSocketUrls() {
  let urls = [];
  Object.keys(config.socketRoutes || {}).forEach((modelName) => {
    let modelConfig = config.socketRoutes[modelName];
    if (modelConfig.socketUrl) {
      urls.push(modelConfig.socketUrl);
    }
    Object.keys(modelConfig).forEach((method) => {
      let methodConfig = modelConfig[method];
      if ((typeof methodConfig === 'object') && methodConfig.socketUrl) {
        urls.push(methodConfig.socketUrl);
      }
    });
  });
  return urls.uniq();
}

export default function() {

  // initialize the list of all apis that doesn't need mirage
  passthrough(this);

  this.namespace = '/api';

  login(this);
  users(this);
  info(this);

  // According to mock-socket docs, we must first create a mock server before creating any mock sockets.
  // So initialize a MockServer for each supported socket url.
  // For each MockServer we instantiate, configure any socket "routes" that we want to mock.
  window.MockServers = uniqueSocketUrls().map((url) => {
    let server = new MockServer(url);
    server.init();
    connect(server);
    disconnect(server);
    test(server);
    incidents(server);
    coreServices(server);
    coreEvents(server);
    coreEventCounts(server);
    categoryTags(server);
    server.mirageServer = this;
    return server;
  });

  // Substitute the mock socket class for the real socket class.
  window.WebSocket = window.MockWebSocket;

  // @workaround Explicitly substitute mock socket for SockJS too, if SockJS is defined. Without this,
  // SockJS will not use our MockSocket even though we've substituted it for WebSocket, not sure why.
  window.SockJS = window.SockJS && window.MockWebSocket;
}
