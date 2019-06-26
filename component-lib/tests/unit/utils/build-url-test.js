import { module, test } from 'qunit';
import { buildEventAnalysisUrl } from 'component-lib/utils/build-url';
import { urlUtil } from 'component-lib/utils/window-proxy';

module('Unit | Util | build-url');

test('it returns a well formed url if right click actions are coming from investigate-events', async function(assert) {

  const contextDetails = {
    language: [{
      displayName: 'Time',
      flags: 2147484691,
      format: 'TimeT',
      metaName: 'time'
    }, {
      displayName: 'Session ID',
      flags: 2147483665,
      format: 'UInt64',
      metaName: 'sessionid'
    }]
  };
  const selected = {
    format: 'UInt64',
    metaName: 'sessionid',
    metaValue: 1,
    moduleName: 'EventAnalysisPanel'
  };

  urlUtil.getWindowLocationHRef = function() {
    return 'https://0.0.0.0/investigate/events?et=1561555598&sid=58ad1076-2eee-48b3-b53e-ef8e2ce5a668&st=1560950798&sortField=time&sortDir=Ascending';
  };

  const url = buildEventAnalysisUrl(selected, '=', contextDetails, false);
  assert.equal(
    url,
    '/investigate/events?et=1561555598&sid=58ad1076-2eee-48b3-b53e-ef8e2ce5a668&st=1560950798&sortField=time&sortDir=Ascending&mf=sessionid%2520%253D%25201',
    'The url was malformed'
  );

});

test('it returns a well formed url if right click actions are coming from outside investigate-events(respond)', async function(assert) {

  const contextDetails = {
    language: [{
      displayName: 'Time',
      flags: 2147484691,
      format: 'TimeT',
      metaName: 'time'
    }, {
      displayName: 'Session ID',
      flags: 2147483665,
      format: 'UInt64',
      metaName: 'sessionid'
    }],
    serviceId: 'fd6ee950-1cae-4013-be6b-85b11dd04135',
    startTime: '1561439460',
    endTime: '1561485479'
  };
  const selected = {
    format: 'UInt64',
    metaName: 'sessionid',
    metaValue: 1,
    moduleName: 'EventAnalysisPanel'
  };

  urlUtil.getWindowLocationHRef = function() {
    return 'https://0.0.0.0/respond/whatever/recon?foo=bar&bar=baz';
  };

  const url = buildEventAnalysisUrl(selected, '=', contextDetails, false);
  assert.equal(
    url,
    '/investigate/events?et=1561485479&mf=sessionid%2520%253D%25201&sid=fd6ee950-1cae-4013-be6b-85b11dd04135&st=1561439460&rs=min&sortField=time&sortDir=Ascending',
    'The url was malformed'
  );

});

test('it returns a well formed url if right click actions are coming from outside investigate-events(recon)', async function(assert) {

  const contextDetails = {
    language: [{
      displayName: 'Time',
      flags: 2147484691,
      format: 'TimeT',
      metaName: 'time'
    }, {
      displayName: 'Session ID',
      flags: 2147483665,
      format: 'UInt64',
      metaName: 'sessionid'
    }],
    serviceId: 'fd6ee950-1cae-4013-be6b-85b11dd04135',
    startTime: '1561439460',
    endTime: '1561485479'
  };
  const selected = {
    format: 'UInt64',
    metaName: 'sessionid',
    metaValue: 1,
    moduleName: 'EventAnalysisPanel'
  };

  urlUtil.getWindowLocationHRef = function() {
    return 'https://0.0.0.0/investigate/recon?foo=bar&bar=baz';
  };

  const url = buildEventAnalysisUrl(selected, '=', contextDetails, false);
  assert.equal(
    url,
    '/investigate/events?et=1561485479&mf=sessionid%2520%253D%25201&sid=fd6ee950-1cae-4013-be6b-85b11dd04135&st=1561439460&rs=min&sortField=time&sortDir=Ascending',
    'The url was malformed'
  );

});