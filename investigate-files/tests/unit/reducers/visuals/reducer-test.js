import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-files/reducers/visuals/reducer';
import * as ACTION_TYPES from 'investigate-files/actions/types';

module('Unit | Reducers | visuals');

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, {
    activeFileDetailTab: 'OVERVIEW',
    activeRiskSeverityTab: 'critical'
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

test('The ACTIVE_RISK_SEVERITY_TAB sets new tab to state', function(assert) {
  const previous = Immutable.from({
    activeRiskSeverityTab: 'critical'
  });

  const expectedEndState = {
    activeRiskSeverityTab: 'high'
  };

  const endState = reducer(previous, { type: ACTION_TYPES.ACTIVE_RISK_SEVERITY_TAB, payload: { tabName: 'high' } });
  assert.deepEqual(endState, expectedEndState);
});

test('The RESET_RISK_CONTEXT sets new tab to state', function(assert) {
  const previous = Immutable.from({
    activeRiskSeverityTab: 'high'
  });
  const result = reducer(previous, { type: ACTION_TYPES.RESET_RISK_CONTEXT });
  assert.equal(result.activeRiskSeverityTab, 'critical', 'Active tab is reset to critical');
});
