import { test, module, skip } from 'qunit';
import Immutable from 'seamless-immutable';
import makePackAction from '../../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-events/actions/types';
import reducer from 'investigate-events/reducers/investigate/event-results/reducer';

module('Unit | Reducers | event-results');

const stateWithoutSelections = Immutable.from({
  selectedEventIds: {}
});

const stateWithSelections = Immutable.from({
  selectedEventIds: { 7: 442, 10: 165 }
});

test('Should update seach term', function(assert) {
  const previous = Immutable.from({
    searchTerm: null
  });

  const action = {
    type: ACTION_TYPES.SET_SEARCH_TERM,
    searchTerm: 'foo'
  };
  const newEndState = reducer(previous, action);
  assert.deepEqual(newEndState.searchTerm, 'foo');
});

test('Should handle SORT_IN_CLIENT_COMPLETE', function(assert) {
  const previous = Immutable.from({
    status: 'sorting',
    data: [],
    cachedData: [{}]
  });

  const action = {
    type: ACTION_TYPES.SORT_IN_CLIENT_COMPLETE
  };
  const newEndState = reducer(previous, action);
  assert.equal(newEndState.status, 'complete');
  assert.equal(newEndState.data.length, 1);
  assert.equal(newEndState.cachedData, null);
});

test('Should handle SORT_IN_CLIENT_BEGIN', function(assert) {
  const previous = Immutable.from({
    status: null,
    data: [{}],
    cachedData: null
  });

  const action = {
    type: ACTION_TYPES.SORT_IN_CLIENT_BEGIN
  };
  const newEndState = reducer(previous, action);
  assert.equal(newEndState.status, 'sorting');
  assert.equal(newEndState.data.length, 0);
  assert.equal(newEndState.cachedData.length, 1);
});

test('Should update searchScrollIndex', function(assert) {
  const previous = Immutable.from({
    searchScrollIndex: null
  });

  const action = {
    type: ACTION_TYPES.SET_SEARCH_SCROLL,
    searchScrollIndex: 0
  };
  const newEndState = reducer(previous, action);
  assert.deepEqual(newEndState.searchScrollIndex, 0);
});

test('Should update visible columns', function(assert) {
  const previous = Immutable.from({
    visibleColumns: null
  });

  const action = {
    type: ACTION_TYPES.SET_VISIBLE_COLUMNS,
    payload: 'foo'
  };
  const newEndState = reducer(previous, action);
  assert.deepEqual(newEndState.visibleColumns, 'foo');
});

test('ACTION_TYPES.SELECT_EVENTS reducer', function(assert) {
  const action = {
    type: ACTION_TYPES.SELECT_EVENTS,
    payload: { 5: 300 }
  };

  const result = reducer(stateWithoutSelections, action);

  assert.equal(Object.keys(result.selectedEventIds).length, 1);
  assert.deepEqual(result.selectedEventIds, { 5: 300 });
});

test('ACTION_TYPES.DESELECT_EVENT reducer', function(assert) {
  const action = {
    type: ACTION_TYPES.DESELECT_EVENT,
    payload: 10
  };

  const result = reducer(stateWithSelections, action);

  assert.deepEqual(result.selectedEventIds, { 7: 442 });
});

test('ACTION_TYPES.SET_EVENTS_PAGE reducer will concatenate', function(assert) {
  const initialState = Immutable.from({
    data: []
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
  assert.equal(result.data[0].timeAsNumber, 7777, 'Two events was absorbed');
  assert.equal(result.data[1].timeAsNumber, 5555, 'Two events was absorbed');

  action = {
    type: ACTION_TYPES.SET_EVENTS_PAGE,
    payload: [{ timeAsNumber: 9999, sessionId: 7 }]
  };
  result = reducer(result, action);
  assert.equal(result.data.length, 3, 'Three events now');
  assert.equal(result.data[0].timeAsNumber, 7777, 'Three events was absorbed');
  assert.equal(result.data[1].timeAsNumber, 5555, 'Three events was absorbed');
  assert.equal(result.data[2].timeAsNumber, 9999, 'Three events was absorbed');
});

test('ACTION_TYPES.SET_EVENTS_PAGE will truncate if going over the limit', function(assert) {
  const initialState = Immutable.from({
    data: [],
    streamLimit: 2,
    eventTimeSortOrderPreferenceWhenQueried: 'Descending'
  });

  const action = {
    type: ACTION_TYPES.SET_EVENTS_PAGE,
    payload: [{ timeAsNumber: 5555, sessionId: 5 }, { timeAsNumber: 7777, sessionId: 7 }, { timeAsNumber: 9999, sessionId: 6 }]
  };
  const result = reducer(initialState, action);
  assert.equal(result.data.length, 2, 'Two events left after truncation');

  // the oldest data was the truncated data
  assert.equal(result.data[0].timeAsNumber, 5555);
  assert.equal(result.data[1].timeAsNumber, 7777);
});

test('ACTION_TYPES.SET_EVENTS_PAGE will truncate the right side of the results', function(assert) {
  const initialState = Immutable.from({
    data: [],
    streamLimit: 2,
    eventTimeSortOrderPreferenceWhenQueried: 'Ascending'
  });

  const action = {
    type: ACTION_TYPES.SET_EVENTS_PAGE,
    payload: [{ timeAsNumber: 5555, sessionId: 5 }, { timeAsNumber: 7777, sessionId: 7 }, { timeAsNumber: 9999, sessionId: 6 }]
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

test('ACTION_TYPES.INIT_EVENTS_STREAMING will set eventTimeSortOrderPreferenceWhenQueried properly and reset all event selections', function(assert) {
  let initialState = Immutable.from({
    eventTimeSortOrderPreferenceWhenQueried: undefined,
    selectedEventIds: { foo: 'foo' }
  });

  let action = {
    type: ACTION_TYPES.INIT_EVENTS_STREAMING,
    payload: { eventTimeSortOrderPreferenceWhenQueried: 'Descending' }
  };

  let result = reducer(initialState, action);
  assert.equal(result.eventTimeSortOrderPreferenceWhenQueried, 'Descending', 'State set properly with the correct value');
  assert.equal(Object.keys(result.selectedEventIds).length, 0);

  initialState = Immutable.from({
    eventTimeSortOrderPreferenceWhenQueried: undefined,
    selectedEventIds: {}
  });

  action = {
    type: ACTION_TYPES.INIT_EVENTS_STREAMING,
    payload: { eventTimeSortOrderPreferenceWhenQueried: 'Ascending' }
  };

  result = reducer(initialState, action);
  assert.equal(result.eventTimeSortOrderPreferenceWhenQueried, 'Ascending', 'State set properly with the correct value');
  assert.equal(Object.keys(result.selectedEventIds).length, 0);
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
