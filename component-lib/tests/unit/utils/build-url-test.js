import { module, test } from 'qunit';
import { buildEventAnalysisUrl, classicEventsURL, extractHashWithoutTextHash, buildQuery } from 'component-lib/utils/build-url';
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
    '/investigate/events?et=1561485479&mf=sessionid%2520%253D%25201&sid=fd6ee950-1cae-4013-be6b-85b11dd04135&st=1561439460&rs=min',
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
    '/investigate/events?et=1561485479&mf=sessionid%2520%253D%25201&sid=fd6ee950-1cae-4013-be6b-85b11dd04135&st=1561439460&rs=min',
    'The url was malformed'
  );

});

test('it encodes searchTerm when creating a classic url with text filter', async function(assert) {
  const props = {
    endTime: '1508178179',
    startTime: '1508091780',
    timeRangeType: 'LAST_24_HOURS',
    serviceId: '555d9a6fe4b0d37c827d402e',
    pillDataHashes: ['wawa1'],
    textSearchTerm: { type: 'some', searchTerm: '^&foobar' },
    mid1: '1',
    mid2: '12296047',
    startCollectionTime: '1506537600',
    endCollectionTime: '1508178160'
  };
  const url = classicEventsURL(props);
  assert.equal(
    url,
    'investigation/555d9a6fe4b0d37c827d402e/events/wawa1/date/2017-10-15T18:23:00Z/2017-10-16T18:22:59Z?mid1=1&mid2=12296047&lastCollectionDate=1508178160&startCollectionDate=1506537600&timeRangeType=LAST_24_HOURS&search=%5E%26foobar',
    'The classic url was malformed'
  );
});

test('It returns a correct Classic events url based on the options passed in', async function(assert) {

  let props = {
    endTime: '1508178179',
    startTime: '1508091780',
    timeRangeType: 'LAST_24_HOURS',
    serviceId: '555d9a6fe4b0d37c827d402e',
    pillDataHashes: ['wawa1'],
    textSearchTerm: { type: 'some', searchTerm: 'foobar' },
    mid1: '1',
    mid2: '12296047',
    startCollectionTime: '1506537600',
    endCollectionTime: '1508178160'
  };


  let url = classicEventsURL(props);
  assert.equal(
    url,
    'investigation/555d9a6fe4b0d37c827d402e/events/wawa1/date/2017-10-15T18:23:00Z/2017-10-16T18:22:59Z?mid1=1&mid2=12296047&lastCollectionDate=1508178160&startCollectionDate=1506537600&timeRangeType=LAST_24_HOURS&search=foobar',
    'The classic url was malformed'
  );

  // If no hash, won't find it in url
  props = {
    ...props,
    pillDataHashes: ['']
  };
  url = classicEventsURL(props);
  assert.equal(
    url,
    'investigation/555d9a6fe4b0d37c827d402e/events/date/2017-10-15T18:23:00Z/2017-10-16T18:22:59Z?mid1=1&mid2=12296047&lastCollectionDate=1508178160&startCollectionDate=1506537600&timeRangeType=LAST_24_HOURS&search=foobar',
    'The classic url was malformed'
  );

  // If there are text hashes, include them only in searchTerm, not in `events/asd2323/`
  props = {
    ...props,
    pillDataHashes: ['wawa1', '˸foobar˸']
  };
  url = classicEventsURL(props);
  assert.equal(
    url,
    'investigation/555d9a6fe4b0d37c827d402e/events/wawa1/date/2017-10-15T18:23:00Z/2017-10-16T18:22:59Z?mid1=1&mid2=12296047&lastCollectionDate=1508178160&startCollectionDate=1506537600&timeRangeType=LAST_24_HOURS&search=foobar',
    'The classic url was malformed'
  );

  // text hashes can be the first entry in the array, ignore it too
  props = {
    ...props,
    pillDataHashes: ['˸foobar˸', 'wawa1']
  };
  url = classicEventsURL(props);
  assert.equal(
    url,
    'investigation/555d9a6fe4b0d37c827d402e/events/wawa1/date/2017-10-15T18:23:00Z/2017-10-16T18:22:59Z?mid1=1&mid2=12296047&lastCollectionDate=1508178160&startCollectionDate=1506537600&timeRangeType=LAST_24_HOURS&search=foobar',
    'The classic url was malformed'
  );
});

test('extractHashWithoutTextHash outputs string hash without text hash', async function(assert) {

  // no hash
  let pillDataHashes = [''];
  let hashString = extractHashWithoutTextHash(pillDataHashes);
  assert.equal(hashString, '', 'Shouldnt be a string');

  // with hashes
  pillDataHashes = ['foo', 'bar'];
  hashString = extractHashWithoutTextHash(pillDataHashes);
  assert.equal(hashString, 'foo,bar/', 'Should have a / added at the end');

  // with text hash
  pillDataHashes = ['foo', 'bar', '˸foobar˸'];
  hashString = extractHashWithoutTextHash(pillDataHashes);
  assert.equal(hashString, 'foo,bar/', 'Should have a / added at the end');

  // with text hash in front
  pillDataHashes = ['˸foobar˸', 'foo', 'bar'];
  hashString = extractHashWithoutTextHash(pillDataHashes);
  assert.equal(hashString, 'foo,bar/', 'Should have a / added at the end');

  // with text hash in middle (maybe)
  pillDataHashes = ['foo', '˸foobar˸', 'bar'];
  hashString = extractHashWithoutTextHash(pillDataHashes);
  assert.equal(hashString, 'foo,bar/', 'Should have a / added at the end');
});

test('buildQuery escapes \\ when one is encountered', function(assert) {
  const filters = [
    {
      meta: 'user.dst',
      operator: '=',
      value: '\\foobar\\r\\'
    }
  ];
  const metaFormatMap = {
    'user.dst': 'Text'
  };

  const queryString = buildQuery(filters, metaFormatMap);
  assert.equal(queryString, "user.dst = '\\\\foobar\\\\r\\\\'", 'Incorrect string found, not escaped');
});