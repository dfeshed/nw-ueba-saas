import { EVENT_TYPES_BY_NAME } from 'recon/utils/event-types';
import { isLogEvent, isNetworkEvent } from 'recon/selectors/event-type-selectors';
import { module, test } from 'qunit';

module('Unit | Mixin | event-type-selector');

test('isLogEvent', function(assert) {
  assert.expect(2);

  const tests = {
    shouldNotBeLogEvent: isLogEvent({
      data: {
        eventType: {
          name: 'foo'
        }
      }
    }),
    shouldBeLogEvent: isLogEvent({
      data: {
        eventType: {
          name: EVENT_TYPES_BY_NAME.LOG.name
        }
      }
    })
  };

  assert.equal(tests.shouldNotBeLogEvent, false, 'isLogEvent should return false for non log events');
  assert.equal(tests.shouldBeLogEvent, true, 'isLogEvent should return true for log events');
});

test('isNetworkEvent', function(assert) {
  assert.expect(2);

  const tests = {
    shouldNotBeNetworkEvent: isNetworkEvent({
      data: {
        eventType: {
          name: 'foo'
        }
      }
    }),
    shouldBeNetworkEvent: isNetworkEvent({
      data: {
        eventType: {
          name: EVENT_TYPES_BY_NAME.NETWORK.name
        }
      }
    })
  };

  assert.equal(tests.shouldNotBeNetworkEvent, false, 'isNetworkEvent should return false for non network events');
  assert.equal(tests.shouldBeNetworkEvent, true, 'isNetworkEvent should return true for network events');
});