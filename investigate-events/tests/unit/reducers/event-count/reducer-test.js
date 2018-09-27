import { test, module } from 'qunit';
import Immutable from 'seamless-immutable';

import * as ACTION_TYPES from 'investigate-events/actions/types';
import reducer from 'investigate-events/reducers/investigate/event-count/reducer';

module('Unit | Reducers | event-count | Investigate');

test('START_GET_EVENT_COUNT reducer toggles state', function(assert) {
  const prevState = Immutable.from({
    data: 'foo',
    status: 'bar',
    reason: 'baz'
  });
  const action = {
    type: ACTION_TYPES.START_GET_EVENT_COUNT
  };
  const result = reducer(prevState, action);
  assert.equal(result.data, undefined);
  assert.equal(result.status, 'wait');
  assert.equal(result.reason, undefined);
});

test('FAILED_GET_EVENT_COUNT reducer toggles state', function(assert) {
  const prevState = Immutable.from({
    data: 'foo',
    status: 'bar',
    reason: 'baz'
  });
  const action = {
    type: ACTION_TYPES.FAILED_GET_EVENT_COUNT,
    payload: 1
  };
  const result = reducer(prevState, action);
  assert.equal(result.status, 'rejected');
  assert.equal(result.reason, 1);
});

test('EVENT_COUNT_RESULTS reducer toggles state', function(assert) {
  const prevState = Immutable.from({
    data: 'foo',
    status: 'bar',
    reason: 'baz'
  });
  const action = {
    type: ACTION_TYPES.EVENT_COUNT_RESULTS,
    payload: {
      data: 'foo'
    }
  };
  const result = reducer(prevState, action);
  assert.equal(result.data, 'foo');
  assert.equal(result.status, 'resolved');
  assert.equal(result.reason, 0);
});
