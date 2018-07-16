import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import interactionCreators from 'investigate-events/actions/interaction-creators';
import ACTION_TYPES from 'investigate-events/actions/types';

module('Unit | Actions | interaction creators', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('setQueryView action creator returns proper type and payload', function(assert) {
    const action = interactionCreators.setQueryView('foo');
    assert.equal(action.type, ACTION_TYPES.SET_QUERY_VIEW, 'action has the correct type');
    assert.deepEqual(action.payload, { queryView: 'foo' }, 'payload has correct data');
  });
});