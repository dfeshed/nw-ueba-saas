import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';

import {
  eventsStartOldest,
  _deriveSort,
  toggleEventRelationships,
  toggleSplitSession
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

  test('toggleEventRelationships action creator returns proper type', function(assert) {
    const { type } = toggleEventRelationships();
    assert.equal(type, ACTION_TYPES.TOGGLE_EVENT_RELATIONSHIPS, 'action has the correct type');
  });

  test('_deriveSort', function(assert) {
    assert.deepEqual(_deriveSort(
      'time',
      'Ascending',
      {
        investigate: {
          services: {
            serviceData: [{ version: 11.4 }]
          },
          eventCount: {
            data: 5,
            threshold: 5
          }
        }
      }
    ), {
      field: 'time',
      descending: false
    });

    assert.notOk(_deriveSort(
      'time',
      'Ascending',
      {
        investigate: {
          services: {
            serviceData: [{ version: 11.3 }]
          },
          eventCount: {
            data: 5,
            threshold: 5
          }
        }
      }
    ));

    assert.notOk(_deriveSort(
      'time',
      'Ascending',
      {
        investigate: {
          services: {
            serviceData: [{ version: 11.4 }]
          },
          eventCount: {
            data: 5,
            threshold: 4
          }
        }
      }
    ));
  });

  test('Retrieves oldest data when does not hit limit', function(assert) {
    assert.expect(10);
    const done = assert.async();
    const getState = () => {
      return new ReduxDataHelper()
        .isQueryRunning(queryIsRunning)
        .selectedColumnGroup('SUMMARY')
        .columnGroups()
        .endTime(1544026619)
        .eventTimeSortOrder()
        .startTime(1513940220)
        .streamLimit(streamLimit)
        .streamBatch(streamBatch)
        .serviceId('789')
        .pillsDataPopulated()
        .metaFilter()
        .eventResultsStatus(status)
        .hasRequiredValuesToQuery(true)
        .eventResults(queryResults)
        .eventCount(500)
        .language()
        .build();
    };
    streamBatch = 250;
    // limit of 1000, 500 get returned so stream ends naturally
    streamLimit = 1000;

    const asserts = () => {
      assert.equal(allActionsDispatched.length, 5, 'total actions dispatched');
      assert.equal(queryResults.length, 500, 'total results accumulated');
      const selectedEvent = queryResults.objectAt(2);
      assert.ok(selectedEvent.hasOwnProperty('nwe.callback_id') === selectedEvent.hasOwnProperty('category'), 'event has category if it is an enpoint event');
      assert.equal(actionsByType[ACTION_TYPES.SET_EVENTS_PAGE].length, 2, '2 pages of data dispatched');
      assert.equal(actionsByType[ACTION_TYPES.QUERY_IS_RUNNING].length, 1, 'query not running just one time');
      assert.equal(actionsByType[ACTION_TYPES.INIT_EVENTS_STREAMING].length, 1, 'initialize streaming just one time');
      assert.equal(actionsByType[ACTION_TYPES.SET_EVENTS_PAGE_STATUS].length, 1, 'set status once');
      assert.equal(actionsByType[ACTION_TYPES.SET_EVENTS_PAGE_STATUS][0].payload, 'complete',
        'first status call is to indicate between streams');
      done();
    };

    const eventsStartOldestThunk = eventsStartOldest();
    eventsStartOldestThunk(downstreamOldestDispatchCreator(assert, asserts, getState), getState);
  });

  test('toggleSplitSession action creator returns proper type', function(assert) {
    const { type, tuple, relatedEvents, parentIndex } = toggleSplitSession('foo', 'bar', 'baz');
    assert.equal(type, ACTION_TYPES.TOGGLE_SPLIT_SESSION, 'action has the correct type');
    assert.equal(tuple, 'foo', 'action has the correct tuple');
    assert.equal(relatedEvents, 'bar', 'action has the correct relatedEvents');
    assert.equal(parentIndex, 'baz', 'action has the correct parentIndex');
  });

});
