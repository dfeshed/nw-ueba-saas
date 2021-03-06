import { test, module } from 'qunit';
import Immutable from 'seamless-immutable';

import * as ACTION_TYPES from 'investigate-events/actions/types';
import reducer from 'investigate-events/reducers/investigate/query-stats/reducer';

module('Unit | Reducers | query-stats | Investigate');

test('TOGGLE_QUERY_CONSOLE reducer toggles isConsoleOpen', function(assert) {
  const prevState = Immutable.from({
    isConsoleOpen: false
  });
  const action = {
    type: ACTION_TYPES.TOGGLE_QUERY_CONSOLE
  };
  const result = reducer(prevState, action);
  assert.equal(result.isConsoleOpen, true);
  const nextResult = reducer(result, action);
  assert.equal(nextResult.isConsoleOpen, false);
});

test('DELETE_GUIDED_PILLS reducer closes isConsoleOpen', function(assert) {
  const prevState = Immutable.from({
    isConsoleOpen: true
  });
  const action = {
    type: ACTION_TYPES.DELETE_GUIDED_PILLS
  };
  const result = reducer(prevState, action);
  assert.equal(result.isConsoleOpen, false);
  const nextResult = reducer(result, action);
  assert.equal(nextResult.isConsoleOpen, false);
});

test('QUERY_STATS reducer updates stats', function(assert) {
  const prevState = Immutable.from({
    description: null,
    percent: 0,
    errors: [],
    warnings: [],
    devices: []
  });
  const action = {
    type: ACTION_TYPES.QUERY_STATS,
    code: 0,
    time: Date.now(),
    payload: {
      description: 'foo',
      percent: 50,
      serviceId: 'bar',
      warning: 'warning',
      devices: [{
        serviceId: 'baz'
      }]
    }
  };
  const result = reducer(prevState, action);

  assert.equal(result.description, 'foo');
  assert.equal(result.percent, 50);
  assert.equal(result.warnings.length, 1);
  assert.equal(result.warnings[0].serviceId, 'bar');
  assert.equal(result.warnings[0].warning, 'warning');
  assert.equal(result.devices.length, 1);
  assert.equal(result.devices[0].serviceId, 'baz');

  const nextResult = reducer(result, action);
  assert.equal(nextResult.warnings.length, 2);
  assert.equal(nextResult.warnings[0].serviceId, 'bar');
  assert.equal(nextResult.warnings[0].warning, 'warning');
  assert.equal(nextResult.warnings[1].serviceId, 'bar');
  assert.equal(nextResult.warnings[1].warning, 'warning');

  const lastResult = reducer(nextResult, {
    type: ACTION_TYPES.QUERY_STATS,
    payload: {},
    time: Date.now()
  });

  assert.equal(lastResult.description, 'foo');
  assert.equal(lastResult.percent, 50);
  assert.equal(lastResult.devices.length, 1);
  assert.equal(lastResult.devices[0].serviceId, 'baz');
});

test('QUERY_STATS reducer updates percent to 99 when percent is 100', function(assert) {
  const prevState = Immutable.from({
    percent: 0
  });
  const action = {
    type: ACTION_TYPES.QUERY_STATS,
    time: Date.now(),
    payload: {
      percent: 100
    }
  };
  const result = reducer(prevState, action);

  assert.equal(result.percent, 99);
});

test('QUERY_STATS reducer updates errors when code/message passed', function(assert) {
  const prevState = Immutable.from({
    description: null,
    percent: 0,
    errors: [],
    warnings: [],
    devices: []
  });
  const action = {
    type: ACTION_TYPES.QUERY_STATS,
    code: 1,
    time: Date.now(),
    payload: {
      message: 'error message',
      code: 1
    }
  };
  const result = reducer(prevState, action);
  assert.equal(result.errors.length, 1);
  assert.equal(result.errors[0].error, 'error message');

  const nextResult = reducer(result, action);
  assert.equal(nextResult.errors.length, 2);
  assert.equal(nextResult.errors[0].error, 'error message');
  assert.equal(nextResult.errors[1].error, 'error message');
});

test('QUERY_STATS reducer does not update errors when 0 code passed', function(assert) {
  const prevState = Immutable.from({
    description: null,
    percent: 0,
    errors: [],
    warnings: [],
    devices: []
  });
  const action = {
    type: ACTION_TYPES.QUERY_STATS,
    code: 0,
    time: Date.now(),
    payload: {
      message: 'message'
    }
  };
  const result = reducer(prevState, action);
  assert.equal(result.errors.length, 0);
});

test('QUERY_STATS reducer does not update streamingStartedTime when no devices passed', function(assert) {
  const prevState = Immutable.from({
    description: null,
    percent: 0,
    errors: [],
    warnings: [],
    streamingStartedTime: null
  });
  const action = {
    type: ACTION_TYPES.QUERY_STATS,
    code: 0,
    time: Date.now(),
    payload: {
      message: 'message'
    }
  };
  const result = reducer(prevState, action);
  assert.equal(result.streamingStartedTime, null);
});

test('QUERY_STATS reducer does update streamingStartedTime when devices passed', function(assert) {
  const time = Date.now();

  const prevState = Immutable.from({
    description: null,
    percent: 0,
    errors: [],
    warnings: [],
    devices: [],
    streamingStartedTime: null
  });
  const action = {
    type: ACTION_TYPES.QUERY_STATS,
    code: 0,
    time,
    payload: {
      message: 'message',
      devices: [{
        serviceId: 'baz'
      }]
    }
  };
  const result = reducer(prevState, action);
  assert.equal(result.streamingStartedTime, time);
});

test('INITIALIZE_INVESTIGATE reducer clears state', function(assert) {
  const prevState = Immutable.from({
    isConsoleOpen: true,
    description: 'foo',
    percent: 100,
    warnings: ['foo'],
    errors: ['foo'],
    devices: ['foo']
  });
  const action = {
    type: ACTION_TYPES.INITIALIZE_INVESTIGATE
  };
  const result = reducer(prevState, action);

  assert.equal(result.isConsoleOpen, false);
  assert.equal(result.description, null);
  assert.equal(result.percent, 0);
  assert.equal(result.errors.length, 0);
  assert.equal(result.warnings.length, 0);
  assert.equal(result.devices.length, 0);
});

test('START_GET_EVENT_COUNT reducer clears errors and warnings', function(assert) {
  const prevState = Immutable.from({
    warnings: ['warning'],
    errors: ['error']
  });

  const action = {
    type: ACTION_TYPES.START_GET_EVENT_COUNT
  };

  const result = reducer(prevState, action);

  assert.equal(result.errors.length, 0);
  assert.equal(result.warnings.length, 0);
});

test('SET_EVENTS_PAGE_STATUS sets streamingEndedTime', function(assert) {
  const prevState = Immutable.from({
    streamingEndedTime: null
  });
  const action = {
    type: ACTION_TYPES.SET_EVENTS_PAGE_STATUS,
    streamingEndedTime: 1
  };
  const result = reducer(prevState, action);

  assert.equal(result.streamingEndedTime, 1);
});
