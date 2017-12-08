import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import incidentReducer from 'respond/reducers/respond/storyline';
import ACTION_TYPES from 'respond/actions/types';

const initialState = Immutable.from({
  storyline: null,
  storylineStatus: false,
  searchResults: null
});

module('Unit | Utility | Incident Storyline Actions - Reducers');

test('When FETCH_INCIDENT_STORYLINE starts, the storylineStatus changes to streaming', function(assert) {
  const storylineStatus = 'streaming';
  const storyline = [];
  const expectedEndState = {
    ...initialState,
    storyline,
    storylineStatus
  };
  const action = { type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_STARTED };
  const endState = incidentReducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_INCIDENT_STORYLINE fails, the storylineStatus changes to error', function(assert) {
  const storylineStatus = 'error';
  const stopStorylineStream = null;
  const expectedEndState = {
    ...initialState,
    storylineStatus,
    stopStorylineStream
  };
  const action = { type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_ERROR };
  const endState = incidentReducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_INCIDENT_STORYLINE retrieves a batch, the storyline and storylineStatus update appropriately', function(assert) {
  const storyline = [ { testing: 123 } ];
  const storylineStatus = 'completed';
  const expectedEndState = {
    ...initialState,
    storylineStatus,
    storyline
  };
  const action = { type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_RETRIEVE_BATCH, payload: { data: storyline, meta: { complete: true } } };
  const endState = incidentReducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_INCIDENT_STORYLINE_EVENTS_RETRIEVE_BATCH, the storyline events are updated', function(assert) {
  const indicatorId = 'ABC123DEFGHI';
  const events = [{}, {}];
  const initialState = {
    storylineEvents: [],
    storylineEventsBuffer: [],
    storylineEventsStatus: 'streaming',
    storylineEventsBufferMax: 2
  };
  const expectedEndState = {
    ...initialState,
    storylineEvents: [{ indicatorId, events: [{ id: `${indicatorId}:0`, indicatorId }, { id: `${indicatorId}:1`, indicatorId }] }],
    storylineEventsBuffer: [],
    storylineEventsStatus: 'paused'
  };
  const action = {
    type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_EVENTS_RETRIEVE_BATCH,
    payload: { indicatorId, events }
  };
  const endState = incidentReducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_INCIDENT_STORYLINE_EVENTS_RETRIEVE_BATCH, events are added to the storylineEventsBuffer when ' +
  'storylineEvents is not empty and the number of events in the buffer is less than the max', function(assert) {
  const indicatorId = 'ABC123DEFGHI';
  const events = [{}, {}];
  const initialState = {
    storylineEvents: [{}], // storylineEvents is not empty (otherwise the buffer would flush immediately)
    storylineEventsBuffer: [],
    storylineEventsStatus: 'streaming',
    storylineEventsBufferMax: 2
  };
  const expectedEndState = {
    ...initialState,
    storylineEvents: [{}],
    storylineEventsBuffer: [{ indicatorId, events: [{ id: `${indicatorId}:0`, indicatorId }, { id: `${indicatorId}:1`, indicatorId }] }],
    storylineEventsStatus: 'paused'
  };
  const action = {
    type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_EVENTS_RETRIEVE_BATCH,
    payload: { indicatorId, events }
  };
  const endState = incidentReducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_INCIDENT_STORYLINE_EVENTS_RETRIEVE_BATCH, and the buffer exceeds its max size, the buffered events are flushed' +
  'to the storylineEvents array', function(assert) {
  const indicatorId = 'ABC123DEFGHI';
  const events = [{}, {}];
  const initialState = {
    storylineEvents: [{}], // at least one event in the storylineEvents array ensures the buffer won't be immediately flushed
    storylineEventsBuffer: [{}, {}], // some objects in the buffer ensures the next event set will flush the buffer because it exceeds the max
    storylineEventsStatus: 'streaming',
    storylineEventsBufferMax: 2 // max of 2 ensures that the buffer is flushed once one more event set is added
  };
  const expectedEndState = {
    ...initialState,
    // items from the buffer along with the new event set from the action end up in the storylineEvents array
    storylineEvents: [{}, {}, {}, { indicatorId, events: [{ id: `${indicatorId}:0`, indicatorId }, { id: `${indicatorId}:1`, indicatorId }] }],
    storylineEventsBuffer: [],
    storylineEventsStatus: 'paused'
  };
  const action = {
    type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_EVENTS_RETRIEVE_BATCH,
    payload: { indicatorId, events }
  };
  const endState = incidentReducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_INCIDENT_STORYLINE_EVENTS_COMPLETED, the buffer is flushed of items and the state is updated as expected', function(assert) {
  const initialState = {
    storylineEvents: [{}], // at least one event in the storylineEvents array ensures the buffer won't be immediately flushed
    storylineEventsBuffer: [{}, {}], // some objects in the buffer ensures the next event set will flush the buffer because it exceeds the max
    storylineEventsStatus: 'streaming',
    storylineEventsBufferMax: 2 // max of 2 ensures that the buffer is flushed once one more event set is added
  };
  const expectedEndState = {
    ...initialState,
    // items from the buffer along with the new event set from the action end up in the storylineEvents array
    storylineEvents: [{}, {}, {}],
    storylineEventsBuffer: [],
    storylineEventsStatus: 'completed'
  };
  const action = {
    type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_EVENTS_COMPLETED
  };
  const endState = incidentReducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});
