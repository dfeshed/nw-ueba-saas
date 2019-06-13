import { module, test, skip } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../helpers/redux-data-helper';

import interactionCreators, { updateUrl } from 'investigate-events/actions/interaction-creators';
import ACTION_TYPES from 'investigate-events/actions/types';

let queryIsRunning = true;
let noMoreEventsAllowed = false;
let status = 'foo';
let actionsByType = {};
let queryResults = [];
const streamBatch = 250;
const streamLimit = 1000; // limit of 1000, 500 get returned so stream ends naturally
let allActionsDispatched = [];

const eventResultsData = [
  { sessionId: 1, medium: 1 },
  { sessionId: 2, medium: 1 },
  { sessionId: 3, medium: 32 }
];

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

  test('searchForTerm action creator returns proper type', function(assert) {
    const { type } = interactionCreators.searchForTerm();
    assert.equal(type, ACTION_TYPES.SET_SEARCH_TERM, 'action has the correct type');
  });

  test('toggleSelectAllEvents has the correct payload when all events were not selected before toggling', function(assert) {
    const getState = () => {
      return new ReduxDataHelper()
        .eventResults(eventResultsData)
        .selectedEventIds({
          1: 1,
          3: 3
        })
        .build();
    };

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.SELECT_EVENTS, 'action has correct type');
      assert.deepEqual(action.payload, { 1: 1, 2: 2, 3: 3 }, 'action selects all events');
    };

    const thunk = interactionCreators.toggleSelectAllEvents();

    thunk(dispatch, getState);
  });

  test('toggleSelectAllEvents action creator returns proper type and payload when all events were selected before toggling', function(assert) {
    const getState = () => {
      return new ReduxDataHelper()
        .eventResults(eventResultsData)
        .selectedEventIds({
          1: 1,
          2: 2,
          3: 3
        })
        .build();
    };

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.SELECT_EVENTS, 'action has correct type');
      assert.deepEqual(action.payload, {}, 'action de-selects all events');
    };

    const thunk = interactionCreators.toggleSelectAllEvents();

    thunk(dispatch, getState);
  });

  test('toggleEventSelection action creator returns proper type and payload when selectedEventIds includes the sessionId being toggled', function(assert) {
    const getState = () => {
      return new ReduxDataHelper()
        .eventResults(eventResultsData)
        .selectedEventIds({
          1: 1,
          3: 3
        })
        .build();
    };

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DESELECT_EVENT, 'action has the correct type');
      assert.equal(action.payload, 1, 'action has the correct payload');
    };

    const thunk = interactionCreators.toggleEventSelection({ sessionId: 1 });

    thunk(dispatch, getState);
  });

  test('toggleEventSelection action creator returns proper type and payload when selectedEventIds does not include the sessionId being toggled', function(assert) {
    const getState = () => {
      return new ReduxDataHelper()
        .eventResults(eventResultsData)
        .selectedEventIds({
          3: 3
        })
        .build();
    };

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.SELECT_EVENTS, 'action has the correct type');
      assert.deepEqual(action.payload, { 2: 2, 3: 3 }, 'action has the correct payload');
    };

    const thunk = interactionCreators.toggleEventSelection({ sessionId: 2 });

    thunk(dispatch, getState);
  });

  // Skipping this post upgrade of node + sass as it is behaving differently
  // with every run and is breaking master. Will return to possibly refactor.
  skip('setColumnGroup - changing columms triggers off fetchInvestigateData which loads events according to the requested columns - OldestEvents', async function(assert) {
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

  test('updateUrl prepares url with new sort values', function(assert) {
    const initialUrl = '?sortField=time&sortDir=Ascending';
    const updateParams = {
      sortField: 'medium',
      sortDir: 'Descending'
    };
    assert.equal(updateUrl(initialUrl, updateParams), 'sortField=medium&sortDir=Descending');
  });

});
