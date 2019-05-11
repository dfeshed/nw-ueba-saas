import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-process-analysis/reducers/filter-popup/reducer';
import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';

const initialState = Immutable.from({
  activeFilterTab: 'all'
});


module('Unit | Reducers | filter-popup', function() {

  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    assert.deepEqual(result, initialState);
  });

  test('SET_ACTIVE_EVENT_FILTER_TAB sets active tab', function(assert) {
    const result = reducer(initialState, { type: ACTION_TYPES.SET_ACTIVE_EVENT_FILTER_TAB, payload: { tabName: 'registry' } });
    assert.equal(result.activeFilterTab, 'registry', 'Selected tab is set');
  });
});