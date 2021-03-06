import { module, test } from 'qunit';
import { urlUtil } from 'component-lib/utils/window-proxy';
import { buildInvestigateUrl, buildHostsUrl, buildEventAnalysisUrl } from 'component-lib/utils/build-url';

const selection = {
  'moduleName': 'EventAnalysisPanel',
  'metaName': 'ip.src',
  'metaValue': '17.127.255.150'
};

const contextDetails = {
  'startTime': 1508091780,
  'endTime': 1508178179,
  'queryConditions': [],
  'endpointId': '555d9a6fe4b0d37c827d402e',
  'language': [
    { 'count': 0,
      'format': 'TimeT',
      'metaName': 'time',
      'flags': -2147482605,
      'displayName': 'Time' },
    { 'count': 0,
      'format': 'Text',
      'metaName': 'risk.info',
      'flags': -2147483133,
      'displayName': 'Risk: Informational' },
    { 'count': 0, 'format': 'Text', 'metaName': 'risk.suspicious', 'flags': -2147483133, 'displayName': 'Risk: Suspicious' },
    { 'count': 0, 'format': 'Text', 'metaName': 'risk.warning', 'flags': -2147483133, 'displayName': 'Risk: Warning' },
    { 'count': 0, 'format': 'IPv6', 'metaName': 'tunnel.ipv6.dst', 'flags': -2147483647, 'displayName': 'Tunnel Destination IPv6 Address' }
  ]
};

module('Unit | Utility | build-url', function() {

  test('it forms the investigate Url', function(assert) {
    const investigateUrl = buildInvestigateUrl(selection, '=', contextDetails);
    assert.equal(investigateUrl, '/investigation/endpointid/555d9a6fe4b0d37c827d402e/navigate/query/ip.src%2520%253D%252017.127.255.150/date/2017-10-15T18:23:00Z/2017-10-16T18:22:59Z', 'Investigate Url formed');
  });

  test('it forms the investigate host Url', function(assert) {
    const hostUrl = buildHostsUrl(selection, contextDetails);
    assert.equal(hostUrl, '/investigate/hosts?query=ip.src%20%3D%2017.127.255.150', 'expected host url formed');
  });

  test('it forms the Event Analysis Url with pdhash if its there in the url', function(assert) {
    urlUtil.getWindowLocationHRef = function() {
      return 'https://0.0.0.0/investigate/events?et=1561555598&pdhash=23vfg&sid=58ad1076-2eee-48b3-b53e-ef8e2ce5a668&st=1560950798&sortField=time&sortDir=Ascending';
    };
    const eventEnalysisUrl = buildEventAnalysisUrl(selection, '=', contextDetails);
    assert.ok(eventEnalysisUrl.indexOf('pdhash') > 0, 'expected pdhash in url formed');
    assert.ok(eventEnalysisUrl.indexOf('&mf=ip.src%2520%253D%252017.127.255.150') > 0, 'expected host url formed');
  });

  test('it forms the Event Analysis Url with no pdhash if its not there in the url', function(assert) {
    urlUtil.getWindowLocationHRef = function() {
      return 'https://0.0.0.0/investigate/events?foo=bar';
    };
    const eventEnalysisUrl = buildEventAnalysisUrl(selection, '=', contextDetails);
    assert.notOk(eventEnalysisUrl.indexOf('pdhash') > 0, 'expected pdhash in url formed');
    assert.ok(eventEnalysisUrl.indexOf('&mf=ip.src%2520%253D%252017.127.255.150') > 0, 'expected host url formed');
  });

  test('it forms the Event Analysis for refocus, throws away pdhash', function(assert) {
    urlUtil.getWindowLocationHRef = function() {
      return 'https://0.0.0.0/investigate/events?et=1561555598&pdhash=23vfg&sid=58ad1076-2eee-48b3-b53e-ef8e2ce5a668&st=1560950798&sortField=time&sortDir=Ascending';
    };
    selection.metaValue = '17.127.255.250';
    const eventEnalysisUrl = buildEventAnalysisUrl(selection, '=', contextDetails, true);
    assert.ok(eventEnalysisUrl.indexOf('mf=ip.src%2520%253D%252017.127.255.250') > 0, 'expected host url formed');
    assert.ok(eventEnalysisUrl.indexOf('pdhash') === -1, 'expected no pdhash url formed');
    selection.metaValue = '17.127.255.150';
  });

  test('it forms the Event Analysis for Apply Drill with complex filter', function(assert) {
    urlUtil.getWindowLocationHRef = function() {
      return 'https://0.0.0.0/investigate/events?mf=ip.src%2520%253D%252017.127.255.250';
    };
    selection.metaValue = '17.127.255.150';
    const eventEnalysisUrl = buildEventAnalysisUrl(selection, '=', contextDetails, false);
    assert.ok(eventEnalysisUrl.indexOf('mf=ip.src%2520%253D%252017.127.255.250/ip.src%2520%253D%252017.127.255.150') > 0, 'expected host url formed');
    selection.metaValue = '17.127.255.150';
  });

});
