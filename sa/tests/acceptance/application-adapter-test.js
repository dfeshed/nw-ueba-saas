import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import Request from 'sa/services/request';
import Adapter from 'sa/application/adapter';
import Store from 'ember-data/store';
import Ember from 'ember';

const {
  Object: EmberObject
} = Ember;

moduleForAcceptance('Acceptance | application adapter', {
  // After each test, destroy the MockServer instances we've created (if any), so that the next test will not
  // throw an error when it tries to re-create them.
  afterEach() {
    (window.MockServers || []).forEach((server) => {
      server.close();
    });
  }
});

test('it can redirect calls to a socket and get a response from a mock server 1', function(assert) {
  visit('/');

  const router = EmberObject.create({
    currentRouteName: null
  });

  let request = Request.create({ router });
  let adapter = Adapter.create({ request });
  let store = Store.create();
  let type = { modelName: 'test' };

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
  let request = Request.create({ router });
  let adapter = Adapter.create({ request });
  let store = Store.create();
  let type = { modelName: 'test' };

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
  let request = Request.create({ router });
  let adapter = Adapter.create({ request });
  let store = Store.create();
  let type = { modelName: 'test' };

  andThen(function() {
    return adapter.updateRecord(store, type, {}).then((response) => {
      assert.ok(!!response, 'Received a socket response for adapter.updateRecord.');
    });
  });

});
