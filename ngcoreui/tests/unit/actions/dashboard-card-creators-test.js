import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import * as dashboardCardCreators from 'ngcoreui/actions/creators/logcollector/dashboard-card-creators';
import ACTION_TYPES from 'ngcoreui/actions/types';

module('Unit | Actions | dashboardCard creators', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('fetchProtocols method returns proper type(s), payload(s), and/or promise(s)', function(assert) {
    const result = dashboardCardCreators.fetchProtocols();
    assert.equal(result.type, ACTION_TYPES.LOG_COLLECTOR_FETCH_PROTOCOLS, 'action has the correct type');
    assert.ok(result.promise, 'action has a fetchProtocolList promise');
  });

  test('fetchEventRate method returns proper type(s), payload(s), and/or promise(s)', function(assert) {
    const obj = [];
    const result = dashboardCardCreators.fetchEventRate(obj);
    assert.equal(result.type, ACTION_TYPES.LOG_COLLECTOR_FETCH_EVENT_RATE, 'action has the correct type');
    assert.ok(result.promise, 'action has a fetchEventRate promise');
  });

  test('fetchBytesRate method returns proper type(s), payload(s), and/or promise(s)', function(assert) {
    const obj = [];
    const result = dashboardCardCreators.fetchBytesRate(obj);
    assert.equal(result.type, ACTION_TYPES.LOG_COLLECTOR_FETCH_BYTE_RATE, 'action has the correct type');
    assert.ok(result.promise, 'action has a fetchBytesRate promise');
  });

  test('fetchErrorsRate method returns proper type(s), payload(s), and/or promise(s)', function(assert) {
    const obj = [];
    const result = dashboardCardCreators.fetchErrorsRate(obj);
    assert.equal(result.type, ACTION_TYPES.LOG_COLLECTOR_FETCH_ERROR_RATE, 'action has the correct type');
    assert.ok(result.promise, 'action has a fetchErrorsRate promise');
  });

  test('fetchTotalEvents method returns proper type(s), payload(s), and/or promise(s)', function(assert) {
    const obj = [];
    const result = dashboardCardCreators.fetchTotalEvents(obj);
    assert.equal(result.type, ACTION_TYPES.LOG_COLLECTOR_FETCH_TOTAL_EVENTS, 'action has the correct type');
    assert.ok(result.promise, 'action has a fetchTotalEvents promise');
  });

  test('fetchTotalBytes method returns proper type(s), payload(s), and/or promise(s)', function(assert) {
    const obj = [];
    const result = dashboardCardCreators.fetchTotalBytes(obj);
    assert.equal(result.type, ACTION_TYPES.LOG_COLLECTOR_FETCH_TOTAL_BYTES, 'action has the correct type');
    assert.ok(result.promise, 'action has a fetchTotalBytes promise');
  });

  test('fetchTotalErrors method returns proper type(s), payload(s), and/or promise(s)', function(assert) {
    const obj = [];
    const result = dashboardCardCreators.fetchTotalErrors(obj);
    assert.equal(result.type, ACTION_TYPES.LOG_COLLECTOR_FETCH_TOTAL_ERRORS, 'action has the correct type');
    assert.ok(result.promise, 'action has a fetchTotalErrors promise');
  });
});
