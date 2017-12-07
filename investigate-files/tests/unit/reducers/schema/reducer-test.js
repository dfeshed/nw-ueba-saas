import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-files/reducers/schema/reducer';
import * as ACTION_TYPES from 'investigate-files/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';

module('Unit | Reducers | schema');

const schemaResponse = {
  data: {
    'type': 'files',
    'fields': [
      {
        'name': 'entropy',
        'dataType': 'DOUBLE',
        'searchable': true,
        'defaultProjection': false,
        'wrapperType': 'NUMBER'
      },
      {
        'name': 'firstFileName',
        'dataType': 'STRING',
        'searchable': true,
        'defaultProjection': true,
        'wrapperType': 'STRING'
      }
    ]
  }
};

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, {
    schema: null,
    schemaLoading: true,
    visibleColumns: [],
    userProjectionChanged: false
  });
});

test('The SCHEMA_RETRIEVE action sets all the schema to the state', function(assert) {
  const previous = Immutable.from({
    schema: null,
    schemaLoading: false
  });

  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.SCHEMA_RETRIEVE });
  const endState = reducer(previous, action);

  assert.deepEqual(endState, { schema: null, schemaLoading: true });

  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.SCHEMA_RETRIEVE,
    payload: schemaResponse
  });
  const newEndState = reducer(previous, newAction);

  assert.equal(newEndState.schema.length, 2, 'expected two schema fields');

});

test('The UPDATE_COLUMN_VISIBILITY action will toggle the defaultProjection property', function(assert) {
  const previous = Immutable.from({
    schema: [
      {
        'name': 'entropy',
        'dataType': 'DOUBLE',
        'searchable': true,
        'defaultProjection': false,
        'wrapperType': 'NUMBER'
      },
      {
        'name': 'firstFileName',
        'dataType': 'STRING',
        'searchable': true,
        'defaultProjection': true,
        'wrapperType': 'STRING'
      }
    ],
    schemaLoading: false
  });

  const result = reducer(previous, { type: ACTION_TYPES.UPDATE_COLUMN_VISIBILITY, payload: { field: 'entropy', visible: false } });

  assert.equal(result.schema[1].defaultProjection, true, 'expected toggle the property');
  assert.equal(result.userProjectionChanged, true, 'projection changed flag is set');
});

test('The GET_PREFERENCES action will set visibleColumns', function(assert) {
  const previous = Immutable.from({
    visibleColumns: []
  });
  const response = {
    filePreference: {
      visibleColumns: ['firstFileName', 'entropy']
    }
  };
  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.GET_PREFERENCES,
    payload: response
  });

  const result = reducer(previous, newAction);
  assert.equal(result.visibleColumns.length, 2, 'visible columns length is properly set');
});