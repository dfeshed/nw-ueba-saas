import Ember from 'ember';
import { module, test } from 'qunit';
import incidentReducer from 'respond/reducers/respond/incident';
import ACTION_TYPES from 'respond/actions/types';

const { copy } = Ember;

const initialState = {
  storyline: null,
  storylineStatus: false
};

module('Unit | Utility | Incident Storyline Actions - Reducers');

test('When FETCH_INCIDENT_STORYLINE starts, the storylineStatus changes to streaming', function(assert) {
  const initState = copy(initialState);
  const storylineStatus = 'streaming';
  const storyline = [];
  const expectedEndState = {
    ...initState,
    storyline,
    storylineStatus
  };
  const action = { type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_STARTED };
  const endState = incidentReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_INCIDENT_STORYLINE fails, the storylineStatus changes to error', function(assert) {
  const initState = copy(initialState);
  const storylineStatus = 'error';
  const stopStorylineStream = null;
  const expectedEndState = {
    ...initState,
    storylineStatus,
    stopStorylineStream
  };
  const action = { type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_ERROR };
  const endState = incidentReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_INCIDENT_STORYLINE retrieves a batch, the storyline and storylineStatus update appropriately', function(assert) {
  const initState = copy(initialState);
  const storyline = [ { testing: 123 } ];
  const storylineStatus = 'completed';
  const expectedEndState = {
    ...initState,
    storylineStatus,
    storyline
  };
  const action = { type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_RETRIEVE_BATCH, payload: { data: storyline, meta: { complete: true } } };
  const endState = incidentReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

