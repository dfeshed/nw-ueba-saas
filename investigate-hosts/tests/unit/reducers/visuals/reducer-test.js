import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/visuals/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';

module('Unit | Reducers | Visuals');

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, {
    activeAutorunTab: 'AUTORUNS',
    activeHostDetailTab: 'OVERVIEW',
    isTreeView: true,
    showCancelScanModal: false,
    showDeleteHostsModal: false,
    hostDetailsLoading: false
  });
});

test('The RESET_INPUT_DATA action reset to initial state', function(assert) {
  const previous = Immutable.from({
    activeAutorunTab: 'AUTORUNS',
    activeHostDetailTab: 'FILES',
    isTreeView: true,
    showCancelScanModal: false,
    showDeleteHostsModal: false,
    hostDetailsLoading: true
  });

  const expectedEndState = {
    activeAutorunTab: 'AUTORUNS',
    activeHostDetailTab: 'OVERVIEW',
    isTreeView: true,
    showCancelScanModal: false,
    showDeleteHostsModal: false,
    hostDetailsLoading: false
  };

  const result = reducer(previous, { type: ACTION_TYPES.RESET_INPUT_DATA });

  assert.deepEqual(result, expectedEndState);
});

test('The CHANGE_DETAIL_TAB action sets the newly selected tab to state', function(assert) {
  const previous = Immutable.from({
    activeHostDetailTab: 'FILES'
  });

  const expectedEndState = {
    activeHostDetailTab: 'OVERVIEW'
  };

  const result = reducer(previous, { type: ACTION_TYPES.CHANGE_DETAIL_TAB, payload: { tabName: 'OVERVIEW' } });

  assert.deepEqual(result, expectedEndState);
});

test('The CHANGE_AUTORUNS_TAB action sets the autorun tab', function(assert) {
  const previous = Immutable.from({
    activeAutorunTab: 'SERVICES'
  });

  const expectedEndState = {
    activeAutorunTab: 'TASKS'
  };

  const result = reducer(previous, { type: ACTION_TYPES.CHANGE_AUTORUNS_TAB, payload: { tabName: 'TASKS' } });

  assert.deepEqual(result, expectedEndState);
});

test('The TOGGLE_CANCEL_SCAN_MODAL toggles the cancel scan modal state', function(assert) {
  const previous = Immutable.from({
    showCancelScanModal: false
  });

  const expectedEndState = {
    showCancelScanModal: true
  };

  const result = reducer(previous, { type: ACTION_TYPES.TOGGLE_CANCEL_SCAN_MODAL });

  assert.deepEqual(result, expectedEndState);
});

test('The TOGGLE_DELETE_HOSTS_MODAL toggles the delete hosts modal state', function(assert) {
  const previous = Immutable.from({
    showDeleteHostsModal: false
  });

  const expectedEndState = {
    showDeleteHostsModal: true
  };

  const result = reducer(previous, { type: ACTION_TYPES.TOGGLE_DELETE_HOSTS_MODAL });

  assert.deepEqual(result, expectedEndState);
});

test('The TOGGLE_PROCESS_VIEW action toggles the tree view state', function(assert) {
  const previous = Immutable.from({
    isTreeView: true
  });

  const expectedEndState = {
    isTreeView: false
  };

  const result = reducer(previous, { type: ACTION_TYPES.TOGGLE_PROCESS_VIEW });

  assert.deepEqual(result, expectedEndState);
});
