import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';

import {
  isHttpData,
  isLogEvent,
  isEndpointEvent,
  nweCallbackId,
  eventType,
  eventTypeFromMetaArray
} from 'recon/reducers/meta/selectors';

module('Unit | selector | meta');

test('isHttpData', function(assert) {
  assert.expect(4);

  const tests = {
    serviceZero: isHttpData(Immutable.from({
      meta: {
        meta: [['service', 0]]
      }
    })),
    serviceEighty: isHttpData(Immutable.from({
      meta: {
        meta: [['service', 80]]
      }
    })),
    emptyMeta: isHttpData(Immutable.from({
      meta: {
        meta: []
      }
    })),
    noMeta: isHttpData(Immutable.from({
      meta: {
        meta: null
      }
    }))
  };

  assert.equal(tests.emptyMeta, false, 'isHttpData should return false when no meta');
  assert.equal(tests.serviceZero, false, 'isHttpData should return false for non http service');
  assert.equal(tests.serviceEighty, true, 'isHttpData should return true for http events');
  assert.equal(tests.noMeta, false, 'isHttpData should return false when no meta');
});

test('isEndpointEvent', function(assert) {
  assert.expect(4);

  const tests = {
    hasCallbackIdInMeta: isEndpointEvent(Immutable.from({
      meta: {
        meta: [['nwe.callback_id', null]]
      }
    })),
    hasNoCallbackIdInMeta: isEndpointEvent(Immutable.from({
      meta: {
        meta: [['medium', 1]]
      }
    })),
    emptyMeta: isHttpData(Immutable.from({
      meta: {
        meta: []
      }
    })),
    noMeta: isEndpointEvent(Immutable.from({
      meta: {
        meta: null
      }
    }))
  };

  assert.equal(tests.hasCallbackIdInMeta, true, 'isEndpointEvent should return true when callback_id in meta');
  assert.equal(tests.hasNoCallbackIdInMeta, false, 'isEndpointEvent should return false when no callback_id in meta');
  assert.equal(tests.emptyMeta, false, 'isEndpointEvent should return false when empty meta');
  assert.equal(tests.noMeta, false, 'isEndpointEvent should return false when no meta');
});

test('isLogEvent', function(assert) {
  assert.expect(5);

  const tests = {
    mediumIsNotLog: isLogEvent(Immutable.from({
      meta: {
        meta: [['medium', 1]]
      }
    })),
    mediumIsLog: isLogEvent(Immutable.from({
      meta: {
        meta: [['medium', 32]]
      }
    })),
    mediumIsLogAndIsEndpoint: isLogEvent(Immutable.from({
      meta: {
        meta: [['medium', 32], ['nwe.callback_id', null]]
      }
    })),
    emptyMeta: isLogEvent(Immutable.from({
      meta: {
        meta: []
      }
    })),
    noMeta: isLogEvent(Immutable.from({
      meta: {
        meta: null
      }
    }))
  };

  assert.equal(tests.mediumIsNotLog, false, 'isLogEvent should return false for non log events');
  assert.equal(tests.mediumIsLog, true, 'isLogEvent should return true for log events');
  assert.equal(tests.mediumIsLogAndIsEndpoint, false, 'isLogEvent should return false for log events that are endpoint events');
  assert.equal(tests.emptyMeta, false, 'isLogEvent should return false when no medium');
  assert.equal(tests.noMeta, false, 'isLogEvent should return false when no meta');
});

test('nweCallbackId', function(assert) {
  assert.expect(5);

  const tests = {
    isNotEndpoint: nweCallbackId(Immutable.from({
      meta: {
        meta: [['medium', 1]]
      }
    })),
    isEndpointNoAgent: nweCallbackId(Immutable.from({
      meta: {
        meta: [['nwe.callback_id', null]]
      }
    })),
    isEndpointWithAgent: nweCallbackId(Immutable.from({
      meta: {
        meta: [['nwe.callback_id', null], ['agent.id', 500]]
      }
    })),
    emptyMeta: nweCallbackId(Immutable.from({
      meta: {
        meta: []
      }
    })),
    noMeta: nweCallbackId(Immutable.from({
      meta: {
        meta: null
      }
    }))
  };

  assert.equal(tests.isNotEndpoint, undefined, 'nweCallbackId should return undefined if not endpoint event');
  assert.equal(tests.isEndpointNoAgent, undefined, 'nweCallbackId should return undefined if endpoint event without agent  id');
  assert.equal(tests.isEndpointWithAgent, 500, 'nweCallbackId should return value if endpoint event without agent  id');
  assert.equal(tests.noMeta, undefined, 'nweCallbackId should return undefined if no meta');
  assert.equal(tests.emptyMeta, undefined, 'nweCallbackId should return undefined if empty meta');
});

test('eventType', function(assert) {
  assert.expect(6);

  const tests = {
    network: eventType(Immutable.from({
      meta: {
        meta: [['medium', 1]]
      }
    })),
    log: eventType(Immutable.from({
      meta: {
        meta: [['medium', 32]]
      }
    })),
    randomMedium: eventType(Immutable.from({
      meta: {
        meta: [['medium', 5000]]
      }
    })),
    metaNoMedium: eventType(Immutable.from({
      meta: {
        meta: [['foo', 10]]
      }
    })),
    emptyMeta: eventType(Immutable.from({
      meta: {
        meta: []
      }
    })),
    noMeta: eventType(Immutable.from({
      meta: {
        meta: null
      }
    }))
  };

  assert.equal(tests.network.name, 'NETWORK', 'eventType should return network when network event');
  assert.equal(tests.log.name, 'LOG', 'eventType should return log when log event');
  assert.equal(tests.randomMedium.name, 'NETWORK', 'eventType should return network when medium not recognized');
  assert.equal(tests.metaNoMedium.name, 'NETWORK', 'eventType should return network when meta but no medium');
  assert.equal(tests.emptyMeta.name, 'NETWORK', 'eventType should return network when empty meta');
  assert.equal(tests.noMeta.name, 'NETWORK', 'eventType should return network when no meta');
});

test('eventTypeFromMetaArray', function(assert) {
  assert.expect(6);

  const tests = {
    network: eventTypeFromMetaArray(Immutable.from([['medium', 1]])),
    log: eventTypeFromMetaArray(Immutable.from([['medium', 32]])),
    randomMedium: eventTypeFromMetaArray(Immutable.from([['medium', 5000]])),
    metaNoMedium: eventTypeFromMetaArray(Immutable.from([['foo', 10]])),
    emptyMeta: eventTypeFromMetaArray(Immutable.from([])),
    noMeta: eventTypeFromMetaArray(Immutable.from(null))
  };

  assert.equal(tests.network.name, 'NETWORK', 'eventType should return network when network event');
  assert.equal(tests.log.name, 'LOG', 'eventType should return log when log event');
  assert.equal(tests.randomMedium.name, 'NETWORK', 'eventType should return network when medium not recognized');
  assert.equal(tests.metaNoMedium.name, 'NETWORK', 'eventType should return network when meta but no medium');
  assert.equal(tests.emptyMeta.name, 'NETWORK', 'eventType should return network when empty meta');
  assert.equal(tests.noMeta.name, 'NETWORK', 'eventType should return network when no meta');
});