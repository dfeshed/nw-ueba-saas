import Ember from 'ember';
import { module, test } from 'qunit';
import { LIFECYCLE, KEY } from 'redux-pack';
import incidentReducer from 'respond/reducers/respond/incident';
import ACTION_TYPES from 'respond/actions/types';

const { copy } = Ember;

const initialState = {
  storyline: null,
  storylineStatus: false
};

module('Unit | Utility | Incident Storyline Actions - Reducers');

// this utility method will make an action that redux pack understands
function makePackAction(lifecycle, { type, payload, meta = {} }) {
  return {
    type,
    payload,
    meta: {
      ...meta,
      [KEY.LIFECYCLE]: lifecycle
    }
  };
}

test('When FETCH_INCIDENT_STORYLINE starts, the storylineStatus changes to wait', function(assert) {
  const initState = copy(initialState);
  const storylineStatus = 'wait';
  const expectedEndState = {
    ...initState,
    storylineStatus
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE });
  const endState = incidentReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_INCIDENT_STORYLINE fails, the storylineStatus changes to error', function(assert) {
  const initState = copy(initialState);
  const storylineStatus = 'error';
  const expectedEndState = {
    ...initState,
    storylineStatus
  };
  const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE });
  const endState = incidentReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_INCIDENT_STORYLINE succeeds, the info obj and storylineStatus update appropriately', function(assert) {
  const initState = copy(initialState);
  const storyline = { testing: 123 };
  const storylineStatus = 'completed';
  const expectedEndState = {
    ...initState,
    storylineStatus,
    storyline
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE, payload: { data: storyline } });
  const endState = incidentReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

