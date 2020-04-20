import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/visuals/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';

module('Unit | Reducers | Visuals');

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, {
    activeAutorunTab: 'AUTORUNS',
    activeAnomaliesTab: 'IMAGEHOOKS',
    activeHostDetailTab: 'OVERVIEW',
    isTreeView: false,
    showDeleteHostsModal: false,
    activeSystemInformationTab: 'HOST_ENTRIES',
    activePropertyPanelTab: 'HOST_DETAILS',
    isProcessDetailsView: false,
    showHostDetailsFilter: true,
    listAllFiles: true
  });
});

test('The RESET_INPUT_DATA action reset to initial state', function(assert) {
  const previous = Immutable.from({
    activeAutorunTab: 'AUTORUNS',
    activeAnomaliesTab: 'IMAGEHOOKS',
    activeHostDetailTab: 'FILES',
    isTreeView: true,
    showDeleteHostsModal: false,
    activeSystemInformationTab: 'HOST_ENTRIES',
    activePropertyPanelTab: 'POLICIES',
    isProcessDetailsView: false
  });

  const expectedEndState = {
    activeAutorunTab: 'AUTORUNS',
    activeAnomaliesTab: 'IMAGEHOOKS',
    activeHostDetailTab: 'OVERVIEW',
    isTreeView: false,
    showDeleteHostsModal: false,
    activeSystemInformationTab: 'HOST_ENTRIES',
    activePropertyPanelTab: 'HOST_DETAILS',
    isProcessDetailsView: false,
    showHostDetailsFilter: true,
    listAllFiles: true
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

test('The SET_SYSTEM_INFORMATION_TAB action sets the system information tab', function(assert) {
  const previous = Immutable.from({
    activeSystemInformationTab: 'HOST_ENTRIES'
  });

  const expectedEndState = {
    activeSystemInformationTab: 'SECURITY_PRODUCTS'
  };

  const result = reducer(previous, { type: ACTION_TYPES.SET_SYSTEM_INFORMATION_TAB, payload: { tabName: 'SECURITY_PRODUCTS' } });

  assert.deepEqual(result, expectedEndState);
});

test('The SET_PROPERTY_PANEL_TAB action sets the property panel tab', function(assert) {
  const previous = Immutable.from({
    activePropertyPanelTab: 'HOST_DETAILS'
  });

  const expectedEndState = {
    activePropertyPanelTab: 'POLICIES'
  };

  const result = reducer(previous, { type: ACTION_TYPES.SET_PROPERTY_PANEL_TAB, payload: { tabName: 'POLICIES' } });

  assert.deepEqual(result, expectedEndState);
});
test('The TOGGLE_HOST_DETAILS_FILTER sets the showHostDetailsFilter true', function(assert) {
  const previous = Immutable.from({
    activePropertyPanelTab: 'HOST_DETAILS'
  });

  const expectedEndState = {
    activePropertyPanelTab: 'HOST_DETAILS',
    showHostDetailsFilter: true
  };

  const result = reducer(previous, { type: ACTION_TYPES.TOGGLE_HOST_DETAILS_FILTER, payload: { flag: true } });

  assert.deepEqual(result, expectedEndState);
});

test('The TOGGLE_ALL_FILE toggles the All files state', function(assert) {
  const previous = Immutable.from({
    listAllFiles: false
  });

  const expectedEndState = {
    listAllFiles: true
  };

  const result = reducer(previous, { type: ACTION_TYPES.TOGGLE_ALL_FILE });

  assert.deepEqual(result, expectedEndState);
});

test('The TOGGLE_PROCESS_DETAILS toggles based on if process details view', function(assert) {
  const previous = Immutable.from({
    isProcessDetailsView: false
  });

  const expectedEndState = {
    isProcessDetailsView: true
  };

  const result = reducer(previous, { type: ACTION_TYPES.TOGGLE_PROCESS_DETAILS, payload: true });

  assert.deepEqual(result, expectedEndState);
});
