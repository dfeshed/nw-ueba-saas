import { test, module, skip } from 'qunit';
import Immutable from 'seamless-immutable';
import makePackAction from '../../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-events/actions/types';
import reducer from 'investigate-events/reducers/investigate/event-results/reducer';

module('Unit | Reducers | event-results');

const stateWithoutSelections = Immutable.from({
  allEventsSelected: false,
  selectedEventIds: []
});

const stateWithSelections = Immutable.from({
  allEventsSelected: false,
  selectedEventIds: ['foo']
});

const stateWithAllSelected = Immutable.from({
  allEventsSelected: true,
  selectedEventIds: ['foo', 'bar']
});

test('Should update seach term', function(assert) {
  const previous = Immutable.from({
    searchTerm: null
  });

  const action = {
    type: ACTION_TYPES.SET_SEARCH_TERM,
    payload: 'foo'
  };
  const newEndState = reducer(previous, action);
  assert.deepEqual(newEndState.searchTerm, 'foo');
});

test('ACTION_TYPES.TOGGLE_SELECT_ALL_EVENTS reducer', function(assert) {
  const toggle = {
    type: ACTION_TYPES.TOGGLE_SELECT_ALL_EVENTS
  };

  const result = reducer(stateWithoutSelections, toggle);
  assert.equal(result.allEventsSelected, true);
});

test('ACTION_TYPES.SELECT_EVENTS reducer', function(assert) {
  const action = {
    type: ACTION_TYPES.SELECT_EVENTS,
    payload: ['foo']
  };

  const result = reducer(stateWithoutSelections, action);

  assert.equal(result.selectedEventIds.length, 1);
  assert.equal(result.selectedEventIds[0], 'foo');
});

test('ACTION_TYPES.DESELECT_EVENT reducer', function(assert) {
  const action = {
    type: ACTION_TYPES.DESELECT_EVENT,
    payload: 'foo'
  };

  const result = reducer(stateWithSelections, action);

  assert.equal(result.selectedEventIds.length, 0);
});

test('ACTION_TYPES.INITIALIZE_INVESTIGATE reducer', function(assert) {
  const action = {
    type: ACTION_TYPES.INITIALIZE_INVESTIGATE
  };

  const result = reducer(stateWithAllSelected, action);

  assert.equal(result.allEventsSelected, false);
  assert.equal(result.selectedEventIds.length, 0);
});

test('ACTION_TYPES.SET_EVENTS_PAGE reducer will concatenate and sort events in Ascending order of Time', function(assert) {
  const initialState = Immutable.from({
    data: [],
    eventTimeSortOrderPreferenceWhenQueried: 'Ascending'
  });

  let action = {
    type: ACTION_TYPES.SET_EVENTS_PAGE,
    payload: [{ timeAsNumber: 7777, sessionId: 5 }]
  };
  let result = reducer(initialState, action);
  assert.equal(result.data.length, 1, 'One event was absorbed');

  action = {
    type: ACTION_TYPES.SET_EVENTS_PAGE,
    payload: [{ timeAsNumber: 9999, sessionId: 6 }]
  };
  result = reducer(result, action);
  assert.equal(result.data.length, 2, 'Two events now');
  assert.equal(result.data[0].timeAsNumber, 7777, 'sorted ascending');

  action = {
    type: ACTION_TYPES.SET_EVENTS_PAGE,
    payload: [{ timeAsNumber: 5555, sessionId: 7 }]
  };
  result = reducer(result, action);
  assert.equal(result.data.length, 3, 'Three events now');
  assert.equal(result.data[0].timeAsNumber, 5555, 'sorted ascending');
  assert.equal(result.data[1].timeAsNumber, 7777, 'sorted ascending');
  assert.equal(result.data[2].timeAsNumber, 9999, 'sorted ascending');
});

test('ACTION_TYPES.SET_EVENTS_PAGE reducer will concatenate and sort events in Descending order of Time', function(assert) {
  const initialState = Immutable.from({
    data: [],
    eventTimeSortOrderPreferenceWhenQueried: 'Descending'
  });

  let action = {
    type: ACTION_TYPES.SET_EVENTS_PAGE,
    payload: [{ timeAsNumber: 7777, sessionId: 5 }]
  };
  let result = reducer(initialState, action);
  assert.equal(result.data.length, 1, 'One event was absorbed');

  action = {
    type: ACTION_TYPES.SET_EVENTS_PAGE,
    payload: [{ timeAsNumber: 5555, sessionId: 6 }]
  };
  result = reducer(result, action);
  assert.equal(result.data.length, 2, 'Two events now');
  assert.equal(result.data[0].timeAsNumber, 7777, 'sorted descending');

  action = {
    type: ACTION_TYPES.SET_EVENTS_PAGE,
    payload: [{ timeAsNumber: 9999, sessionId: 7 }]
  };
  result = reducer(result, action);
  assert.equal(result.data.length, 3, 'Three events now');
  assert.equal(result.data[0].timeAsNumber, 9999, 'sorted descending');
  assert.equal(result.data[1].timeAsNumber, 7777, 'sorted descending');
  assert.equal(result.data[2].timeAsNumber, 5555, 'sorted descending');
});

