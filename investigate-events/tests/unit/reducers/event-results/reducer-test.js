import { test, module } from 'qunit';
import Immutable from 'seamless-immutable';

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
    data: []
  });
  const sortOrderPreference = 'Ascending';

  let action = {
    type: ACTION_TYPES.SET_EVENTS_PAGE,
    payload: { eventsBatch: [{ timeAsNumber: 7777, sessionId: 5 }], sortOrderPreference }
  };
  let result = reducer(initialState, action);
  assert.equal(result.data.length, 1, 'One event was absorbed');

  action = {
    type: ACTION_TYPES.SET_EVENTS_PAGE,
    payload: { eventsBatch: [{ timeAsNumber: 9999, sessionId: 6 }], sortOrderPreference }
  };
  result = reducer(result, action);
  assert.equal(result.data.length, 2, 'Two events now');
  assert.equal(result.data[0].timeAsNumber, 7777, 'sorted ascending');

  action = {
    type: ACTION_TYPES.SET_EVENTS_PAGE,
    payload: { eventsBatch: [{ timeAsNumber: 5555, sessionId: 7 }], sortOrderPreference }
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
    eventTimeSortOrder: 'Descending'
  });

  const sortOrderPreference = 'Descending';

  let action = {
    type: ACTION_TYPES.SET_EVENTS_PAGE,
    payload: { eventsBatch: [{ timeAsNumber: 7777, sessionId: 5 }], sortOrderPreference }
  };
  let result = reducer(initialState, action);
  assert.equal(result.data.length, 1, 'One event was absorbed');

  action = {
    type: ACTION_TYPES.SET_EVENTS_PAGE,
    payload: { eventsBatch: [{ timeAsNumber: 5555, sessionId: 6 }], sortOrderPreference }
  };
  result = reducer(result, action);
  assert.equal(result.data.length, 2, 'Two events now');
  assert.equal(result.data[0].timeAsNumber, 7777, 'sorted descending');

  action = {
    type: ACTION_TYPES.SET_EVENTS_PAGE,
    payload: { eventsBatch: [{ timeAsNumber: 9999, sessionId: 7 }], sortOrderPreference }
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
    eventTimeSortOrder: 'Descending'
  });
  const sortOrderPreference = 'Descending';

  const action = {
    type: ACTION_TYPES.SET_EVENTS_PAGE,
    payload: { eventsBatch: [{ timeAsNumber: 5555, sessionId: 5 }, { timeAsNumber: 9999, sessionId: 6 }, { timeAsNumber: 7777, sessionId: 7 }], sortOrderPreference }
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
    eventTimeSortOrder: 'Ascending'
  });

  const sortOrderPreference = 'Ascending';

  const action = {
    type: ACTION_TYPES.SET_EVENTS_PAGE,
    payload: { eventsBatch: [{ timeAsNumber: 5555, sessionId: 5 }, { timeAsNumber: 9999, sessionId: 6 }, { timeAsNumber: 7777, sessionId: 7 }], sortOrderPreference }
  };
  const result = reducer(initialState, action);
  assert.equal(result.data.length, 2, 'Two events left after truncation');

  // the oldest data was the truncated data
  assert.equal(result.data[0].timeAsNumber, 7777, 'sorted ascending');
  assert.equal(result.data[1].timeAsNumber, 9999, 'sorted ascending');
});

test('ACTION_TYPES.SET_PREFERENCES will set correct preferences', function(assert) {
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
