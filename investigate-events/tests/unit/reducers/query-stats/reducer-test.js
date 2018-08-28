import { test, module } from 'qunit';
import Immutable from 'seamless-immutable';

import * as ACTION_TYPES from 'investigate-events/actions/types';
import reducer from 'investigate-events/reducers/investigate/query-stats/reducer';

module('Unit | Reducers | query-stats | Investigate');

test('TOGGLE_QUERY_CONSOLE reducer toggles consoleIsOpen', function(assert) {
  const prevState = Immutable.from({
    consoleIsOpen: false
  });
  const action = {
    type: ACTION_TYPES.TOGGLE_QUERY_CONSOLE
  };
  const result = reducer(prevState, action);
  assert.equal(result.consoleIsOpen, true);
  const nextResult = reducer(result, action);
  assert.equal(nextResult.consoleIsOpen, false);
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
    payload: {
      description: 'foo',
      percent: 50,
      error: 'error',
      serviceId: 'bar',
      warning: 'warning',
      devices: [{
        id: 'baz'
      }]
    }
  };
  const result = reducer(prevState, action);

  assert.equal(result.description, 'foo');
  assert.equal(result.percent, 50);
  assert.equal(result.errors.length, 1);
  assert.equal(result.errors[0].serviceId, 'bar');
  assert.equal(result.errors[0].error, 'error');
  assert.equal(result.warnings.length, 1);
  assert.equal(result.warnings[0].serviceId, 'bar');
  assert.equal(result.warnings[0].warning, 'warning');
  assert.equal(result.devices.length, 1);
  assert.equal(result.devices[0].id, 'baz');

  const nextResult = reducer(result, action);
  assert.equal(nextResult.errors.length, 2);
  assert.equal(nextResult.errors[0].serviceId, 'bar');
  assert.equal(nextResult.errors[0].error, 'error');
  assert.equal(nextResult.errors[1].serviceId, 'bar');
  assert.equal(nextResult.errors[1].error, 'error');
  assert.equal(nextResult.warnings.length, 2);
  assert.equal(nextResult.warnings[0].serviceId, 'bar');
  assert.equal(nextResult.warnings[0].warning, 'warning');
  assert.equal(nextResult.warnings[1].serviceId, 'bar');
  assert.equal(nextResult.warnings[1].warning, 'warning');
});

test('INITIALIZE_QUERYING reducer clears state', function(assert) {
  const prevState = Immutable.from({
    consoleIsOpen: true,
    description: 'foo',
    percent: 100,
    warnings: ['foo'],
    errors: ['foo'],
    devices: ['foo']
  });
  const action = {
    type: ACTION_TYPES.INITIALIZE_QUERYING
  };
  const result = reducer(prevState, action);

  assert.equal(result.consoleIsOpen, false);
  assert.equal(result.description, null);
  assert.equal(result.percent, 0);
  assert.equal(result.errors.length, 0);
  assert.equal(result.warnings.length, 0);
  assert.equal(result.devices.length, 0);
});
