import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import sourcesCreators from 'admin-source-management/actions/creators/sources-creators';
import ACTION_TYPES from 'admin-source-management/actions/types';

module('Unit | Actions | sources creators', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('initializeSources action creator returns proper type(s), payload(s), and/or promise(s)', function(assert) {
    const dispatch = (action) => {
      switch (action.type) {
        case ACTION_TYPES.FETCH_SOURCES:
          assert.equal(action.type, ACTION_TYPES.FETCH_SOURCES, 'action has the correct type of FETCH_SOURCES');
          assert.ok(action.promise, 'action has a fetchSources promise');
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
    const thunk = sourcesCreators.initializeSources();
    thunk(dispatch);
  });
});