import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import Request from 'sa/services/request';
import Adapter from 'sa/application/adapter';
import Store from 'ember-data/store';
import Ember from 'ember';
import teardownSockets from 'sa/tests/helpers/teardown-sockets';

const {
  Object: EmberObject
} = Ember;

moduleForAcceptance('Acceptance | application adapter', {
  afterEach: teardownSockets
});

test('it can redirect calls to a socket and get a response from a mock server 1', function(assert) {
  visit('/');

  const router = EmberObject.create({
    currentRouteName: null
  });

  const request = Request.create({ router });
  const adapter = Adapter.create({ request });
  const store = Store.create();
  const type = { modelName: 'test' };

  andThen(function() {
    return adapter.query(store, type, {}).then((response) => {
      assert.ok(!!response, 'Received a socket response for adapter.query.');
    });
  });

});

test('it can redirect calls to a socket and get a response from a mock server 2', function(assert) {
  visit('/');

  const router = EmberObject.create({
    currentRouteName: null
  });
  const request = Request.create({ router });
  const adapter = Adapter.create({ request });
  const store = Store.create();
  const type = { modelName: 'test' };

  andThen(function() {
    return adapter.findRecord(store, type, 'id1', {}).then((response) => {
      assert.ok(!!response, 'Received a socket response for adapter.findRecord.');
    });
  });

});

test('it can redirect calls to a socket and get a response from a mock server 3', function(assert) {
  visit('/');

  const router = EmberObject.create({
    currentRouteName: null
  });
  const request = Request.create({ router });
  const adapter = Adapter.create({ request });
  const store = Store.create();
  const type = { modelName: 'test' };

  andThen(function() {
    return adapter.updateRecord(store, type, {}).then((response) => {
      assert.ok(!!response, 'Received a socket response for adapter.updateRecord.');
    });
  });

});
