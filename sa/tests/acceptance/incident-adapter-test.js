import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import Websocket from 'sa/websocket/service';
import Adapter from 'sa/incident/adapter';
import Store from 'sa/store/service';
import config from 'sa/config/environment';

moduleForAcceptance('Acceptance | Incident adapter', {
  // After each test, destroy the MockServer instances we've created (if any), so that the next test will not
  // throw an error when it tries to re-create them.
  afterEach() {
    (window.MockServers || []).forEach((server) => {
      server.close();
    });
  }
});

test('it can redirect calls to a socket and get a response from a mock server', function(assert) {
  visit('/');

  let websocket = Websocket.create(),
    adapter = Adapter.create({ websocket }),
    store = Store.create(),
    type = { modelName: 'test' },
    { socketUrl } = config.socketRoutes.test;

  andThen(function() {
    return adapter.findRecord(store, type, 'id1', {}).then((response) => {
      assert.ok(!!response.data, 'data is missing');
      assert.ok(!!response, 'Received a socket response for adapter.findRecord.');
    });
  });

  andThen(function() {
    return websocket.disconnect(socketUrl);
  });
});

test('it can redirect calls to a socket and get a response from a mock server', function(assert) {

  visit('/');

  let websocket = Websocket.create(),
    adapter = Adapter.create({ websocket }),
    store = Store.create(),
    type = { modelName: 'test' },
    { socketUrl } = config.socketRoutes.test;

  let snapShot = { 'id': 1,
                  changedAttributes() {
                    return { 'priority': ['LOW', 'HIGH'] };
                  }
                };

  andThen(function() {
    return adapter.updateRecord(store, type, snapShot).then((response) => {
      assert.ok(!!response, 'Received a socket response for adapter.updateRecord.');
      assert.ok(!!response.request.updates, 'Updates key is missing');
      assert.ok(!!response.request.updates.priority, 'Priority is missing in Updates');
      assert.equal(response.request.updates.priority, 'HIGH', 'Invalid priority');
      assert.equal(response.request.incidentId, 1, 'Invalid incidentId');
    });
  });

  andThen(function() {
    return websocket.disconnect(socketUrl);
  });
});
