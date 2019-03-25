import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../helpers/redux-data-helper';

import interactionCreators from 'investigate-events/actions/interaction-creators';
import ACTION_TYPES from 'investigate-events/actions/types';

let queryIsRunning = true;
let noMoreEventsAllowed = false;
let status = 'foo';
let actionsByType = {};
let queryResults = [];
const streamBatch = 250;
const streamLimit = 1000; // limit of 1000, 500 get returned so stream ends naturally
let allActionsDispatched = [];

const downstreamOldestDispatchCreator = (assert, asserts, getState) => {

  const downstreamDispatch = (actionOrThunk) => {
    if (noMoreEventsAllowed) {
      assert.ok(false, 'should not have taken in more events');
      return;
    }

    if (typeof actionOrThunk === 'function') {
      // is another thunk, recurse
      actionOrThunk(downstreamDispatch, getState);
    } else {

      allActionsDispatched.push(actionOrThunk);

      if (actionOrThunk.type === ACTION_TYPES.QUERY_IS_RUNNING) {
        queryIsRunning = actionOrThunk.payload;
      }
      if (actionOrThunk.type === ACTION_TYPES.INIT_EVENTS_STREAMING) {
        status = 'streaming';
      }
      if (actionOrThunk.type === ACTION_TYPES.SET_EVENTS_PAGE_STATUS) {
        status = actionOrThunk.payload;
        if (status === 'complete') {
          noMoreEventsAllowed = true;
          actionsByType = {};
          allActionsDispatched.forEach((action) => {
            if (actionsByType[action.type]) {
              actionsByType[action.type].push(action);
            } else {
              actionsByType[action.type] = [action];
            }
          });

          assert.equal(queryIsRunning === false, true, 'query is running flag should be false');
          assert.equal(status, 'complete', 'query is complete');
          asserts();
        }
      }
      if (actionOrThunk.type === ACTION_TYPES.SET_EVENTS_PAGE) {
        queryResults = queryResults.concat(actionOrThunk.payload);
      }
    }
  };

  return downstreamDispatch;
};

module('Unit | Actions | interaction creators', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
    allActionsDispatched = [];
    queryIsRunning = true;
    queryResults = [];
    actionsByType = {};
  });
  test('setQueryView action creator returns proper type and payload', function(assert) {
    const action = interactionCreators.setQueryView('foo');
    assert.equal(action.type, ACTION_TYPES.SET_QUERY_VIEW, 'action has the correct type');
    assert.deepEqual(action.payload, { queryView: 'foo' }, 'payload has correct data');
  });

  test('toggleQueryConsole fires when not disabled', function(assert) {
    assert.expect(1);
    const getState = () => {
      return new ReduxDataHelper().queryStats().build();
    };
    const myDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.TOGGLE_QUERY_CONSOLE, 'action has the correct type');
    };
    const thunk = interactionCreators.toggleQueryConsole();
    thunk(myDispatch, getState);
  });

  test('toggleQueryConsole does not fire when disabled', function(assert) {
    const getState = () => {
      return new ReduxDataHelper().queryStats().queryStatsIsEmpty().build();
    };
    const thunk = interactionCreators.toggleQueryConsole();
    assert.equal(thunk(() => {}, getState), undefined);
  });


  test('toggleSelectAllEvents action creator returns proper type', function(assert) {
    const { type } = interactionCreators.toggleSelectAllEvents();
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

    const thunk = interactionCreators.toggleEventSelection({ sessionId: 'foo' });

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

    const thunk = interactionCreators.toggleEventSelection({ sessionId: 'bar' });

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

    const thunk = interactionCreators.toggleEventSelection({ sessionId: 'foo' });

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

    const thunk = interactionCreators.toggleEventSelection({ sessionId: 'baz' });

    thunk(dispatch, getState);
  });

  test('setColumnGroup - changing columms triggers off fetchInvestigateData which loads events according to the requested columns - OldestEvents', async function(assert) {
    const done = assert.async();
    let fetchInvestigateDispatchCount = 0;
    let columngrpDispatchCount = 0;
    const getState = () => {
      return new ReduxDataHelper()
        .isQueryRunning(queryIsRunning)
        .columnGroup('SUMMARY')
        .eventsPreferencesConfig()
        .streamLimit(streamLimit)
        .streamBatch(streamBatch)
        .pillsDataPopulated()
        .metaFilter()
        .eventResultsStatus(status)
        .eventResults(queryResults)
        .eventCount(500)
        .language()
        .serviceId()
        .startTime()
        .endTime()
        .columnGroups()
        .metaPanel({ init: false })
        .eventTimeSortOrder()
        .build();
    };

    const setColumnDispatch = (action) => {
      if (typeof action === 'function') {
        action(fetchDispatch, getState);
      } else if (columngrpDispatchCount === 0) {
        assert.equal(action.type, ACTION_TYPES.SET_SELECTED_COLUMN_GROUP, 'sent out action to change column groups');
        columngrpDispatchCount++;
      } else {
        assert.equal(action.type, ACTION_TYPES.SET_QUERY_EXECUTED_BY_COLUMN_GROUP_FLAG, 'change flag to represent query executed by column groups');
      }
    };

    const asserts = () => {
      assert.equal(allActionsDispatched.length, 6, 'total actions dispatched');
      assert.equal(queryResults.length, 500, 'total results accumulated');
      assert.equal(actionsByType[ACTION_TYPES.SET_EVENTS_PAGE].length, 2, '2 pages of data dispatched');
      assert.equal(actionsByType[ACTION_TYPES.QUERY_IS_RUNNING].length, 1, 'query not running just one time');
      assert.equal(actionsByType[ACTION_TYPES.INIT_EVENTS_STREAMING].length, 1, 'initialize streaming just one time');
      assert.equal(actionsByType[ACTION_TYPES.SET_EVENTS_PAGE_STATUS].length, 1, 'set status once');
      assert.equal(actionsByType[ACTION_TYPES.SET_EVENTS_PAGE_STATUS][0].payload, 'complete', 'first status call is to indicate between streams');
      done();
    };

    const fetchDispatch = (action) => {
      if ((fetchInvestigateDispatchCount === 3) && typeof action === 'function') {
        action(downstreamOldestDispatchCreator(assert, asserts, getState), getState);
      } else {
        fetchInvestigateDispatchCount++;
      }
    };

    const thunk = interactionCreators.setColumnGroup({ id: 2 });

    thunk(setColumnDispatch, getState);

  });


});
