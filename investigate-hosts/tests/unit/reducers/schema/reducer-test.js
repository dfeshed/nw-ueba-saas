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
