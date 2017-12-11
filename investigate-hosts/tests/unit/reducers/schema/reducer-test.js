import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/schema/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';

module('Unit | Reducers | schema');

const schemaResponse = {
  data: {
    'type': 'machine',
    'fields': [
      {
        'name': 'id',
        'description': 'Agent Id',
        'dataType': 'STRING',
        'searchable': true,
        'defaultProjection': true,
        'wrapperType': 'STRING'
      },
      {
        'name': 'machine.agentVersion',
        'description': 'Agent Version',
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
    visibleColumns: []
  });
});


test('The FETCH_ALL_SCHEMAS action sets all the schema to to state', function(assert) {
  const previous = Immutable.from({
    schema: null,
    schemaLoading: false
  });

  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_ALL_SCHEMAS });
  const endState = reducer(previous, action);

  assert.deepEqual(endState, { schema: null, schemaLoading: true });

  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_ALL_SCHEMAS,
    payload: schemaResponse
  });
  const newEndState = reducer(previous, newAction);

  assert.equal(newEndState.schema.length, 2, 'expected two schema fields');

});

test('The UPDATE_COLUMN_VISIBILITY action will toggle the defaultProjection property', function(assert) {
  const previous = Immutable.from({
    schema: [
      {
        'name': 'id',
        'description': 'Agent Id',
        'dataType': 'STRING',
        'searchable': true,
        'visible': true,
        'wrapperType': 'STRING'
      },
      {
        'name': 'machine.agentVersion',
        'description': 'Agent Version',
        'dataType': 'STRING',
        'searchable': true,
        'visible': false,
        'wrapperType': 'STRING'
      }
    ],
    schemaLoading: false
  });

  const result = reducer(previous, { type: ACTION_TYPES.UPDATE_COLUMN_VISIBILITY, payload: { field: 'machine.agentVersion', visible: false } });

  assert.equal(result.schema[1].visible, true, 'expected toggle the property');
});


test('The GET_PREFERENCES  action will set visibleColumns', function(assert) {
  const previous = Immutable.from({
    visibleColumns: []
  });
  const response = {
    machinePreference: {
      visibleColumns: ['id', 'machine.agentVersion']
    }
  };

  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.GET_PREFERENCES,
    payload: response
  });

  const result = reducer(previous, newAction);
  assert.equal(result.visibleColumns.length, 2, 'visible columns length is properly set');
});
