import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-process-analysis/reducers/process-visuals/reducer';
import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';

const initialState = Immutable.from({
  detailsTabSelected: 'Properties'
});

module('Unit | Reducers | process-visuals', function() {

  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    assert.deepEqual(result, initialState);
  });

  test('SET_DETAILS_TAB set selected tab', function(assert) {
    const result = reducer(initialState, { type: ACTION_TYPES.SET_DETAILS_TAB, payload: 'Events' });
    assert.equal(result.detailsTabSelected, 'Events', 'Selected tab as been updated in the State');
  });

});