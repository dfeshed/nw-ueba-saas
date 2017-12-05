import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import incidentReducer from 'respond/reducers/respond/incident';
import ACTION_TYPES from 'respond/actions/types';
import makePackAction from '../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';

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

test('When SEARCH_RELATED_INDICATORS_STARTED, the incident search state is reset appropriately', function(assert) {
  const entityId = '10.20.30.40';
  const entityType = 'IP';
  const timeFrameName = 'LAST_24_HOURS';
  const searchResults = [];
  const searchStatus = 'streaming';
  const expectedEndState = {
    ...initialState,
    searchEntity: { type: entityType, id: entityId },
    searchTimeFrameName: timeFrameName,
    searchResults,
    searchStatus
  };
  const action = {
    type: ACTION_TYPES.SEARCH_RELATED_INDICATORS_STARTED,
    payload: { entityId, entityType, timeFrameName }
  };
  const endState = incidentReducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When SEARCH_RELATED_INDICATORS_STREAM_INITIALIZED, the given stop function is stored in state', function(assert) {
  const stopSearchStream = function() {};
  const expectedEndState = {
    ...initialState,
    stopSearchStream
  };
  const action = { type: ACTION_TYPES.SEARCH_RELATED_INDICATORS_STREAM_INITIALIZED, payload: stopSearchStream };
  const endState = incidentReducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When SEARCH_RELATED_INDICATORS_RETRIEVE_BATCH, the searchResults & searchStatus state are updated correctly', function(assert) {
  const data = [ { id: 1, alert: { name: 'Alert 1' } } ];
  const expectedEndState = {
    ...initialState,
    searchResults: data,
    searchStatus: 'complete'
  };
  const action = {
    type: ACTION_TYPES.SEARCH_RELATED_INDICATORS_RETRIEVE_BATCH,
    payload: {
      code: 0,
      data,
      meta: {
        complete: true
      }
    }
  };
  const endState = incidentReducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When SEARCH_RELATED_INDICATORS_COMPLETED, the search stop function is cleared from state', function(assert) {
  const stopSearchStream = null;
  const expectedEndState = {
    ...initialState,
    stopSearchStream
  };
  const action = { type: ACTION_TYPES.SEARCH_RELATED_INDICATORS_COMPLETED };
  const endState = incidentReducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When SEARCH_RELATED_INDICATORS_STOPPED, the search status & stop function state are updated correctly', function(assert) {
  const stopSearchStream = null;
  const searchStatus = 'stopped';
  const expectedEndState = {
    ...initialState,
    stopSearchStream,
    searchStatus
  };
  const action = { type: ACTION_TYPES.SEARCH_RELATED_INDICATORS_STOPPED };
  const endState = incidentReducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When SEARCH_RELATED_INDICATORS_ERROR, the search status & stop function state are updated correctly', function(assert) {
  const stopSearchStream = null;
  const searchStatus = 'error';
  const expectedEndState = {
    ...initialState,
    stopSearchStream,
    searchStatus
  };
  const action = { type: ACTION_TYPES.SEARCH_RELATED_INDICATORS_ERROR };
  const endState = incidentReducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('The ADD_RELATED_INDICATORS action properly modifies the app state', function(assert) {
  const searchResult = Immutable.from({ id: '586ecf95ecd25950034e1314', receivedTime: 1483657109645 });
  const endStateIndicatorToAdd = searchResult.asMutable();
  endStateIndicatorToAdd.partOfIncident = true;
  endStateIndicatorToAdd.incidentId = 'INC-123';

  const initState = initialState.asMutable();
  initState.storyline = [
    { id: '586ecf95ecd25950034e1312', receivedTime: 1483657109643 },
    { id: '586ecf95ecd25950034e1313', receivedTime: 1483657109644 }
  ];
  initState.searchResults = [searchResult];

  const expectedEndState = {
    ...initialState,
    storyline: [...initState.storyline, endStateIndicatorToAdd],
    searchResults: [endStateIndicatorToAdd],
    addRelatedIndicatorsStatus: 'success'
  };

  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.ADD_RELATED_INDICATORS,
    payload: {
      data: ['586ecf95ecd25950034e1314'],
      request: { data: { entity: { id: 'INC-123' } } }
    }
  });

  const endState = incidentReducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('When CLEAR_ADD_RELATED_INDICATORS_STATUS, the add status is reset to null', function(assert) {
  const initState = {
    ...initialState,
    addRelatedIndicatorsStatus: 'wait'
  };

  const expectedEndState = {
    ...initialState,
    addRelatedIndicatorsStatus: null
  };
  const action = { type: ACTION_TYPES.CLEAR_ADD_RELATED_INDICATORS_STATUS };
  const endState = incidentReducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});
