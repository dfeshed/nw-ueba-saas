import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import policiesCreators from 'admin-source-management/actions/creators/policies-creators';
import ACTION_TYPES from 'admin-source-management/actions/types';

module('Unit | Actions | policies creators', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('initializePolicies action creator returns proper type(s), payload(s), and/or promise(s)', function(assert) {
    const dispatch = (action) => {
      switch (action.type) {
        case ACTION_TYPES.FETCH_POLICIES:
          assert.equal(action.type, ACTION_TYPES.FETCH_POLICIES, 'action has the correct type of FETCH_POLICIES');
          assert.ok(action.promise, 'action has a fetchPolicies promise');
          break;
        case ACTION_TYPES.FETCH_ENDPOINT_SERVERS:
          assert.equal(action.type, ACTION_TYPES.FETCH_ENDPOINT_SERVERS, 'action has the correct type of FETCH_ENDPOINT_SERVERS');
          assert.ok(action.promise, 'action has a fetchEndpointServers promise');
          break;
        case ACTION_TYPES.FETCH_LOG_SERVERS:
          assert.equal(action.type, ACTION_TYPES.FETCH_LOG_SERVERS, 'action has the correct type of FETCH_LOG_SERVERS');
          assert.ok(action.promise, 'action has a fetchLogServers promise');
          break;
      }
    };
    const thunk = policiesCreators.initializePolicies();
    thunk(dispatch);
  });
});