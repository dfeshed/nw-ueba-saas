import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../helpers/redux-data-helper';

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

  test('toggleQueryConsole fires when not disabled', function(assert) {
    assert.expect(1);
    const getState = () => {
      return new ReduxDataHelper().queryStats().build();
    };
    const myDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.TOGGLE_QUERY_CONSOLE, 'action has the correct type');
    };
    const thunk = interactionCreators.toggleQueryConsole();
    thunk(myDispatch, getState);
  });

  test('toggleQueryConsole does not fire when disabled', function(assert) {
    const getState = () => {
      return new ReduxDataHelper().queryStats().queryStatsIsEmpty().build();
    };
    const thunk = interactionCreators.toggleQueryConsole();
    assert.equal(thunk(() => {}, getState), undefined);
  });
});
