import { test, module } from 'qunit';
import reducer from 'preferences/reducers/preferences-panel/reducer';
import * as ACTION_TYPES from 'preferences/actions/types';
import Immutable from 'seamless-immutable';
import { makePackAction, DEFAULT_INITIALIZE } from '../../../helpers/data-helper';
import { LIFECYCLE } from 'redux-pack';

module('Unit | Reducers | preferences-panel | Preferences');

const initialState = Immutable.from({
  isExpanded: true,
  additionalFilters: null,
  preferences: null,
  isClicked: false,
  shouldPublishPreferences: false,
  changedField: null,
  preferencesConfig: {}
});

const preferences = {
  defaultPacketFormat: 'downloadPCAP',
  defaultLogFormat: 'downloadLog',
  currentReconView: 'PACKET',
  isHeaderOpen: true,
  isMetaShown: true,
  isReconExpanded: true,
  isReconOpen: true,
  isRequestShown: true,
  isResponseShown: true
};

let result;

const reducerFunction = (type, payload) => {
  const action = {
    type,
    payload
  };
  return reducer(initialState, action);
};

test('test TOGGLE_PREFERENCES_PANEL action handler', function(assert) {
  result = reducerFunction(ACTION_TYPES.TOGGLE_PREFERENCES_PANEL, {});
  assert.equal(result.isClicked, true);
  assert.equal(result.isExpanded, false);
  // Since its a toggle action, so calling the reducer again to check if the state value toggles
  const updatedResult = reducer(result, {
    type: ACTION_TYPES.TOGGLE_PREFERENCES_PANEL,
    payload: {}
  });
  // isClicked will always remain true whenever toggle happens
  assert.equal(updatedResult.isClicked, true);
  assert.equal(updatedResult.isExpanded, true);
});


test('test CLOSE_PREFERENCES_PANEL action handler', function(assert) {
  result = reducerFunction(ACTION_TYPES.CLOSE_PREFERENCES_PANEL, {});
  assert.equal(result.isExpanded, false);
});

test('test LOAD_PREFERENCES action handler', function(assert) {
  const action = makePackAction(
    LIFECYCLE.SUCCESS,
    {
      type: ACTION_TYPES.LOAD_PREFERENCES,
      payload: DEFAULT_INITIALIZE
    });
  const result = reducer(initialState, action);
  assert.notEqual(result.preferences, null);
});

test('test SAVE_PREFERENCES action handler with success event', function(assert) {
  const action = makePackAction(
    LIFECYCLE.SUCCESS,
    {
      type: ACTION_TYPES.SAVE_PREFERENCES,
      payload: preferences
    });
  result = reducer(initialState, action);
  assert.notEqual(result.preferences, null);
  assert.equal(result.preferences.currentReconView, 'PACKET');
  assert.equal(result.preferences.defaultPacketFormat, 'downloadPCAP');
});

test('test SAVE_PREFERENCES action handler with failure event', function(assert) {
  const action = makePackAction(
    LIFECYCLE.FAILURE,
    {
      type: ACTION_TYPES.SAVE_PREFERENCES,
      payload: preferences
    });
  result = reducer(initialState, action);
  assert.equal(result.preferences, null);
});

test('test UPDATE_PANEL_CLICKED action handler', function(assert) {
  result = reducerFunction(ACTION_TYPES.UPDATE_PANEL_CLICKED, true);
  assert.equal(result.isClicked, true);
});

test('test RESET_PREFERENCES_PANEL action handler', function(assert) {
  result = reducer(initialState, {
    type: ACTION_TYPES.RESET_PREFERENCES_PANEL
  });
  assert.equal(result.isClicked, false);
  assert.equal(result.isExpanded, false);
  assert.equal(result.shouldPublishPreferences, false);
  assert.equal(result.preferences, null);
});
