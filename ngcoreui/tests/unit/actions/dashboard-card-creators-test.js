import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { initializeProtocols } from 'ngcoreui/actions/creators/logcollector/dashboard-card-creators';

import ACTION_TYPES from 'ngcoreui/actions/types';

module('Unit | Actions | dashboardCard creators', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('initializeProtocols action creator returns proper type(s), payload(s), and/or promise(s)', function(assert) {
    assert.expect(2);
    const dispatch = (action) => {
      const actionType = action.type || typeof action;
      switch (actionType) {
        case 'function':
          action(innerDispatch, innerGetState);
          break;
        case ACTION_TYPES.LOG_COLLECTOR_FETCH_PROTOCOLS:
          assert.equal(action.type, ACTION_TYPES.LOG_COLLECTOR_FETCH_PROTOCOLS, 'action has the correct type');
          assert.ok(action.promise, 'action has a fetchProtocolList promise');
          break;
        default:
          assert.ok(false, 'default inner case... action has the correct type');
      }
    };

    const innerDispatch = (action) => {
      switch (action.type) {
        case ACTION_TYPES.LOG_COLLECTOR_FETCH_PROTOCOLS:
          assert.equal(action.type, ACTION_TYPES.LOG_COLLECTOR_FETCH_PROTOCOLS, 'action has the correct type');
          assert.ok(action.promise, 'action has a fetchProtocolList promise');
          break;
        default:
          assert.ok(false, 'default inner case... action has the correct type');
      }
    };

    const innerGetState = () => {
      return {
      };
    };

    const thunk = initializeProtocols();
    thunk(dispatch);
  });
});
