/* eslint-disable */

import Ember from 'ember';
import sinon from 'sinon';
import { module, test } from 'qunit';

import Request from 'streaming-data/services/request';

const { Object: EmberObject } = Ember;

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
      'error was thrown because method not set');
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
      'error was thrown because modelName not set');
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
      'error was thrown because query not set');
  }
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

test('promiseRequest return null if environment config not present', function(assert) {
  assert.expect(1);
  const router = EmberObject.create({ currentRouteName: 'foo' });
  const requestService = Request.create({ router });

  const output = requestService.promiseRequest({
    modelName: 'foo',
    query: {},
    method: 'foo'
  });

  assert.equal(output, null, 'promiseRequest returns null when no config present');
});
