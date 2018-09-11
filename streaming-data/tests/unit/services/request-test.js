import EmberObject from '@ember/object';

import sinon from 'sinon';
import { module, test } from 'qunit';

import Request from 'streaming-data/services/request';

const noop = function() {};

module('Unit | Service | request');

test('observes route changes', function(assert) {
  assert.expect(1);
  const router = EmberObject.create({ currentRouteName: 'foo' });
  const requestService = Request.create({ router });

  sinon.spy(requestService, '_routeCleanup');
  router.set('currentRouteName', 'bar');

  assert.ok(requestService._routeCleanup.calledOnce, 'when route changes request service takes action');
});

test('streamRequest will throw socket config error if environment config not present', function(assert) {
  assert.expect(1);
  const router = EmberObject.create({ currentRouteName: 'foo' });
  const requestService = Request.create({ router });

  try {
    requestService.streamRequest({
      onResponse: noop,
      modelName: 'foo',
      query: {},
      method: 'foo'
    });
  } catch (err) {
    assert.equal(
      err.message,
      'Invalid socket stream configuration:. model: foo, method: foo',
      'error message from throw error is correct');
  }
});

test('will not break if right things passed into streamRequest', function(assert) {
  assert.expect(1);
  const router = EmberObject.create({ currentRouteName: 'foo' });
  const requestService = Request.create({ router });

  try {
    requestService.streamRequest({
      onResponse: noop,
      modelName: 'foo',
      query: {},
      method: 'foo'
    });
    assert.ok(false, 'error should have been thrown (WebsocketConfigurationNotFoundException)');
  } catch (err) {
    assert.equal(err.name,
      'WebsocketConfigurationNotFoundException',
      'error thrown shows it made it beyond asserts and attempted to create socket');
  }
});

test('will break if wrong things passed into streamRequest', function(assert) {
  assert.expect(4);
  const router = EmberObject.create({ currentRouteName: 'foo' });
  const requestService = Request.create({ router });

  try {
    requestService.streamRequest({
      onResponse: noop,
      modelName: 'foo',
      query: {},
      method: undefined
    });
  } catch (err) {
    assert.equal(
      err.message,
      'Assertion Failed: Cannot call streamRequest without method',
      'error was thrown because method not set');
  }

  try {
    requestService.streamRequest({
      onResponse: noop,
      modelName: null,
      query: {},
      method: 'foo'
    });
  } catch (err) {
    assert.equal(
      err.message,
      'Assertion Failed: Cannot call streamRequest without modelName',
      'error was thrown because modelName not set');
  }

  try {
    requestService.streamRequest({
      onResponse: noop,
      modelName: 'bar',
      query: undefined,
      method: 'foo'
    });
  } catch (err) {
    assert.equal(
      err.message,
      'Assertion Failed: Cannot call streamRequest without query',
      'error was thrown because query not set');
  }

  try {
    requestService.streamRequest({
      onResponse: undefined,
      modelName: 'bar',
      query: {},
      method: 'foo'
    });
  } catch (err) {
    assert.equal(
      err.message,
      'Assertion Failed: Cannot call streamRequest without onResponse',
      'error was thrown because query not set');
  }
});

test('pagedStreamRequest will not break when right things passed in', function(assert) {
  assert.expect(1);
  const router = EmberObject.create({ currentRouteName: 'foo' });
  const requestService = Request.create({ router });

  try {
    requestService.pagedStreamRequest({
      onResponse: noop,
      modelName: 'foo',
      query: {},
      method: 'foo'
    });
    assert.ok(false, 'error should have been thrown (WebsocketConfigurationNotFoundException)');
  } catch (err) {
    assert.equal(
      err.name,
      'WebsocketConfigurationNotFoundException',
      'error thrown shows it made it beyond asserts and attempted to create socket'
    );
  }
});

test('will break if wrong things passed into promiseRequest', function(assert) {
  assert.expect(3);
  const router = EmberObject.create({ currentRouteName: 'foo' });
  const requestService = Request.create({ router });

  try {
    requestService.promiseRequest({
      modelName: 'foo',
      query: {},
      method: undefined
    });
  } catch (err) {
    assert.equal(
      err.message,
      'Assertion Failed: Cannot call promiseRequest without method',
      'error was thrown because method not set'
    );
  }

  try {
    requestService.promiseRequest({
      modelName: null,
      query: {},
      method: 'foo'
    });
  } catch (err) {
    assert.equal(
      err.message,
      'Assertion Failed: Cannot call promiseRequest without modelName',
      'error was thrown because modelName not set'
    );
  }

  try {
    requestService.promiseRequest({
      modelName: 'bar',
      query: undefined,
      method: 'foo'
    });
  } catch (err) {
    assert.equal(
      err.message,
      'Assertion Failed: Cannot call promiseRequest without query',
      'error was thrown because query not set'
    );
  }
});

