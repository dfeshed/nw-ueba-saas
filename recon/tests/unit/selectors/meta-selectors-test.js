import {
  isHttpData,
  isNotHttpData,
  isLogEvent,
  isNetworkEvent
} from 'recon/reducers/meta/selectors';
import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';

module('Unit | Mixin | meta-selectors');

const generateHttpDataTests = function(selector) {
  return {
    shouldNotBeHttpData: selector(Immutable.from({
      meta: {
        meta: []
      }
    })),
    shouldAlsoNotBeHttpData: selector(Immutable.from({
      meta: {
        meta: [['service', 0]]
      }
    })),
    shouldBeHttpData: selector(Immutable.from({
      meta: {
        meta: [['service', 80]]
      }
    }))
  };
};

test('isHttpData', function(assert) {
  assert.expect(3);
  const tests = generateHttpDataTests(isHttpData);
  assert.equal(tests.shouldNotBeHttpData, false, 'isHttpData should return false when no meta');
  assert.equal(tests.shouldAlsoNotBeHttpData, false, 'isHttpData should return false for non http service');
  assert.equal(tests.shouldBeHttpData, true, 'isHttpData should return true for http events');
});

test('isNotHttpData', function(assert) {
  assert.expect(3);
  const tests = generateHttpDataTests(isNotHttpData);
  assert.equal(tests.shouldNotBeHttpData, true, 'isNotHttpData should return true when no meta');
  assert.equal(tests.shouldAlsoNotBeHttpData, true, 'isNotHttpData should return true for non http service');
  assert.equal(tests.shouldBeHttpData, false, 'isNotHttpData should return false for http events');
});

test('isLogEvent', function(assert) {
  assert.expect(2);

  const tests = {
    shouldNotBeLogEvent: isLogEvent(Immutable.from({
      meta: {
        meta: [['medium', 1]]
      }
    })),
    shouldBeLogEvent: isLogEvent(Immutable.from({
      meta: {
        meta: [['medium', 32]]
      }
    }))
  };

  assert.equal(tests.shouldNotBeLogEvent, false, 'isLogEvent should return false for non log events');
  assert.equal(tests.shouldBeLogEvent, true, 'isLogEvent should return true for log events');
});

test('isNetworkEvent', function(assert) {
  assert.expect(2);

  const tests = {
    shouldNotBeNetworkEvent: isNetworkEvent(Immutable.from({
      meta: {
        meta: [['medium', 32]]
      }
    })),
    shouldBeNetworkEvent: isNetworkEvent(Immutable.from({
      meta: {
        meta: [['medium', 1]]
      }
    }))
  };

  assert.equal(tests.shouldNotBeNetworkEvent, false, 'isNetworkEvent should return false for non network events');
  assert.equal(tests.shouldBeNetworkEvent, true, 'isNetworkEvent should return true for network events');
});