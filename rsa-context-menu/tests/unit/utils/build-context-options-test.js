import { buildContextOptions } from 'rsa-context-menu/utils/build-context-options';
import data from '../../helpers/actions-data';
import { module, test } from 'qunit';
import windowProxy from 'rsa-context-menu/utils/window-proxy';
import sinon from 'sinon';

const result = buildContextOptions(data.data, {
  exists: () => true,
  t: (str) => {
    return str;
  }
});
const selection = {
  'moduleName': 'EventAnalysisPanel',
  'metaName': 'ip.src',
  'metaValue': '17.127.255.150'
};

module('Unit | Utility | build-context-options', function() {

  test('test parse result based on data', function(assert) {
    assert.ok(result);
    assert.equal(result.EventAnalysisPanel['ip.src'].length, 3, 'Should retrun only 2 actions');
    assert.notOk(result.EventAnalysisPanel.test, 'Should not be having any actions');
  });
  test('test action should open url in new tab', function(assert) {
    const spy = sinon.spy(windowProxy, 'openInNewTab');
    const ipActions = result.EventAnalysisPanel['ip.src'].find((action) => action.label === 'contextmenu.actions.applyRefocusSessionSplitsInNewTabLabelNew');
    ipActions.action([selection]);
    assert.ok(spy.calledOnce);
  });

  test('test action should open url in current tab', function(assert) {
    let currentUrl = null;
    let newTab = true;
    const locationStub = sinon.stub(windowProxy, 'openInCurrentTab', (urlPassed) => {
      currentUrl = urlPassed;
      newTab = false;
    });
    const ipActions = result.EventAnalysisPanel['ip.src'].find((action) => action.label === 'contextmenu.actions.nw-event-value-drillable-contains');
    ipActions.action([selection]);
    assert.equal(currentUrl, 'http://www.google.com/search?q=17.127.255.150');
    assert.notOk(newTab);
    locationStub.restore();
  });
});