import { nonSupportedActionList, nonUrlBasedActions } from 'rsa-context-menu/utils/non-url-actions-handler';
import { module, test } from 'qunit';
import sinon from 'sinon';

const selection = {
  'moduleName': 'EventGrid',
  'metaName': 'ip.src',
  'metaValue': '17.127.255.150'
};

const contextDetails = {
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
      'displayName': 'Risk: Informational' }
  ]
};

module('Unit | Utility | non-url-actions-handler', function() {

  test('this should return non supported actions list and common actions support', function(assert) {
    assert.equal(nonSupportedActionList.length, 9, `Currently following actions are not supported : ${nonSupportedActionList.concat(',')}`);
  });

  test('test nonUrlBasedActions for drillDownNewTabEquals', function(assert) {
    const url = '/investigation/endpointid/555d9a6fe4b0d37c827d402e/navigate/query/ip.src%2520%253D%252017.127.255.150/date/2017-10-15T18:23:00Z/2017-10-16T18:22:59Z';
    const spy = sinon.spy(window, 'open');
    nonUrlBasedActions.drillDownNewTabEquals([selection], contextDetails);
    assert.ok(spy.withArgs(url));
    spy.restore();
  });

  test('test nonUrlBasedActions for copyToClipboard', function(assert) {
    const spy = sinon.spy(document, 'execCommand');
    nonUrlBasedActions.copyMetaAction([selection]);
    assert.ok(spy.withArgs('copy').calledOnce);
    spy.restore();
  });
});