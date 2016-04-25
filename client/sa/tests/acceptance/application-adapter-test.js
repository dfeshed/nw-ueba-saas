import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import Websocket from 'sa/websocket/service';
import Adapter from 'sa/application/adapter';
import Store from 'sa/store/service';
import config from 'sa/config/environment';

moduleForAcceptance('Acceptance | application adapter', {
  // After each test, destroy the MockServer instances we've created (if any), so that the next test will not
  // throw an error when it tries to re-create them.
  afterEach() {
    (window.MockServers || []).forEach((server) => {
      server.close();
    });
  }
});

test('it can redirect calls to a socket and get a response from a mock server', function(assert) {
  assert.expect(3);

  visit('/');

  let websocket = Websocket.create(),
    adapter = Adapter.create({ websocket }),
    store = Store.create(),
    type = { modelName: 'test' },
    { socketUrl } = config.socketRoutes.test;

  andThen(function() {
    return adapter.query(store, type, {}).then((response) => {
      assert.ok(!!response, 'Received a socket response for adapter.query.');
    });
  });

  andThen(function() {
    return adapter.findRecord(store, type, 'id1', {}).then((response) => {
      assert.ok(!!response, 'Received a socket response for adapter.findRecord.');
    });
  });

  andThen(function() {
    return adapter.updateRecord(store, type, {}).then((response) => {
      assert.ok(!!response, 'Received a socket response for adapter.updateRecord.');
    });
  });

  andThen(function() {
    return websocket.disconnect(socketUrl);
  });
});
