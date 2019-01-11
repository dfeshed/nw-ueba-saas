import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';

import {
  eventsStartNewest,
  eventsStartOldest
} from 'investigate-events/actions/events-creators';
import * as ACTION_TYPES from 'investigate-events/actions/types';
import ReduxDataHelper from '../../helpers/redux-data-helper';

let noMoreEventsAllowed = false;
let allActionsDispatched = [];
let actionsByType = {};
let queryIsRunning = true;
let status = 'foo';
let queryResults = [];
let streamBatch = 250;
let streamLimit = 2000;


const getState = () => {
  return new ReduxDataHelper()
    .isQueryRunning(queryIsRunning)
    .columnGroup('SUMMARY')
    .columnGroups()
    .endTime(1544026619)
    .startTime(1513940220)
    .streamLimit(streamLimit)
    .streamBatch(streamBatch)
    .serviceId('789')
    .pillsDataPopulated()
    .metaFilter()
    .eventResultsStatus(status)
    .eventResults(queryResults)
    .eventCount(undefined)
    .language()
    .build();
};

const downstreamNewestDispatchCreator = (assert, asserts) => {

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

const downstreamOldestDispatchCreator = (assert, asserts) => {

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

module('Unit | Actions | event-creators', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    noMoreEventsAllowed = false;
    allActionsDispatched = [];
    queryIsRunning = true;
    status = 'foo';
    queryResults = [];
    streamBatch = 250;
    streamLimit = 2000;
    actionsByType = {};
  });

  test('Pages way through large query properly', function(assert) {
    assert.expect(10);
    const done = assert.async();

    const asserts = () => {
      assert.equal(allActionsDispatched.length, 8, 'total actions dispatched');
      assert.equal(queryResults.length, 2000, 'total results accumulated');
      assert.equal(actionsByType[ACTION_TYPES.SET_EVENTS_PAGE].length, 4, '4 pages of data dispatched');
      assert.equal(actionsByType[ACTION_TYPES.QUERY_IS_RUNNING].length, 1, 'query not running just one time');
      assert.equal(actionsByType[ACTION_TYPES.INIT_EVENTS_STREAMING].length, 1, 'initialize streaming just one time');
      assert.equal(actionsByType[ACTION_TYPES.SET_EVENTS_PAGE_STATUS].length, 2, 'set status twice');
      assert.equal(actionsByType[ACTION_TYPES.SET_EVENTS_PAGE_STATUS][0].payload, 'between-streams', 'first status call is to indicate between streams');
      assert.equal(actionsByType[ACTION_TYPES.SET_EVENTS_PAGE_STATUS][1].payload, 'complete', 'second status call is to indicate complete');
      done();
    };

    const eventsStartNewestThunk = eventsStartNewest();
    eventsStartNewestThunk(downstreamNewestDispatchCreator(assert, asserts), getState);
  });

  test('Pages way smaller range correctly', function(assert) {
    assert.expect(10);
    const done = assert.async();

    streamBatch = 250;
    streamLimit = 700;

    const asserts = () => {
      assert.equal(allActionsDispatched.length, 6, 'total actions dispatched');
      // only want 700, but 1000 will stream in as entire results have to be processed
      // (gets truncated in reducer)
      assert.equal(queryResults.length, 1000, 'total results accumulated');
      assert.equal(actionsByType[ACTION_TYPES.SET_EVENTS_PAGE].length, 2, '2 pages of data dispatched');
      assert.equal(actionsByType[ACTION_TYPES.QUERY_IS_RUNNING].length, 1, 'query not running just one time');
      assert.equal(actionsByType[ACTION_TYPES.INIT_EVENTS_STREAMING].length, 1, 'initialize streaming just one time');
      assert.equal(actionsByType[ACTION_TYPES.SET_EVENTS_PAGE_STATUS].length, 2, 'set status twice');
      assert.equal(actionsByType[ACTION_TYPES.SET_EVENTS_PAGE_STATUS][0].payload, 'between-streams', 'first status call is to indicate between streams');
      assert.equal(actionsByType[ACTION_TYPES.SET_EVENTS_PAGE_STATUS][1].payload, 'complete', 'second status call is to indicate complete');
      done();
    };

    const eventsStartNewestThunk = eventsStartNewest();
    eventsStartNewestThunk(downstreamNewestDispatchCreator(assert, asserts), getState);
  });

  test('Retrieves oldest data when does not hit limit', function(assert) {
    assert.expect(9);
    const done = assert.async();

    streamBatch = 250;
    // limit of 1000, 500 get returned so stream ends naturally
    streamLimit = 1000;

    const asserts = () => {
      assert.equal(allActionsDispatched.length, 5, 'total actions dispatched');
      assert.equal(queryResults.length, 500, 'total results accumulated');
      assert.equal(actionsByType[ACTION_TYPES.SET_EVENTS_PAGE].length, 2, '2 pages of data dispatched');
      assert.equal(actionsByType[ACTION_TYPES.QUERY_IS_RUNNING].length, 1, 'query not running just one time');
      assert.equal(actionsByType[ACTION_TYPES.INIT_EVENTS_STREAMING].length, 1, 'initialize streaming just one time');
      assert.equal(actionsByType[ACTION_TYPES.SET_EVENTS_PAGE_STATUS].length, 1, 'set status once');
      assert.equal(actionsByType[ACTION_TYPES.SET_EVENTS_PAGE_STATUS][0].payload, 'complete', 'first status call is to indicate between streams');
      done();
    };

    const eventsStartOldestThunk = eventsStartOldest();
    eventsStartOldestThunk(downstreamOldestDispatchCreator(assert, asserts), getState);
  });
});