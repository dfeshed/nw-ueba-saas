import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-files/reducers/preferences/reducer';
import * as ACTION_TYPES from 'investigate-files/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';

module('Unit |  Reducers | Preferences');

const preferencesInitialState = Immutable.from({
  preferences: {
    filePreference: {
      visibleColumns: [],
      sortField: null
    },
    machinePreference: null
  }
});

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, {
    preferences: {
      filePreference: {
        visibleColumns: [
          'firstFileName',
          'firstSeenTime',
          'reputationStatus',
          'score',
          'size',
          'signature.features',
          'pe.resources.company',
          'fileStatus',
          'remediationAction',
          'downloadInfo.status',
          'size',
          'signature.features',
          'firstSeenTime',
          'machineOsType'
        ],
        sortField: '{ "sortField": "score", "isSortDescending": false }'
      }
    }
  });
});


test('The UPDATE_COLUMN_VISIBILITY action will set the selected column', function(assert) {

  const result = reducer(preferencesInitialState, { type: ACTION_TYPES.UPDATE_COLUMN_VISIBILITY, payload: { field: 'fileFirstName', selected: true } });
  assert.equal(result.preferences.filePreference.visibleColumns.length, 1, 'expected to have one column');
  assert.equal(result.preferences.filePreference.visibleColumns[0], 'fileFirstName', 'expected to match fileFirstName');
});

test('The UPDATE_COLUMN_VISIBILITY  action unchecking column', function(assert) {
  const response = Immutable.from({
    preferences: {
      filePreference: {
        visibleColumns: ['fileFirstName', 'score', 'machineCount'],
        sortField: null
      },
      machinePreference: null
    }
  });

  const result = reducer(response, { type: ACTION_TYPES.UPDATE_COLUMN_VISIBILITY, payload: { field: 'machineCount', selected: false } });
  assert.equal(result.preferences.filePreference.visibleColumns.length, 2, 'expected to return 2 column');
});

test('The SET_FILE_PREFERENCES  action will set visibleColumns', function(assert) {

  const response = {
    filePreference: {
      visibleColumns: ['fileFirstName']
    }
  };

  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.SET_FILE_PREFERENCES,
    payload: response
  });

  const result = reducer(preferencesInitialState, newAction);
  assert.equal(result.preferences.filePreference.visibleColumns.length, 1, 'expected to return 1 column');
});

test('The SET_SORT_BY  action will set sortField', function(assert) {
  // Initial state
  const initialResult = reducer(undefined, {});
  assert.equal(initialResult.preferences.filePreference.sortField, '{ "sortField": "score", "isSortDescending": false }', 'original sortField value');

  const response = { 'sortField': 'firstSeenTime', 'isSortDescending': true };

  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.SET_SORT_BY,
    payload: response
  });

  const result = reducer(preferencesInitialState, newAction);
  assert.equal(result.preferences.filePreference.sortField, '{"sortField":"firstSeenTime","isSortDescending":true}', 'sortField value is set');
});
