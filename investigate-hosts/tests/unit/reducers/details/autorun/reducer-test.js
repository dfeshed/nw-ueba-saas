import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/details/autorun/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../../helpers/make-pack-action';
import { autorunsData } from '../../../state/state';
import _ from 'lodash';

module('Unit | Reducers | autorun');

const initialState = {
  autorun: null,
  service: null,
  task: null,
  autorunLoadingStatus: null,
  serviceLoadingStatus: null,
  taskLoadingStatus: null,
  selectedRowId: null
};

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, initialState);
});


test('The RESET_HOST_DETAILS will reset the state', function(assert) {
  const previous = Immutable.from({
    autorun: { 1: { path: '/root', fileProperties: { fileName: 'test' } } }
  });
  const result = reducer(previous, { type: ACTION_TYPES.RESET_HOST_DETAILS });

  assert.deepEqual(result, initialState);
});

test('The SET_AUTORUN_SELECTED_ROW will reset the state', function(assert) {
  const previous = Immutable.from({
    selectedRowId: null
  });
  const result = reducer(previous, { type: ACTION_TYPES.SET_AUTORUN_SELECTED_ROW, payload: { id: 123 } });

  assert.equal(result.selectedRowId, 123, 'Expected to match the selected id 123');
});

test('The FETCH_FILE_CONTEXT_AUTORUNS sets the host details information', function(assert) {
  const previous = Immutable.from({ autorun: null, autorunLoadingStatus: 'completed' });

  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_FILE_CONTEXT_AUTORUNS });
  const startEndState = reducer(previous, startAction);
  assert.deepEqual(startEndState.autorunLoadingStatus, 'wait');

  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_FILE_CONTEXT_AUTORUNS,
    payload: { data: autorunsData }
  });

  const endState = reducer(previous, action);
  assert.deepEqual(_.values(endState.autorun).length, 1);
});

test('The FETCH_FILE_CONTEXT_SERVICES sets the host details information', function(assert) {
  const previous = Immutable.from({ service: null, serviceLoadingStatus: 'completed' });

  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_FILE_CONTEXT_SERVICES });
  const startEndState = reducer(previous, startAction);
  assert.deepEqual(startEndState.serviceLoadingStatus, 'wait');

  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_FILE_CONTEXT_SERVICES,
    payload: { data: autorunsData }
  });
  const endState = reducer(previous, action);
  assert.deepEqual(_.values(endState.service).length, 1);
});

test('The FETCH_FILE_CONTEXT_TASKS sets the host details information', function(assert) {
  const previous = Immutable.from({ task: null, taskLoadingStatus: 'completed' });

  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_FILE_CONTEXT_TASKS });
  const startEndState = reducer(previous, startAction);
  assert.deepEqual(startEndState.taskLoadingStatus, 'wait');

  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_FILE_CONTEXT_TASKS,
    payload: { data: autorunsData }
  });
  const endState = reducer(previous, action);
  assert.deepEqual(_.values(endState.task).length, 2);
});

test('The CHANGE_AUTORUNS_TAB resets the selected row id', function(assert) {
  const previous = Immutable.from({
    selectedRowId: '123'
  });
  const result = reducer(previous, { type: ACTION_TYPES.CHANGE_AUTORUNS_TAB });
  assert.equal(result.selectedFileId, null);
});

test('The HOST_DETAILS_DATATABLE_SORT_CONFIG resets the selected row id', function(assert) {
  const previous = Immutable.from({
    selectedRowId: '123'
  });
  const result = reducer(previous, { type: ACTION_TYPES.CHANGE_AUTORUNS_TAB });
  assert.equal(result.selectedFileId, null);
});

test('The SET_AUTORUN_SELECTED_ROW set the selected row id to state', function(assert) {
  const previous = Immutable.from({
    selectedRowId: null
  });
  const result = reducer(previous, { type: ACTION_TYPES.CHANGE_AUTORUNS_TAB, payload: { id: '123' } });
  assert.equal(result.selectedFileId, null);
});
