/* eslint-disable new-cap */
import { nonSupportedActionList, nonUrlBasedActions } from 'rsa-context-menu/utils/non-url-actions-handler';
import { module, test } from 'qunit';
import sinon from 'sinon';
import windowProxy from 'component-lib/utils/window-proxy';

const selection = {
  'moduleName': 'EventAnalysisPanel',
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
let openStub = null;
let locationStub = null;
let currentUrl = null;
let newTab = false;
module('Unit | Utility | non-url-actions-handler', function(hooks) {

  hooks.beforeEach(function() {
    openStub = sinon.stub(windowProxy, 'openInNewTab').callsFake((urlPassed) => {
      currentUrl = urlPassed;
      newTab = true;
    });
    locationStub = sinon.stub(windowProxy, 'openInCurrentTab').callsFake((urlPassed) => {
      currentUrl = urlPassed;
      newTab = false;
    });
  });
  hooks.afterEach(function() {
    openStub.restore();
    locationStub.restore();
  });

  test('this should return non supported actions list and common actions support', function(assert) {
    assert.equal(nonSupportedActionList.length, 11, `Currently following actions are not supported : ${nonSupportedActionList.concat(',')}`);
  });

  test('test nonUrlBasedActions for drillDownNewTabEquals', function(assert) {
    const url = '/investigation/endpointid/555d9a6fe4b0d37c827d402e/navigate/query/ip.src%2520%253D%252017.127.255.150';
    nonUrlBasedActions.drillDownNewTabEquals([selection], contextDetails);
    assert.ok(currentUrl.indexOf(url) > -1);
    assert.ok(newTab);
  });

  test('test nonUrlBasedActions for InvestigationEventDrillDownNotEquals', function(assert) {
    const url = 'mf=ip.src%2520!%253D%252017.127.255.150';
    nonUrlBasedActions.InvestigationEventDrillDownNotEquals([selection], contextDetails);
    assert.ok(currentUrl.indexOf(url) > -1);
    assert.notOk(newTab);
  });

  test('test nonUrlBasedActions for InvestigationEventDrillDownNotEqualsNewTab', function(assert) {
    const url = 'mf=ip.src%2520!%253D%252017.127.255.150';
    nonUrlBasedActions.InvestigationEventDrillDownNotEqualsNewTab([selection], contextDetails);
    assert.ok(currentUrl.indexOf(url) > -1);
    assert.ok(newTab);
  });

  test('test nonUrlBasedActions for InvestigationEventDrillDownEquals', function(assert) {
    const url = 'mf=ip.src%2520%253D%252017.127.255.150';
    nonUrlBasedActions.InvestigationEventDrillDownEquals([selection], contextDetails);
    assert.ok(currentUrl.indexOf(url) > -1);
    assert.ok(newTab);
  });

  test('test nonUrlBasedActions for InvestigationEventDrillDownContainsNewTab', function(assert) {
    const url = 'mf=ip.src%2520contains%252017.127.255.150';
    nonUrlBasedActions.InvestigationEventDrillDownContainsNewTab([selection], contextDetails);
    assert.ok(currentUrl.indexOf(url) > -1);
    assert.ok(newTab);
  });

  test('test nonUrlBasedActions for InvestigationEventDrillDownContains', function(assert) {
    const url = 'mf=ip.src%2520contains%252017.127.255.150';
    nonUrlBasedActions.InvestigationEventDrillDownContains([selection], contextDetails);
    assert.ok(currentUrl.indexOf(url) > -1);
    assert.notOk(newTab);
  });

  test('test nonUrlBasedActions for InvestigationEventRefocusEquals', function(assert) {
    const url = 'mf=ip.src%2520%253D%252017.127.255.150';
    nonUrlBasedActions.InvestigationEventRefocusEquals([selection], contextDetails);
    assert.ok(currentUrl.indexOf(url) > -1);
    assert.notOk(newTab);
  });

  test('test nonUrlBasedActions for InvestigationEventRefocusNotEquals', function(assert) {
    const url = 'mf=ip.src%2520!%253D%252017.127.255.150';
    nonUrlBasedActions.InvestigationEventRefocusNotEquals([selection], contextDetails);
    assert.ok(currentUrl.indexOf(url) > -1);
    assert.notOk(newTab);
  });

  test('test nonUrlBasedActions for InvestigationEventRefocusContains', function(assert) {
    // Need to check why this is undefined.
    const url = 'mf=undefined%2520contains%2520undefined';
    nonUrlBasedActions.InvestigationEventRefocusContains([selection], contextDetails);
    assert.ok(currentUrl.indexOf(url) > -1);
    assert.notOk(newTab);
  });

  test('test nonUrlBasedActions for InvestigationEventRefocusNewTabEquals', function(assert) {
    const url = 'mf=ip.src%2520%253D%252017.127.255.150';
    nonUrlBasedActions.InvestigationEventRefocusNewTabEquals([selection], contextDetails);
    assert.ok(currentUrl.indexOf(url) > -1);
    assert.ok(newTab);
  });

  test('test nonUrlBasedActions for InvestigationEventRefocusNewTabNotEqualsNewTab', function(assert) {
    const url = 'mf=ip.src%2520!%253D%252017.127.255.150';
    nonUrlBasedActions.InvestigationEventRefocusNewTabNotEqualsNewTab([selection], contextDetails);
    assert.ok(currentUrl.indexOf(url) > -1);
    assert.ok(newTab);
  });

  test('test nonUrlBasedActions for InvestigationEventRefocusNewTabContainsNewTab', function(assert) {
    const url = 'mf=ip.src%2520contains%252017.127.255.150';
    nonUrlBasedActions.InvestigationEventRefocusNewTabContainsNewTab([selection], contextDetails);
    assert.ok(currentUrl.indexOf(url) > -1);
    assert.ok(newTab);
  });

  test('test nonUrlBasedActions for copyToClipboard', function(assert) {
    const spy = sinon.spy(document, 'execCommand');
    nonUrlBasedActions.copyMetaAction([selection]);
    assert.ok(spy.withArgs('copy').calledOnce);
    spy.restore();
  });
});