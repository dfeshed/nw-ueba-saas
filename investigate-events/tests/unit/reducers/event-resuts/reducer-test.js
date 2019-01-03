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

test('ACTION_TYPES.SET_EVENTS_PAGE reducer will concatenate and sort events', function(assert) {
  const initialState = Immutable.from({
    data: []
  });

  let action = {
    type: ACTION_TYPES.SET_EVENTS_PAGE,
    payload: [{ timeAsNumber: 5555, sessionId: 5 }]
  };
  let result = reducer(initialState, action);
  assert.equal(result.data.length, 1, 'One event was absorbed');

  action = {
    type: ACTION_TYPES.SET_EVENTS_PAGE,
    payload: [{ timeAsNumber: 9999, sessionId: 6 }]
  };
  result = reducer(result, action);
  assert.equal(result.data.length, 2, 'Two events now');
  assert.equal(result.data[0].timeAsNumber, 9999, 'sorted descending');

  action = {
    type: ACTION_TYPES.SET_EVENTS_PAGE,
    payload: [{ timeAsNumber: 7777, sessionId: 7 }]
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
    streamLimit: 2
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