test('ACTION_TYPES.SET_EVENTS_PAGE will truncate if going over the limit', function(assert) {
  const initialState = Immutable.from({
    data: [],
    streamLimit: 2,
    eventTimeSortOrderPreferenceWhenQueried: 'Descending'
  });

  const action = {
    type: ACTION_TYPES.SET_EVENTS_PAGE,
    payload: [{ timeAsNumber: 5555, sessionId: 5 }, { timeAsNumber: 9999, sessionId: 6 }, { timeAsNumber: 7777, sessionId: 7 }]
  };
  const result = reducer(initialState, action);
  assert.equal(result.data.length, 2, 'Two events left after truncation');

  // the oldest data was the truncated data
  assert.equal(result.data[0].timeAsNumber, 9999, 'sorted descending');
  assert.equal(result.data[1].timeAsNumber, 7777, 'sorted descending');
});

test('ACTION_TYPES.SET_EVENTS_PAGE will truncate the right side of the results', function(assert) {
  const initialState = Immutable.from({
    data: [],
    streamLimit: 2,
    eventTimeSortOrderPreferenceWhenQueried: 'Ascending'
  });

  const action = {
    type: ACTION_TYPES.SET_EVENTS_PAGE,
    payload: [{ timeAsNumber: 5555, sessionId: 5 }, { timeAsNumber: 9999, sessionId: 6 }, { timeAsNumber: 7777, sessionId: 7 }]
  };
  const result = reducer(initialState, action);
  assert.equal(result.data.length, 2, 'Two events left after truncation');

  // the oldest data was the truncated data
  assert.equal(result.data[0].timeAsNumber, 7777, 'sorted ascending');
  assert.equal(result.data[1].timeAsNumber, 9999, 'sorted ascending');
});

// NewestFirst code commented out
skip('ACTION_TYPES.SET_PREFERENCES will set correct preferences', function(assert) {
  const initialState = Immutable.from({
    eventTimeSortOrder: 'Ascending'
  });

  const action = {
    type: ACTION_TYPES.SET_PREFERENCES,
    payload: {
      eventAnalysisPreferences: {
        eventTimeSortOrder: 'Descending'
      }
    }
  };
  const result = reducer(initialState, action);
  assert.equal(result.eventTimeSortOrder, 'Descending');
});

test('ACTION_TYPES.SET_LOG will merge log status into data', function(assert) {
  const initialState = Immutable.from({
    data: [
      { sessionId: 1 },
      { sessionId: 2 },
      { sessionId: 3 }
    ]
  });

  const action = {
    type: ACTION_TYPES.SET_LOG,
    payload: [{
      sessionId: 1, log: 'covfefe'
    }]
  };

  const result = reducer(initialState, action);
  assert.equal(result.data[0].log, 'covfefe', 'log data was set properly');
  assert.equal(result.data[0].logStatus, 'resolved', 'log status was set properly');
  assert.equal(result.data[1].log, undefined, 'log data was not changed');
  assert.equal(result.data[2].log, undefined, 'log data was not changed');

});

test('ACTION_TYPES.SET_LOG will do nothing if no logs match existing data', function(assert) {
  const initialState = Immutable.from({
    data: [
      { sessionId: 1 },
      { sessionId: 2 },
      { sessionId: 3 }
    ]
  });

  const action = {
    type: ACTION_TYPES.SET_LOG,
    payload: [{
      sessionId: 4, log: 'covfefe'
    }]
  };

  const result = reducer(initialState, action);
  assert.deepEqual(result, initialState, 'state unchanged');
});

test('ACTION_TYPES.SET_LOG will properly set error data', function(assert) {
  const initialState = Immutable.from({
    data: [
      { sessionId: 1 },
      { sessionId: 2 },
      { sessionId: 3 }
    ]
  });

  const action = {
    type: ACTION_TYPES.SET_LOG,
    payload: [{
      sessionId: 1, log: 'covfefe', errorCode: 154
    }]
  };

  const result = reducer(initialState, action);
  assert.equal(result.data[0].log, undefined, 'log data was not set');
  assert.equal(result.data[0].errorCode, 154, 'log data was not set');
  assert.equal(result.data[0].logStatus, 'rejected', 'log status was set properly');
});

test('ACTION_TYPES.INIT_EVENTS_STREAMING will set eventTimeSortOrderPreferenceWhenQueried properly', function(assert) {
  const initialState = Immutable.from({
    eventTimeSortOrderPreferenceWhenQueried: undefined
  });

  let action = {
    type: ACTION_TYPES.INIT_EVENTS_STREAMING,
    payload: { eventTimeSortOrderPreferenceWhenQueried: 'Descending' }
  };

  let result = reducer(initialState, action);
  assert.equal(result.eventTimeSortOrderPreferenceWhenQueried, 'Descending', 'State set properly with the correct value');

  action = {
    type: ACTION_TYPES.INIT_EVENTS_STREAMING,
    payload: { eventTimeSortOrderPreferenceWhenQueried: 'Ascending' }
  };

  result = reducer(initialState, action);
  assert.equal(result.eventTimeSortOrderPreferenceWhenQueried, 'Ascending', 'State set properly with the correct value');

});

test('ACTION_TYPES.SET_MAX_EVENT_LIMIT reducer updates the max event count when it succeeds', function(assert) {

  const initialState = Immutable.from({
    streamLimit: undefined
  });

  const data = {
    calculatedEventLimit: 200
  };
  const startAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.SET_MAX_EVENT_LIMIT,
    payload: { data }
  });
  const result = reducer(initialState, startAction);

  assert.equal(result.streamLimit, 200, 'Correct value in state based upon streamEvent limit');
});