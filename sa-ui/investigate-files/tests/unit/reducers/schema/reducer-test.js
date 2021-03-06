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
    name: 'score'
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
    schemaLoading: true
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

  assert.equal(newEndState.schema.length, 7, 'expected schema fields present');

});
