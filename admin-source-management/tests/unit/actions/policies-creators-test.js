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
    assert.expect(8);

    const dispatch = (action) => {
      const actionType = action.type || typeof action;
      switch (actionType) {
        case 'function': // ACTION_TYPES.FETCH_POLICIES returns an inner thunk/function
          action(innerDispatch, innerGetState);
          break;
        case ACTION_TYPES.FETCH_ENDPOINT_SERVERS:
          assert.equal(action.type, ACTION_TYPES.FETCH_ENDPOINT_SERVERS, 'action has the correct type of FETCH_ENDPOINT_SERVERS');
          assert.ok(action.promise, 'action has a fetchEndpointServers promise');
          break;
        case ACTION_TYPES.FETCH_LOG_SERVERS:
          assert.equal(action.type, ACTION_TYPES.FETCH_LOG_SERVERS, 'action has the correct type of FETCH_LOG_SERVERS');
          assert.ok(action.promise, 'action has a fetchLogServers promise');
          break;
        case ACTION_TYPES.FETCH_FILE_SOURCE_TYPES:
          assert.equal(action.type, ACTION_TYPES.FETCH_FILE_SOURCE_TYPES, 'action has the correct type of FETCH_FILE_SOURCE_TYPES');
          assert.ok(action.promise, 'action has a fetchFileSourceTypes promise');
          break;
        default:
          assert.equal(true, false, 'default case... action has the correct type');
      }
    };

    const innerDispatch = (action) => {
      switch (action.type) {
        case ACTION_TYPES.FETCH_POLICIES:
          assert.equal(action.type, ACTION_TYPES.FETCH_POLICIES, 'action has the correct type of FETCH_POLICIES');
          assert.ok(action.promise, 'action has a fetchPolicies promise');
          break;
        default:
          assert.equal(true, false, 'default inner case... action has the correct type');
      }
    };

    const innerGetState = () => {
      return {
        usm: {
          policies: {
            sortField: 'name',
            isSortDescending: false
          },
          policiesFilter: {
            expressionList: []
          }
        }
      };
    };

    const thunk = policiesCreators.initializePolicies();
    thunk(dispatch);
  });
});