import windowProxy from 'component-lib/utils/window-proxy';
import { module, test } from 'qunit';
import sinon from 'sinon';

module('Unit | Utility | window-proxy', function() {

  test('window proxy have open and change properties', function(assert) {
    assert.ok(windowProxy.openInNewTab);
    assert.ok(windowProxy.openInCurrentTab);
    assert.ok(typeof windowProxy.openInNewTab === 'function');
    assert.ok(typeof windowProxy.openInCurrentTab === 'function');
  });
  test('window proxy openInNewTab is calling window.open function', function(assert) {
    const spy = sinon.spy(window, 'open');
    const testUrl = 'www.google.com';
    windowProxy.openInNewTab(testUrl);
    assert.ok(spy.withArgs(testUrl));
    spy.restore();
  });
  test('test window proxy openInCurrentTab', function(assert) {
    const testUrl = 'www.google.com';
    const stub = sinon.stub(window, 'open').callsFake((url, windowName) => {
      assert.equal(windowName, '_self', 'this should open in current window.');
      assert.equal(testUrl, url, 'this should open correct url.');
    });
    windowProxy.openInCurrentTab(testUrl);
    assert.ok(stub.withArgs(testUrl));
    stub.restore();
  });
});
