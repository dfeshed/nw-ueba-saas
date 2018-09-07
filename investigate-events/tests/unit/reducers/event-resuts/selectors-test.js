import { module, test } from 'qunit';
import {
  isEventResultsError,
  eventResultsErrorMessage,
  getNextPayloadSize
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

test('it pulls out correct formatted error message - type 1', function(assert) {
  const state = {
    investigate: {
      eventResults: {
        message: 'rule syntax error: expecting <unary operator> or <relational operator> here: "does 45454 && time="2018-04-09 15:48:00" - "2018-04-10 15:47:59""'
      }
    }
  };

  const formattedErrorMessage = eventResultsErrorMessage(state);
  assert.equal(formattedErrorMessage, 'syntax error: expecting <unary operator> or <relational operator> here: "does 45454 ', 'Correct error message');
});

test('it pulls out correct formatted error message - type 2', function(assert) {
  const state = {
    investigate: {
      eventResults: {
        message: 'rule syntax error: unrecognized key something'
      }
    }
  };

  const formattedErrorMessage = eventResultsErrorMessage(state);
  assert.equal(formattedErrorMessage, 'syntax error: unrecognized key something', 'Expected error message');
});

test('determines and returns the original message if not type 1 or 2', function(assert) {
  const state = {
    investigate: {
      eventResults: {
        message: 'The language key "someweirdtextishere" exceeds the maximum size of 16'
      }
    }
  };

  const formattedErrorMessage = eventResultsErrorMessage(state);
  assert.equal(formattedErrorMessage, 'The language key "someweirdtextishere" exceeds the maximum size of 16', 'Expected error message');
});

test('fetches next events payload size correctly', function(assert) {

  const state0 = {
    investigate: {
      eventResults: {
        goal: 200,
        streamGoal: 100,
        status: 'stopped'
      },
      eventCount: {
        data: 330
      }
    }
  };

  const state1 = {
    investigate: {
      eventResults: {
        goal: 300,
        streamGoal: 100,
        status: 'stopped'
      },
      eventCount: {
        data: 330
      }
    }
  };

  const state2 = {
    investigate: {
      eventResults: {
        goal: 400,
        streamGoal: 100,
        status: 'stopped'
      },
      eventCount: {
        data: 330
      }
    }
  };

  const intermediatePayloadSize = getNextPayloadSize(state0);
  assert.equal(intermediatePayloadSize, 100, 'Intermediate events payload size is equal to streamGoal size');

  const lastPayloadSize = getNextPayloadSize(state1);
  assert.equal(lastPayloadSize, 30, 'Last events payload size is 30');

  const nextToLastPayloadSize = getNextPayloadSize(state2);
  assert.equal(nextToLastPayloadSize, 0, 'Next to last events payload size is 0');
});
