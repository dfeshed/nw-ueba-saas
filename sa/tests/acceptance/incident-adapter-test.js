import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import Request from 'sa/services/request';
import Adapter from 'sa/incident/adapter';
import Store from 'ember-data/store';
import Ember from 'ember';
import teardownSockets from 'sa/tests/helpers/teardown-sockets';

const {
  Object: EmberObject
} = Ember;

moduleForAcceptance('Acceptance | Incident adapter', {
  afterEach: teardownSockets
});


test('it can redirect calls to a socket and get a response from a mock server', function(assert) {
  visit('/responded');

  const router = EmberObject.create({
    currentRouteName: null
  });
  const request = Request.create({ router });
  const adapter = Adapter.create({ request });
  const store = Store.create();
  const type = { modelName: 'test' };

  andThen(function() {
    return adapter.findRecord(store, type, 'id1', {}).then((response) => {
      assert.ok(!!response.data, 'data is missing');
      assert.ok(!!response, 'Received a socket response for adapter.findRecord.');
    });
  });

});

test('it can redirect calls to a socket and get a response from a mock server', function(assert) {
  visit('/responded');

  const router = EmberObject.create({
    currentRouteName: null
  });
  const request = Request.create({ router });
  const adapter = Adapter.create({ request });
  const store = Store.create();
  const type = { modelName: 'test' };

  const snapShot = { 'id': 1,
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

});
