import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper, { DEFAULT_PILLS_DATA, TEXT_PILL_DATA } from '../../helpers/redux-data-helper';
import interactionCreators, { updateUrl } from 'investigate-events/actions/interaction-creators';
import ACTION_TYPES from 'investigate-events/actions/types';
import Immutable from 'seamless-immutable';

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

const state0 = new ReduxDataHelper()
  .hasSummaryData(true)
  .isQueryRunning(queryIsRunning)
  .selectedColumnGroup('SUMMARY')
  .eventsPreferencesConfig()
  .streamLimit(streamLimit)
  .streamBatch(streamBatch)
  .pillsDataPopulated(DEFAULT_PILLS_DATA)
  .metaFilter()
  .eventResultsStatus(status)
  .eventResults(queryResults)
  .eventCount(500)
  .language()
  .serviceId('1')
  .startTime('1111')
  .endTime('9999')
  .columnGroups()
  .currentQueryHash('1', '1111', '9999', DEFAULT_PILLS_DATA)
  .metaPanel({ init: false })
  .eventTimeSortOrder()
  .build();

const _getState0 = (selectedColumnGroupId, pillsData) => {
  return {
    investigate: {
      data: {
        ...state0.investigate.data,
        selectedColumnGroup: selectedColumnGroupId
      },
      columnGroup: state0.investigate.columnGroup,
      queryNode: {
        ...state0.investigate.queryNode,
        pillsData
      },
      dictionaries: state0.investigate.dictionaries,
      eventCount: state0.investigate.eventCount,
      eventResults: state0.investigate.eventResults,
      meta: state0.investigate.meta,
      services: state0.investigate.services
    }
  };
};

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

  test('setSort dispatches three times without sort args', function(assert) {
    assert.expect(3);
    const getState = () => {
      return new ReduxDataHelper().hasSummaryData(true).eventCount(1).eventThreshold(1).build();
    };
    const myDispatch = () => assert.ok(true);
    const thunk = interactionCreators.setSort();
    thunk(myDispatch, getState);
  });

  test('setSort dispatches five times when hasMinimumCoreServicesVersionForColumnSorting and resultCountAtThreshold', function(assert) {
    assert.expect(4);
    const getState = () => {
      return new ReduxDataHelper().hasSummaryData(true).eventCount(1).eventThreshold(1).build();
    };
    const myDispatch = (action) => {

      if (action && action.type === 'INVESTIGATE_EVENTS::UPDATE_SORT' && action.sortField === 'time' && action.sortDirection === 'Ascending') {
        return assert.ok(true);
      } else if (action && action.type === 'INVESTIGATE_EVENTS::SELECT_EVENTS' && action.payload.length === 0) {
        return assert.ok(true);
      } else if (typeof action === 'function') {
        return assert.ok(true);
      } else {
        return assert.ok(false);
      }
    };
    const thunk = interactionCreators.setSort('time', 'Ascending');
    thunk(myDispatch, getState);
  });

  test('setSort dispatches six times when not hasMinimumCoreServicesVersionForColumnSorting', function(assert) {
    assert.expect(6);
    const getState = () => {
      return new ReduxDataHelper().hasSummaryData(true).withoutMinimumCoreServicesVersionForColumnSorting().eventCount(1).eventThreshold(1).build();
    };
    const myDispatch = (action) => {
      if (action && action.type === 'INVESTIGATE_EVENTS::UPDATE_SORT' && action.sortField === 'time' && action.sortDirection === 'Ascending') {
        return assert.ok(true);
      } else if (action && action.type === 'INVESTIGATE_EVENTS::SELECT_EVENTS' && action.payload.length === 0) {
        return assert.ok(true);
      } else if (action.type === 'INVESTIGATE_EVENTS::SORT_IN_CLIENT_BEGIN') {
        return assert.ok(true);
      } else if (action.type === 'INVESTIGATE_EVENTS::SORT_IN_CLIENT_COMPLETE') {
        return assert.ok(true);
      } else if (typeof action === 'function') {
        return assert.ok(true);
      } else {
        return assert.ok(false);
      }
    };
    const thunk = interactionCreators.setSort('time', 'Ascending');
    thunk(myDispatch, getState);
  });

  test('setSort dispatches six times when not resultCountAtThreshold', function(assert) {
    assert.expect(6);
    const getState = () => {
      return new ReduxDataHelper().hasSummaryData(true).eventCount(1).eventThreshold(2).build();
    };
    const myDispatch = (action) => {
      if (action && action.type === 'INVESTIGATE_EVENTS::UPDATE_SORT' && action.sortField === 'time' && action.sortDirection === 'Ascending') {
        return assert.ok(true);
      } else if (action && action.type === 'INVESTIGATE_EVENTS::SELECT_EVENTS' && action.payload.length === 0) {
        return assert.ok(true);
      } else if (action.type === 'INVESTIGATE_EVENTS::SORT_IN_CLIENT_BEGIN') {
        return assert.ok(true);
      } else if (action.type === 'INVESTIGATE_EVENTS::SORT_IN_CLIENT_COMPLETE') {
        return assert.ok(true);
      } else if (typeof action === 'function') {
        return assert.ok(true);
      } else {
        return assert.ok(false);
      }
    };
    const thunk = interactionCreators.setSort('time', 'Ascending');
    thunk(myDispatch, getState);
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

  test('toggleSelectAllEvents has the correctly sorted payload when all events were not selected before toggling', function(assert) {
    const getState = () => {
      return new ReduxDataHelper()
        .eventThreshold(100000)
        .eventResults(eventResultsData)
        .withoutMinimumCoreServicesVersionForColumnSorting()
        .language()
        .withPreviousQuery()
        .eventsQuerySort('medium', 'Descending')
        .selectedEventIds({
          0: 1,
          2: 3
        })
        .build();
    };
    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.SELECT_EVENTS, 'action has correct type');
      assert.deepEqual(action.payload, { 0: 3, 1: 1, 2: 2 }, 'action selects all events in descending order by medium');
    };

    const thunk = interactionCreators.toggleSelectAllEvents();

    thunk(dispatch, getState);
  });

  test('toggleSelectAllEvents action creator returns proper type and payload when all events were selected before toggling', function(assert) {
    const getState = () => {
      return new ReduxDataHelper()
        .eventResults(eventResultsData)
        .selectedEventIds({
          0: 1,
          1: 2,
          2: 3
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
          1: 2,
          2: 3
        })
        .build();
    };

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DESELECT_EVENT, 'action has the correct type');
      assert.equal(action.payload, 1, 'action has the correct payload');
    };

    const thunk = interactionCreators.toggleEventSelection({ sessionId: 1 }, 1);

    thunk(dispatch, getState);
  });

  test('toggleEventSelection action creator returns proper type and payload when selectedEventIds does not include the sessionId being toggled', function(assert) {
    const getState = () => {
      return new ReduxDataHelper()
        .eventResults(eventResultsData)
        .selectedEventIds({
          2: 3
        })
        .build();
    };

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.SELECT_EVENTS, 'action has the correct type');
      assert.deepEqual(action.payload, { 0: 1, 2: 3 }, 'action has the correct payload');
    };

    const thunk = interactionCreators.toggleEventSelection({ sessionId: 1 }, 0);

    thunk(dispatch, getState);
  });

  test('setColumnGroup - changing columms triggers off fetchInvestigateData which loads events according to the requested columns - OldestEvents',
    async function(assert) {
      const done = assert.async();
      let fetchInvestigateDispatchCount = 0;
      let columngrpDispatchCount = 0;
      let updateSortDispatchCount = 0;
      const getState = () => {
        return new ReduxDataHelper()
          .hasSummaryData(true)
          .isQueryRunning(queryIsRunning)
          .selectedColumnGroup('SUMMARY')
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
        } else if (updateSortDispatchCount === 0) {
          assert.equal(action.type, ACTION_TYPES.UPDATE_SORT, 'update sort dispatched');
          updateSortDispatchCount++;
        } else {
          assert.equal(action.type, ACTION_TYPES.SET_QUERY_EXECUTED_BY_COLUMN_GROUP_FLAG,
            'set isQueryExecutedByColumnGroup dispatched');
        }
      };

      const asserts = () => {
        // this assert sometimes fails sometimes passes
        // length of allActionsDispatched seems to be between 9 and 13 most times
        // count is 9 or 10 depending on whether QUERY_STATS dispatches twice
        // const actionsDispatchedCountCheck = allActionsDispatched.length > 8 && allActionsDispatched.length < 14;
        // assert.ok(actionsDispatchedCountCheck, 'total actions dispatched', allActionsDispatched.length);
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

  test('setProfile replaces query pills, sets column group, and executes query if column group and query both changed',
    function(assert) {
      assert.expect(4);
      const executeQuery = () => {
        assert.ok(true, 'executeQuery triggered only if new column group and new query');
      };

      const emailProfile = {
        name: 'RSA Email Analysis',
        columnGroup: {
          name: 'RSA Email Analysis',
          id: 'EMAIL'
        },
        preQueryPillsData: Immutable.from([ ...DEFAULT_PILLS_DATA, { type: 'text', searchTerm: 'newPill' } ]),
        contentType: 'OOTB'
      };

      let replaceAllGuidedPillsDispatchCount = 0;
      let colGroupDispatchCount = 0;
      let getStateCount = 0;
      const getState = () => {
        getStateCount++;
        // when getState() is called, return appropriate state
        // for isDirty check in setProfile to work
        return getStateCount === 2 ? _getState0('EMAIL', emailProfile.preQueryPillsData) : state0;
      };

      const setProfileDispatch = (action) => {
        if (typeof action !== 'function') {
          if (replaceAllGuidedPillsDispatchCount === 0) {
            // first, check that REPLACE_ALL_GUIDED_PILLS was dispatched
            assert.equal(action.type, ACTION_TYPES.REPLACE_ALL_GUIDED_PILLS, 'sent out action to replace all guided pills');
            assert.equal(action.payload.pillData.length, 6, 'action has correct number of pills (pre-query pills wrapped in parens)');
            replaceAllGuidedPillsDispatchCount++;
          } else if (colGroupDispatchCount === 0) {
            // second, check that SET_SELECTED_COLUMN_GROUP was dispatched
            assert.equal(action.type, ACTION_TYPES.SET_SELECTED_COLUMN_GROUP, 'sent out action to set column group');
            colGroupDispatchCount++;
          }
        }
      };

      // select a profile with different column group and different query
      const thunk = interactionCreators.setProfile(emailProfile, executeQuery);
      thunk(setProfileDispatch, getState);
    });

  test('setProfile replaces query pills if column group did not change and query changed',
    function(assert) {
      assert.expect(2);
      const executeQuery = () => {
        assert.ok(false, 'executeQuery shall not be triggered');
      };

      const summaryProfile = {
        name: 'SUMMARY',
        columnGroup: {
          name: 'SUMMARY',
          id: 'SUMMARY'
        },
        preQueryPillsData: Immutable.from([ ...DEFAULT_PILLS_DATA, ...TEXT_PILL_DATA ]),
        contentType: 'OOTB'
      };

      let replaceAllGuidedPillsDispatchCount = 0;
      let colGroupDispatchCount = 0;
      let getStateCount = 0;

      const getState = () => {
        getStateCount++;
        // when getState() is called, return appropriate state
        // for isDirty check in setProfile to work
        return getStateCount === 2 ? _getState0('SUMMARY', summaryProfile.preQueryPillsData) : state0;
      };

      const setProfileDispatch = (action) => {
        if (typeof action !== 'function') {
          if (replaceAllGuidedPillsDispatchCount === 0) {
            // first, check that REPLACE_ALL_GUIDED_PILLS was dispatched
            assert.equal(action.type, ACTION_TYPES.REPLACE_ALL_GUIDED_PILLS, 'sent out action to replace all guided pills');
            assert.equal(action.payload.pillData.length, 6, 'action has correct number of pills (pre-query pills wrapped in parens)');
            replaceAllGuidedPillsDispatchCount++;
          } else if (colGroupDispatchCount === 0) {
            // SET_SELECTED_COLUMN_GROUP shall not be dispatched
            assert.ok(false, 'shall not send out action to set column group');
            colGroupDispatchCount++;
          }
        }
      };

      // select a profile with same column group and different query
      const thunk = interactionCreators.setProfile(summaryProfile, executeQuery);
      thunk(setProfileDispatch, getState);
    });

  test('setProfile replaces query pills and sets column group if column group changed and query did not change',
    function(assert) {
      assert.expect(3);
      const executeQuery = () => {
        assert.ok(false, 'executeQuery shall not be triggered');
      };

      const emailProfile = {
        name: 'RSA Email Analysis',
        columnGroup: {
          name: 'RSA Email Analysis',
          id: 'EMAIL'
        },
        preQueryPillsData: Immutable.from(DEFAULT_PILLS_DATA),
        contentType: 'OOTB'
      };

      let replaceAllGuidedPillsDispatchCount = 0;
      let colGroupDispatchCount = 0;
      let getStateCount = 0;
      const getState = () => {
        getStateCount++;
        // when getState() is called, return appropriate state
        // for isDirty check in setProfile to work
        return getStateCount === 2 ? _getState0('EMAIL', emailProfile.preQueryPillsData) : state0;
      };

      const setProfileDispatch = (action) => {
        if (typeof action !== 'function') {
          if (replaceAllGuidedPillsDispatchCount === 0) {
            // first, check that REPLACE_ALL_GUIDED_PILLS was dispatched
            assert.equal(action.type, ACTION_TYPES.REPLACE_ALL_GUIDED_PILLS, 'sent out action to replace all guided pills');
            assert.equal(action.payload.pillData.length, 5, 'action has correct number of pills (pre-query pills wrapped in parens)');
            replaceAllGuidedPillsDispatchCount++;
          } else if (colGroupDispatchCount === 0) {
            // second, check that SET_SELECTED_COLUMN_GROUP was dispatched
            assert.equal(action.type, ACTION_TYPES.SET_SELECTED_COLUMN_GROUP, 'sent out action to set column group');
            colGroupDispatchCount++;
          }
        }
      };

      // select a profile with different column group and same query
      const thunk = interactionCreators.setProfile(emailProfile, executeQuery);
      thunk(setProfileDispatch, getState);
    });
});
