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

  test('fetchProtocolData method returns proper type(s), payload(s), and/or promise(s)', function(assert) {
    const result = dashboardCardCreators.fetchProtocolData();
    assert.equal(result.type, ACTION_TYPES.LOG_COLLECTOR_FETCH_PROTOCOL_DATA, 'action has the correct type');
    assert.ok(result.promise, 'action has a fetchProtocolData promise');
  });

  test('updateTcpTid method returns proper type(s), payload(s), and/or promise(s)', function(assert) {
    const result = dashboardCardCreators.updateTcpTid('11');
    assert.equal(result.type, ACTION_TYPES.LOG_COLLECTOR_UPDATE_TCP_TID, 'action has the correct type');
    assert.ok(result.payload, 'action has a updateTcpTid payload');
  });

  test('updateTcpValue method returns proper type(s), payload(s), and/or promise(s)', function(assert) {
    const result = dashboardCardCreators.updateTcpValue('23');
    assert.equal(result.type, ACTION_TYPES.LOG_COLLECTOR_UPDATE_TCP_VALUE, 'action has the correct type');
    assert.ok(result.payload, 'action has a updateTcpValue payload');
  });

});