test('will break if wrong things passed into pagedStreamRequest', function(assert) {
  assert.expect(4);
  const router = EmberObject.create({ currentRouteName: 'foo' });
  const requestService = Request.create({ router });

  try {
    requestService.pagedStreamRequest({
      modelName: 'foo',
      query: {},
      method: undefined,
      onResponse() {}
    });
  } catch (err) {
    assert.equal(
      err.message,
      'Assertion Failed: Cannot call pagedStreamRequest without method',
      'error was thrown because method not set'
    );
  }

  try {
    requestService.pagedStreamRequest({
      modelName: null,
      query: {},
      method: 'foo',
      onResponse() {}
    });
  } catch (err) {
    assert.equal(
      err.message,
      'Assertion Failed: Cannot call pagedStreamRequest without modelName',
      'error was thrown because modelName not set');
  }

  try {
    requestService.pagedStreamRequest({
      modelName: 'bar',
      query: undefined,
      method: 'foo',
      onResponse() {}
    });
  } catch (err) {
    assert.equal(
      err.message,
      'Assertion Failed: Cannot call pagedStreamRequest without query',
      'error was thrown because query not set'
    );
  }

  try {
    requestService.pagedStreamRequest({
      modelName: 'bar',
      query: undefined,
      method: 'foo'
    });
  } catch (err) {
    assert.equal(
      err.message,
      'Assertion Failed: Cannot call pagedStreamRequest without onResponse',
      'error was thrown because onResponse not set'
    );
  }
});


test('promiseRequest will throw socket config error if environment config not present', function(assert) {
  assert.expect(1);
  const router = EmberObject.create({ currentRouteName: 'foo' });
  const requestService = Request.create({ router });

  try {
    requestService.promiseRequest({
      modelName: 'foo',
      query: {},
      method: 'foo'
    });
  } catch (err) {
    assert.equal(
      err.message,
      'Invalid socket stream configuration:. model: foo, method: foo',
      'error message from throw error is correct');
  }
});

test('pagedStreamRequest will throw socket config error if environment config not present', function(assert) {
  assert.expect(1);
  const router = EmberObject.create({ currentRouteName: 'foo' });
  const requestService = Request.create({ router });

  try {
    requestService.pagedStreamRequest({
      onResponse: noop,
      modelName: 'foo',
      query: {},
      method: 'foo'
    });
  } catch (err) {
    assert.equal(
      err.message,
      'Invalid socket stream configuration:. model: foo, method: foo',
      'error message from throw error is correct');
  }
});

test('ping resolves to success', function(assert) {
  const done = assert.async();
  assert.expect(1);
  const router = EmberObject.create({ currentRouteName: 'foo' });
  const requestService = Request.create({ router });
  requestService.ping('test-ping').then(() => {
    assert.ok(true, 'should return the socket information');
    done();
  });
});

test('ping resolves to failure', function(assert) {
  const done = assert.async();
  assert.expect(1);
  const router = EmberObject.create({ currentRouteName: 'foo' });
  const requestService = Request.create({ router });
  requestService.ping('test-ping/_fail')
    .then(function() {
      assert.ok(false, 'ping should not have succeeded');
      done();
    })
    .catch(function() {
      assert.ok(true, 'ping should fail');
      done();
    });
});

test('clearPersistentStreamOptions to remove socketUrlPostfix', function(assert) {
  assert.expect(2);
  const router = EmberObject.create({ currentRouteName: 'foo' });
  const requestService = Request.create({ router, persistentStreamOptions: {} });
  requestService.registerPersistentStreamOptions({
    socketUrlPostfix: '123'
  });
  assert.equal(requestService.get('persistentStreamOptions').socketUrlPostfix, '123', 'socketUrlPostfix got added');
  requestService.clearPersistentStreamOptions(['socketUrlPostfix']);
  assert.equal(requestService.get('persistentStreamOptions').socketUrlPostfix, undefined, 'socketUrlPostfix got removed');
});

test('added and deleted two properties in persistentStreamOptions', function(assert) {
  assert.expect(2);
  const router = EmberObject.create({ currentRouteName: 'foo' });
  const requestService = Request.create({ router, persistentStreamOptions: {} });
  requestService.registerPersistentStreamOptions({
    socketUrlPostfix: '123',
    requiredSocketUrl: 'endpoint/socket'
  });
  assert.equal(Object.keys(requestService.get('persistentStreamOptions')).length, 2, 'persistentStreamOptions has two properties');
  requestService.clearPersistentStreamOptions(['socketUrlPostfix', 'requiredSocketUrl']);
  assert.equal(Object.keys(requestService.get('persistentStreamOptions')).length, 0, 'persistentStreamOptions is empty');
});

test('testing updateOpts', function(assert) {
  assert.expect(2);
  const router = EmberObject.create({ currentRouteName: 'foo' });
  const requestService = Request.create({ router, persistentStreamOptions: {} });
  const opts = {
    method: 'test',
    modelName: 'foo',
    query: {},
    streamOptions: {
      id: '123'
    }
  };
  requestService.registerPersistentStreamOptions({
    socketUrlPostfix: '123',
    requiredSocketUrl: 'endpoint/'
  });
  const updateOpts = requestService._updateOpts(opts);
  assert.equal(Object.keys(updateOpts.streamOptions).length, 3, 'persistentStreamOptions is added to opts streamOptions');
  assert.equal(Object.keys(updateOpts).length, 4, 'streamOptions is added to opts');
});

test('added a property in persistentStreamOptions', function(assert) {
  assert.expect(2);
  const router = EmberObject.create({ currentRouteName: 'foo' });
  const requestService = Request.create({ router, persistentStreamOptions: {} });
  requestService.registerPersistentStreamOptions({
    socketUrlPostfix: '123',
    requiredSocketUrl: 'endpoint/socket'
  });
  assert.equal(Object.keys(requestService.get('persistentStreamOptions')).length, 2, 'persistentStreamOptions has two properties');
  requestService.registerPersistentStreamOptions({ param: 'userMode' });
  assert.equal(Object.keys(requestService.get('persistentStreamOptions')).length, 3, 'an extra property got added');
});