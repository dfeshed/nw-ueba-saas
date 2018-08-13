import { test, module } from 'qunit';
import Immutable from 'seamless-immutable';

import * as ACTION_TYPES from 'investigate-events/actions/types';
import reducer from 'investigate-events/reducers/investigate/event-results/reducer';

module('Unit | Reducers | event-results | Investigate');

const stateWithoutSelections = Immutable.from({
  allEventsSelected: false,
  selectedEventIds: []
});

const stateWithSelections = Immutable.from({
  allEventsSelected: false,
  selectedEventIds: ['foo']
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
