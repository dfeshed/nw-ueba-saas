import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import {
  isBrokerView
} from 'investigate-shared/selectors/broker-load-more/selectors';

module('Unit | Selectors | broker-load-more', function(hooks) {

  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('isBrokerView returns true when a server is broker', function(assert) {
    const state = Immutable.from({
      serverId: '123',
      servers: [{ id: '123', name: 'endpoint-broker-server' }]
    });
    const data = isBrokerView(state);
    assert.equal(data, true);
  });

  test('isBrokerView returns false when a server is not broker', function(assert) {
    const state = Immutable.from({
      serverId: '123',
      servers: [{ id: '123', name: 'endpoint-server' }]
    });
    const data = isBrokerView(state);
    assert.equal(data, false);
  });

  test('isBrokerView returns false when the servers is undefined', function(assert) {
    const state = Immutable.from({
      serverId: '123',
      servers: undefined
    });
    const data = isBrokerView(state);
    assert.equal(data, false);
  });

  test('isBrokerView returns false when the state is an empty object', function(assert) {
    const state = {};
    const data = isBrokerView(state);
    assert.equal(data, false);
  });
});
