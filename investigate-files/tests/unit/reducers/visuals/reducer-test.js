import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-files/reducers/visuals/reducer';
import * as ACTION_TYPES from 'investigate-files/actions/types';

module('Unit | Reducers | visuals');

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, {
    activeFileDetailTab: 'OVERVIEW',
    activeDataSourceTab: 'FILE_DETAILS'
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

test('The CHANGE_DATASOURCE_TAB action sets the newly selected tab to state', function(assert) {
  const previous = Immutable.from({
    activeDataSourceTab: 'ALERT'
  });
  const expectedEndState = { activeDataSourceTab: 'INCIDENT' };
  const result = reducer(previous, { type: ACTION_TYPES.CHANGE_DATASOURCE_TAB, payload: { tabName: 'INCIDENT' } });
  assert.deepEqual(result, expectedEndState);
});