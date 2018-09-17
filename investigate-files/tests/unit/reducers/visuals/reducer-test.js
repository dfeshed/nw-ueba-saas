import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-files/reducers/visuals/reducer';
import * as ACTION_TYPES from 'investigate-files/actions/types';

module('Unit | Reducers | visuals');

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, {
    activeFileDetailTab: 'OVERVIEW',
    activeDetailAlertTab: 'CRITICAL'
  });
});

test('The CHANGE_FILE_DETAIL_TAB sets new tab to state', function(assert) {
  const previous = Immutable.from({
    activeFileDetailTab: 'OVERVIEW'
  });

  const expectedEndState = {
    activeFileDetailTab: 'ANALYSIS'
  };

  const endState = reducer(previous, { type: ACTION_TYPES.CHANGE_FILE_DETAIL_TAB, payload: { tabName: 'ANALYSIS' } });
  assert.deepEqual(endState, expectedEndState);
});

test('The CHANGE_DETAIL_ALERT_TAB sets new tab to state', function(assert) {
  const previous = Immutable.from({
    activeDetailAlertTab: 'CRITICAL'
  });

  const expectedEndState = {
    activeDetailAlertTab: 'HIGH'
  };

  const endState = reducer(previous, { type: ACTION_TYPES.CHANGE_DETAIL_ALERT_TAB, payload: { tabName: 'HIGH' } });
  assert.deepEqual(endState, expectedEndState);
});