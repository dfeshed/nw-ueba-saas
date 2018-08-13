import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../helpers/redux-data-helper';

import eventsCreators from 'investigate-events/actions/events-creators';
import ACTION_TYPES from 'investigate-events/actions/types';

module('Unit | Actions | events creators', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('toggleSelectAllEvents action creator returns proper type', function(assert) {
    const { type } = eventsCreators.toggleSelectAllEvents();
    assert.equal(type, ACTION_TYPES.TOGGLE_SELECT_ALL_EVENTS, 'action has the correct type');
  });

  test('toggleEventSelection action creator returns proper type and payload when allEventsSelected is true', function(assert) {
    const getState = () => {
      return new ReduxDataHelper().allEventsSelected(true).eventResults([
        { sessionId: 'foo' },
        { sessionId: 'bar' }
      ]).build();
    };

    const dispatch = (action) => {
      switch (action.type) {
        case ACTION_TYPES.TOGGLE_SELECT_ALL_EVENTS:
          break;
        case ACTION_TYPES.SELECT_EVENTS:
          assert.equal(action.payload.length, 1, 'action has the correct payload length');
          assert.equal(action.payload[0], 'bar', 'action has the correct payload');
          break;
        default:
          assert.equal(true, false, 'action has the correct type');
      }
    };

    const thunk = eventsCreators.toggleEventSelection({ sessionId: 'foo' });

    thunk(dispatch, getState);
  });

  test('toggleEventSelection action creator returns proper type and payload when allEventsSelected is false and selectedEventIds includes payload', function(assert) {
    const getState = () => {
      return new ReduxDataHelper().allEventsSelected(false).eventResults([
        { sessionId: 'foo' },
        { sessionId: 'bar' }
      ]).withSelectedEventIds().build();
    };

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DESELECT_EVENT, 'action has the correct type');
      assert.equal(action.payload, 'bar', 'action has the correct payload');
    };

    const thunk = eventsCreators.toggleEventSelection({ sessionId: 'bar' });

    thunk(dispatch, getState);
  });

  test('toggleEventSelection action creator returns proper type and payload when allEventsSelected is false, and last unselected event is selected', function(assert) {
    const getState = () => {
      return new ReduxDataHelper().allEventsSelected(false).eventResults([
        { sessionId: 'foo' },
        { sessionId: 'bar' }
      ]).eventCount(2).withSelectedEventIds().build();
    };

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.TOGGLE_SELECT_ALL_EVENTS, 'action has the correct type');
    };

    const thunk = eventsCreators.toggleEventSelection({ sessionId: 'foo' });

    thunk(dispatch, getState);
  });

  test('toggleEventSelection action creator returns proper type and payload when allEventsSelected is false, selectedEventIds does not include payload, and all event ids are not selected', function(assert) {
    const getState = () => {
      return new ReduxDataHelper().allEventsSelected(false).eventResults([
        { sessionId: 'foo' },
        { sessionId: 'bar' },
        { sessionId: 'baz' }
      ]).eventCount(3).withSelectedEventIds().build();
    };

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.SELECT_EVENTS, 'action has the correct type');
      assert.equal(action.payload.length, 1, 'action has the correct payload length');
      assert.equal(action.payload[0], 'baz', 'action has the correct payload');
    };

    const thunk = eventsCreators.toggleEventSelection({ sessionId: 'baz' });

    thunk(dispatch, getState);
  });
});
