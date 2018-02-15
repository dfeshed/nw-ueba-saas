import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/preferences/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';

module('Unit |  Reducers | Preferences');

const preferencesInitialState = Immutable.from({
  preferences: {
    machinePreference: {
      visibleColumns: [],
      sortField: null
    },
    filePreference: null
  }
});

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, {
    preferences: {
      machinePreference: {
        visibleColumns: [
          'machine.machineOsType',
          'machine.scanStartTime',
          'machine.users.name',
          'agentStatus.lastSeenTime',
          'agentStatus.scanStatus'
        ],
        sortField: '{ "key": "machine.scanStartTime", "descending": true }'
      }
    }
  });
});


test('The UPDATE_COLUMN_VISIBILITY action will set the selected column', function(assert) {

  let result = reducer(preferencesInitialState, { type: ACTION_TYPES.UPDATE_COLUMN_VISIBILITY, payload: { field: 'machine.machineOsType', selected: true } });
  assert.equal(result.preferences.machinePreference.visibleColumns.length, 1, 'expected to have one column');
  assert.equal(result.preferences.machinePreference.visibleColumns[0], 'machine.machineOsType', 'expected to match machine.machineOsType');

  const previousState = Immutable.from({
    preferences: {
      machinePreference: {
        visibleColumns: ['machine.machineOsType']
      }
    }
  });
  result = reducer(previousState, { type: ACTION_TYPES.UPDATE_COLUMN_VISIBILITY, payload: { field: 'machine.machineOsType', selected: false } });
  assert.equal(result.preferences.machinePreference.visibleColumns.length, 0);
});


test('The SET_PREFERENCES  action will set visibleColumns', function(assert) {

  const response = {
    machinePreference: {
      visibleColumns: ['machine.machineOsType']
    }
  };

  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.SET_PREFERENCES,
    payload: response
  });

  const result = reducer(preferencesInitialState, newAction);
  assert.equal(result.preferences.machinePreference.visibleColumns.length, 1, 'expected to return 1 column');
});

test('The SET_HOST_COLUMN_SORT  action will set visibleColumns', function(assert) {

  const previousState = Immutable.from({
    preferences: {
      machinePreference: {
        sortField: null
      }
    }
  });
  const result = reducer(previousState, { type: ACTION_TYPES.SET_HOST_COLUMN_SORT, payload: { key: 'machine.machineName', descending: true } });
  assert.equal(result.preferences.machinePreference.sortField, '{"key":"machine.machineName","descending":true}');
});

