import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/preferences/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';

module('Unit |  Reducers | Preferences');

const preferencesEmptyState = Immutable.from({
  preferences: {
    machinePreference: {
      columnConfig: [],
      sortField: null
    },
    filePreference: null
  }
});

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, {
    preferences: {
      filePreference: {
        sortField: '{ "sortField": "score", "isSortDescending": false }'
      }
    }
  });
});


test('The SET_PREFERENCES  action will set visibleColumns', function(assert) {

  const response = {
    machinePreference: {
      columnConfig: [{
        columns: [{
          tableId: 'hosts',
          columns: [
            { field: 'machineIdentity.machineOsType' }
          ]
        }]
      }]
    }
  };

  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.SET_PREFERENCES,
    payload: response
  });

  const result = reducer(preferencesEmptyState, newAction);
  assert.equal(result.preferences.machinePreference.columnConfig[0].columns.length, 1, 'expected to return 1 column');
});

test('The SET_HOST_COLUMN_SORT  action will set visibleColumns', function(assert) {

  const previousState = Immutable.from({
    preferences: {
      machinePreference: {
        sortField: null
      }
    }
  });
  const result = reducer(previousState, { type: ACTION_TYPES.SET_HOST_COLUMN_SORT, payload: { key: 'machineIdentity.machineName', descending: true } });
  assert.equal(result.preferences.machinePreference.sortField, '{"key":"machineIdentity.machineName","descending":true}');
});

test('SAVE_COLUMN_CONFIG action will update column config', function(assert) {

  const previousState = Immutable.from({
    preferences: {
      machinePreference: {
        columnConfig: [{
          tableId: 'hosts',
          columns: [
            { field: 'machineIdentity.machineOsType', width: '8vw' }
          ]
        }]
      }
    }
  });

  const result = reducer(previousState, { type: ACTION_TYPES.SAVE_COLUMN_CONFIG, payload: {
    tableId: 'hosts',
    columns: [{ field: 'machineIdentity.machineOsType', width: 100 } ] } });
  assert.equal(result.preferences.machinePreference.columnConfig[0].columns[0].width, '6vw', 'new width, converted to vw is set in preferences state for that column.');
});


test('SAVE_COLUMN_CONFIG action will set the column config', function(assert) {

  const previousState = Immutable.from({
    preferences: {
      machinePreference: {
        columnConfig: [{
          tableId: 'FILE',
          columns: [
            { field: 'firstFileName', width: '8vw' }
          ]
        }]
      }
    }
  });

  const result = reducer(previousState, { type: ACTION_TYPES.SAVE_COLUMN_CONFIG, payload: {
    tableId: 'hosts',
    columns: [{ field: 'machineIdentity.machineOsType', width: 100 } ] } });
  assert.equal(result.preferences.machinePreference.columnConfig.length, 2);
});


test('SAVE_COLUMN_CONFIG action sets the config', function(assert) {

  const previousState = Immutable.from({
    preferences: {
    }
  });

  const result = reducer(previousState, { type: ACTION_TYPES.SAVE_COLUMN_CONFIG, payload: {
    tableId: 'hosts',
    columns: [{ field: 'machineIdentity.machineOsType', width: 100 } ] } });
  assert.equal(result.preferences.machinePreference.columnConfig.length, 1);
});
