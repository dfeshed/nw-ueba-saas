import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import alertReducer from 'respond/reducers/respond/alert';
import ACTION_TYPES from 'respond/actions/types';
import makePackAction from '../../helpers/make-pack-action';
import { copy } from 'ember-metal/utils';

const initialState = {
  info: null,
  infoStatus: null,
  events: null,
  eventsStatus: null,
  originalAlertStatus: null,
  originalAlert: null
};

module('Unit | Utility | Alert Profile Actions - Reducers');

test('When FETCH_ALERT_EVENTS starts, the eventStatus changes to wait', function(assert) {
  const initState = copy(initialState);
  const eventsStatus = 'wait';
  const expectedEndState = {
    ...initState,
    eventsStatus
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_ALERT_EVENTS });
  const endState = alertReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_ALERT_EVENTS fails, the eventsStatus changes to error', function(assert) {
  const initState = copy(initialState);
  const eventsStatus = 'error';
  const expectedEndState = {
    ...initState,
    eventsStatus
  };
  const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.FETCH_ALERT_EVENTS });
  const endState = alertReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_ALERT_EVENTS succeeds, the events and eventsStatus update appropriately', function(assert) {
  const initState = copy(initialState);
  const events = [{ testing: 123 }];
  const eventsStatus = 'success';
  const expectedEndState = {
    ...initState,
    events,
    eventsStatus
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.FETCH_ALERT_EVENTS, payload: { data: events } });
  const endState = alertReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_ALERT_DETAILS_STARTED, the infoStatus changes to streaming', function(assert) {
  const initState = copy(initialState);
  const infoStatus = 'streaming';
  const expectedEndState = {
    ...initState,
    infoStatus
  };
  const action = { type: ACTION_TYPES.FETCH_ALERT_DETAILS_STARTED };
  const endState = alertReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_ALERT_DETAILS_STREAM_INITIALIZED, the stream stop function is stored in state', function(assert) {
  const initState = copy(initialState);
  const stopInfoStream = function() {
    /* NOOP */
  };
  const expectedEndState = {
    ...initState,
    stopInfoStream
  };
  const action = { type: ACTION_TYPES.FETCH_ALERT_DETAILS_STREAM_INITIALIZED, payload: stopInfoStream };
  const endState = alertReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_ALERT_DETAILS_RETRIEVE_BATCH, the first data record is stored in state', function(assert) {
  const initState = copy(initialState);
  const info = { testing: 123 };
  const expectedEndState = {
    ...initState,
    info
  };
  const action = { type: ACTION_TYPES.FETCH_ALERT_DETAILS_RETRIEVE_BATCH, payload: { code: 0, data: [ info ] } };
  const endState = alertReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_ALERT_DETAILS_COMPLETED, the infoState is set to complete', function(assert) {
  const initState = copy(initialState);
  const expectedEndState = {
    ...initState,
    infoStatus: 'complete',
    stopInfoStream: null
  };
  const action = { type: ACTION_TYPES.FETCH_ALERT_DETAILS_COMPLETED };
  const endState = alertReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_ALERT_DETAILS_ERROR, the infoState is set to error', function(assert) {
  const initState = copy(initialState);
  const expectedEndState = {
    ...initState,
    infoStatus: 'error',
    stopInfoStream: null
  };
  const action = { type: ACTION_TYPES.FETCH_ALERT_DETAILS_ERROR };
  const endState = alertReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_ORIGINAL_ALERT starts, the originalAlertStatus changes to wait', function(assert) {
  const initState = copy(initialState);
  const originalAlertStatus = 'wait';
  const expectedEndState = {
    ...initState,
    originalAlertStatus
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_ORIGINAL_ALERT });
  const endState = alertReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_ORIGINAL_ALERT fails, the originalAlertStatus changes to error', function(assert) {
  const initState = copy(initialState);
  const originalAlertStatus = 'error';
  const expectedEndState = {
    ...initState,
    originalAlertStatus
  };
  const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.FETCH_ORIGINAL_ALERT });
  const endState = alertReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_ORIGINAL_ALERT succeeds, the originalAlert and originalAlertStatus update appropriately', function(assert) {
  const initState = copy(initialState);
  const originalAlert = { testing: 123 };
  const originalAlertStatus = 'complete';
  const expectedEndState = {
    ...initState,
    originalAlert,
    originalAlertStatus
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.FETCH_ORIGINAL_ALERT, payload: { data: originalAlert } });
  const endState = alertReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});