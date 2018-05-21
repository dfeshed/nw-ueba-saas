import { test, module } from 'qunit';
import reducer from 'investigate-events/reducers/investigate/next-gen/reducer';
import * as ACTION_TYPES from 'investigate-events/actions/types';
import Immutable from 'seamless-immutable';

module('Unit | Reducers | next-gen');

const stateWithPills = Immutable.from({
  pillsData: [1, 2]
});

const emptyState = Immutable.from({
  pillsData: []
});

test('ACTION_TYPES.ADD_NEXT_GEN_PILL adds pill to empty list', function(assert) {
  const action = {
    type: ACTION_TYPES.ADD_NEXT_GEN_PILL,
    payload: {
      pillData: { foo: 1234 },
      position: 0
    }
  };
  const result = reducer(emptyState, action);

  assert.equal(result.pillsData.length, 1);
  assert.equal(result.pillsData[0].foo, 1234);
});

test('ACTION_TYPES.ADD_NEXT_GEN_PILL adds pill to beginning of list', function(assert) {
  const action = {
    type: ACTION_TYPES.ADD_NEXT_GEN_PILL,
    payload: {
      pillData: { foo: 1234 },
      position: 0
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 3);
  assert.equal(result.pillsData[0].foo, 1234);
});

test('ACTION_TYPES.ADD_NEXT_GEN_PILL adds pill to the middle of a list', function(assert) {
  const action = {
    type: ACTION_TYPES.ADD_NEXT_GEN_PILL,
    payload: {
      pillData: { foo: 1234 },
      position: 1
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 3);
  assert.equal(result.pillsData[1].foo, 1234);
});

test('ACTION_TYPES.ADD_NEXT_GEN_PILL adds pill to end of list', function(assert) {
  const action = {
    type: ACTION_TYPES.ADD_NEXT_GEN_PILL,
    payload: {
      pillData: { foo: 1234 },
      position: 2
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 3);
  assert.equal(result.pillsData[2].foo, 1234);
});
