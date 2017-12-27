import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-files/reducers/schema/reducer';
import * as ACTION_TYPES from 'investigate-files/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';

module('Unit | Reducers | schema');
const SCHEMA = [
  {
    name: 'entropy'
  },
  {
    name: 'firstFileName'
  },
  {
    name: 'firstSeenTime'
  },
  {
    name: 'machineOsType'
  },
  {
    name: 'signature.features'
  },
  {
    name: 'size'
  }
];

const schemaResponse = {
  data: {
    'type': 'files',
    'fields': SCHEMA
  }
};

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, {
    schema: null,
    schemaLoading: true,
    preferences: { machinePreference: null, filePreference: null }
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

  assert.equal(newEndState.schema.length, 6, 'expected schema fields present');

});

test('The UPDATE_COLUMN_VISIBILITY action will toggle the defaultProjection property', function(assert) {
  const previous = Immutable.from({
    schema: SCHEMA,
    schemaLoading: false
  });

  const result = reducer(previous, { type: ACTION_TYPES.UPDATE_COLUMN_VISIBILITY, payload: { field: 'entropy', visible: false } });

  assert.equal(result.schema[0].visible, true, 'expected toggle the property');
});

test('The GET_PREFERENCES action will set visibleColumns', function(assert) {
  const previous = Immutable.from({
    schema: SCHEMA
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
  assert.equal(result.schema.filter((item) => item.visible).length, 2, 'visible columns length is set');
});

test('The GET_PREFERENCES action will set default visibleColumns first time', function(assert) {
  const previous = Immutable.from({
    schema: SCHEMA
  });
  const response = {};
  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.GET_PREFERENCES,
    payload: response
  });
  const result = reducer(previous, newAction);
  assert.equal(result.schema.filter((item) => item.visible).length, 6, 'Default visible columns length is set');
});
