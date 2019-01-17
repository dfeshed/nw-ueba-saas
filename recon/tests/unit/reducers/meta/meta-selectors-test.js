import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import moment from 'moment';

import {
  isHttpData,
  isLogEvent,
  isEndpointEvent,
  nweCallbackId,
  eventType,
  eventTypeFromMetaArray,
  processAnalysisQueryString,
  isProcessAnalysisDisabled,
  agentId,
  endpointServiceId,
  eventTime,
  eventCategory,
  hostName,
  user,
  endpointMeta
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

test('processAnalysisQueryString test', function(assert) {
  const data = {
    investigate: {
      queryNode: {
        serviceId: '123456'
      }
    },
    recon: {
      data: {
        queryInputs: {
          startTime: 10,
          endTime: 12
        }
      },
      meta: {
        meta: [
          ['OS', 'windows'],
          ['checksum.src', 'test-checksum'],
          ['agent.id', 'abcd'],
          ['filename.src', 'testfile'],
          ['process.vid.src', 1],
          ['alias.host', 'TestHostName']
        ]
      }
    }
  };
  const result = processAnalysisQueryString(Immutable.from(data));
  assert.equal(result, 'checksum=test-checksum&serverId=123456&sid=123456&aid=abcd&hn=TestHostName&pn=testfile&osType=windows&vid=1&st=10&et=12', 'should return valid queryString');
});

test('processAnalysisQueryString test as if Respond opened Recon', function(assert) {
  const data = {
    investigate: {
      queryNode: undefined
    },
    recon: {
      data: {
        endpointId: 666,
        queryInputs: {}
      },
      meta: {
        meta: [
          ['OS', 'windows'],
          ['checksum.src', 'test-checksum'],
          ['agent.id', 'abcd'],
          ['filename.src', 'testfile'],
          ['process.vid.src', 1],
          ['alias.host', 'TestHostName']
        ]
      }
    }
  };
  const now = moment();
  const endDate = now.unix();
  const startDate = now.subtract(7, 'days').unix();
  const timeStr = `st=${startDate}&et=${endDate}`;
  const result = processAnalysisQueryString(Immutable.from(data));
  assert.equal(result, `checksum=test-checksum&serverId=666&sid=666&aid=abcd&hn=TestHostName&pn=testfile&osType=windows&vid=1&${timeStr}`, 'should return valid queryString');
});

test('isProcessAnalysisDisabled test', function(assert) {
  const data = {
    recon: {
      meta: {
        meta: [
          ['process.vid.src', 1]
        ]
      }
    }
  };
  const result = isProcessAnalysisDisabled(Immutable.from(data));
  assert.equal(result, false, 'should return valid boolean value for isProcessAnalysisDesabled');
});

test('agentId test', function(assert) {
  const data = {
    recon: { meta: { meta: [['agent.id', 'abc'] ] } }
  };
  const result = agentId(Immutable.from(data));
  assert.equal(result, 'abc', 'Agent id is returned');
});

test('endpointServiceId test', function(assert) {
  const data = {
    recon: { meta: { meta: [['nwe.callback_id', 'nwe://610b34a1-eeee-47a3-abec-74d2861bf99e']] } }
  };
  const result = endpointServiceId(Immutable.from(data));
  assert.equal(result, '610b34a1-eeee-47a3-abec-74d2861bf99e', 'Endpoint service ID is returned after parsing');
});

test('eventTime', function(assert) {
  const data = {
    recon: { meta: { meta: [['event.time', '2019-01-08T04:17:20.000+0000']] } }
  };
  const result = eventTime(Immutable.from(data));
  assert.equal(result, '2019-01-08T04:17:20.000+0000', 'event time is returned');
});

test('eventCategory', function(assert) {
  const data = {
    recon: { meta: { meta: [['category', 'Machine']] } }
  };
  const result = eventCategory(Immutable.from(data));
  assert.equal(result, 'Machine', 'event category is returned');
});

test('hostName', function(assert) {
  const data = {
    recon: { meta: { meta: [['alias.host', 'INENJOHNP3']] } }
  };
  const result = hostName(Immutable.from(data));
  assert.equal(result, 'INENJOHNP3', 'hostName is returned');
});

test('user', function(assert) {
  const data = {
    recon: { meta: { meta: [['user.src', 'INENJOHNP3']] } }
  };
  const result = user(Immutable.from(data));
  assert.equal(result, 'INENJOHNP3', 'user is returned');
});

test('endpointMeta', function(assert) {
  const data = {
    recon: { meta: { meta: [['user.src', 'INENJOHNP3'],
      ['category', 'Registry Event'],
      ['filename.src', 'cmd.exe'],
      ['action', 'modify'],
      ['registry.key', '/HKEY/WINDOWS/LOCAL']] } }
  };
  const result = endpointMeta(Immutable.from(data));
  assert.equal(result[0].field, 'filename.src', 'first field is filename.src');
  assert.equal(result[0].value, 'cmd.exe', 'first field value is correct');
  assert.equal(result[1].value, 'modify', 'action value is modify');
  assert.equal(result[2].value, '/HKEY/WINDOWS/LOCAL', 'registy.key value is /HKEY/WINDOWS/LOCAL');
});