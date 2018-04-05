import { module, test } from 'qunit';
import {
  isEventResultsError,
  eventResultsErrorMessage
} from 'investigate-events/reducers/investigate/event-results/selectors';

module('Unit | Selectors | event-results');

test('isEventResultsError is false when status is not error', function(assert) {
  const state = {
    investigate: {
      eventResults: {
        status: 'foo'
      }
    }
  };

  const isError = isEventResultsError(state);
  assert.ok(isError === false, 'is not in error');
});

test('isEventResultsError is true when status is error', function(assert) {
  const state = {
    investigate: {
      eventResults: {
        status: 'error'
      }
    }
  };

  const isError = isEventResultsError(state);
  assert.ok(isError === true, 'error message');
});

test('eventResultsErrorMessage returns error message', function(assert) {
  const state = {
    investigate: {
      eventResults: {
        message: 'fooooooo'
      }
    }
  };

  const errorMessage = eventResultsErrorMessage(state);
  assert.ok(errorMessage === 'fooooooo', 'error message');
});