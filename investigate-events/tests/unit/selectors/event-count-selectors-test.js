import { module, test } from 'qunit';
import { isInvalidQuery } from 'investigate-events/reducers/investigate/event-count/selectors';

module('Unit | Selectors | event-count');

test('determine if query server response is invalid', function(assert) {
  const state = {
    investigate: {
      eventCount: {
        status: 'rejected',
        reason: 11
      }
    }
  };
  const queryInvalid = isInvalidQuery(state);

  assert.ok(queryInvalid, 'Query is invalid');
});

test('determine if query server response is valid', function(assert) {
  const state = {
    investigate: {
      eventCount: {
        status: 'resolved',
        reason: 0
      }
    }
  };
  const queryInvalid = isInvalidQuery(state);

  assert.notOk(queryInvalid, 'Query is valid');
});