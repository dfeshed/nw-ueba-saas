import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/details/anomalies/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../../helpers/make-pack-action';
import { hooksData } from '../../../state/state';
import _ from 'lodash';

module('Unit | Reducers | Anomalies');

const initialState = {
  hooks: null,
  threads: null,
  hooksLoadingStatus: null,
  threadsLoadingStatus: null,
  selectedRowId: null
};


test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, initialState);
});


test('The RESET_HOST_DETAILS will reset the state', function(assert) {
  const previous = Immutable.from({
    hooks: { hooks_1: {
      id: 'hooks_4',
      machineOsType: 'windows',
      machineName: 'WIN-BKA6OVQGQI0',
      machineAgentId: '3B1C9364-F6D1-6E1F-0552-B0F85F72AA70',
      windows: { hooks: [{ type: 'inline' }] }
    }
    }
  });
  const result = reducer(previous, { type: ACTION_TYPES.RESET_HOST_DETAILS });

  assert.deepEqual(result, initialState);
});

test('The SET_ANOMALIES_SELECTED_ROW will reset the state', function(assert) {
  const previous = Immutable.from({
    selectedRowId: null
  });
  const result = reducer(previous, { type: ACTION_TYPES.SET_ANOMALIES_SELECTED_ROW, payload: { id: 123 } });

  assert.equal(result.selectedRowId, 123, 'Expected to match the selected id 123');
});

test('The FETCH_FILE_CONTEXT_HOOKS sets the host details information', function(assert) {
  const previous = Immutable.from({ hooks: null, hooksLoadingStatus: 'completed' });

  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_FILE_CONTEXT_HOOKS });
  const startEndState = reducer(previous, startAction);
  assert.deepEqual(startEndState.hooksLoadingStatus, 'wait');

  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_FILE_CONTEXT_HOOKS,
    payload: { data: hooksData }
  });

  const endState = reducer(previous, action);
  assert.deepEqual(_.values(endState.hooks).length, 7);
});

test('The CHANGE_ANOMALIES_TAB resets the selected row id', function(assert) {
  const previous = Immutable.from({
    selectedRowId: '123'
  });
  const result = reducer(previous, { type: ACTION_TYPES.CHANGE_ANOMALIES_TAB });
  assert.equal(result.selectedRowId, null);
});

test('The HOST_DETAILS_DATATABLE_SORT_CONFIG resets the selected row id', function(assert) {
  const previous = Immutable.from({
    selectedRowId: '123'
  });
  const result = reducer(previous, { type: ACTION_TYPES.HOST_DETAILS_DATATABLE_SORT_CONFIG });
  assert.equal(result.selectedRowId, null);
});

test('The FETCH_FILE_CONTEXT_THREADS sets the host details information', function(assert) {
  const previous = Immutable.from({ threads: null, threadsLoadingStatus: 'completed' });

  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_FILE_CONTEXT_THREADS });
  const startEndState = reducer(previous, startAction);
  assert.deepEqual(startEndState.threadsLoadingStatus, 'wait');

  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_FILE_CONTEXT_THREADS,
    payload: { data: hooksData }
  });

  const endState = reducer(previous, action);
  assert.deepEqual(_.values(endState.threads).length, 5);
});