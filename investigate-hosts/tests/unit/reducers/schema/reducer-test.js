import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/schema/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';

module('Unit | Reducers | schema');

const SCHEMA = [
  {
    'name': 'machine.machineOsType',
    'visible': true
  },
  {
    'name': 'machine.machineName',
    'visible': false
  },
  {
    name: 'machine.scanStartTime'
  },
  {
    name: 'machine.users.name'
  },
  {
    name: 'agentStatus.lastSeenTime'
  },
  {
    name: 'agentStatus.scanStatus'
  }
];

const schemaResponse = {
  data: {
    'type': 'machine',
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

  assert.equal(newEndState.schema.length, 6, 'expected schema fields present');

});

test('The UPDATE_COLUMN_VISIBILITY action will toggle the defaultProjection property', function(assert) {
  const previous = Immutable.from({
    schema: SCHEMA,
    schemaLoading: false
  });

  const result = reducer(previous, { type: ACTION_TYPES.UPDATE_COLUMN_VISIBILITY, payload: { field: 'machine.machineOsType', visible: false } });

  assert.equal(result.schema[0].visible, true, 'expected toggle the property');
});


test('The GET_PREFERENCES  action will set visibleColumns', function(assert) {
  const previous = Immutable.from({
    schema: SCHEMA
  });
  const response = {
    machinePreference: {
      visibleColumns: ['machine.machineName', 'machine.machineOsType']
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


