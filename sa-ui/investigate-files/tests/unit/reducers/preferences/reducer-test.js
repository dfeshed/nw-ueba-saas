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
      columnConfig: [{
        tableId: 'files',
        columns: [
          {
            field: 'machineCount',
            width: '7vw',
            displayIndex: 3
          }
        ]
      }],
      sortField: null
    },
    machinePreference: null
  }
});

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, {
    preferences: {
      machinePreference: {
        sortField: '{ "key": "score", "descending": true }'
      }
    }
  });
});

test('The SET_FILE_PREFERENCES  action will set visibleColumns', function(assert) {

  const response = {
    filePreference: {
      columnConfig: [{
        tableId: 'files',
        columns: [
          {
            field: 'size',
            width: '7vw',
            displayIndex: 4
          }
        ]
      }]
    }
  };

  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.SET_FILE_PREFERENCES,
    payload: response
  });

  const result = reducer(preferencesInitialState, newAction);
  assert.equal(result.preferences.filePreference.columnConfig[0].columns.length, 1, 'expected to return 1 column');
});

test('The SET_SORT_BY  action will set sortField', function(assert) {

  const response = { 'sortField': 'firstSeenTime', 'isSortDescending': true };

  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.SET_SORT_BY,
    payload: response
  });

  const result = reducer(preferencesInitialState, newAction);
  assert.equal(result.preferences.filePreference.sortField, '{"sortField":"firstSeenTime","isSortDescending":true}', 'sortField value is set');
});


test('SAVE_COLUMN_CONFIG action will set the columns config', function(assert) {

  const columnToChange = [{
    field: 'machineCount',
    width: 100
  }];
  const result = reducer(preferencesInitialState, { type: ACTION_TYPES.SAVE_COLUMN_CONFIG, payload: { columns: columnToChange } });
  assert.equal(result.preferences.filePreference.columnConfig[0].columns[0].width, '7vw', 'new width, converted to vw is set in preferences state for that column.');
});
