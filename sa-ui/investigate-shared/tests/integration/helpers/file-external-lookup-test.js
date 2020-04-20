import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { externalLookup } from 'investigate-shared/utils/file-external-lookup';
import sinon from 'sinon';

module('Integration | Helper | file-external-lookup', function(hooks) {
  setupRenderingTest(hooks);
});
test('externalLookup test google', function(assert) {
  const action = [
    { title: 'File name', name: 'fileName', type: 'google' },
    { title: 'MD5', name: 'md5', type: 'google' },
    { title: 'SHA1', name: 'sha1', type: 'google' },
    { title: 'SHA256', name: 'sha256', type: 'google' }
  ];
  const selectedList = [{ fileName: 'abc', checksumMd5: 'abc', checksumSha1: 'abc', checksumSha256: 'abc' }];
  action.map((a) => {
    const result = externalLookup(a, selectedList);
    assert.equal(result, true, 'External google lookup should true');
  });
});
test('externalLookup test VirusTotal', function(assert) {
  const action = [
    { title: 'MD5', name: 'md5', type: 'VirusTotal' },
    { title: 'SHA1', name: 'sha1', type: 'VirusTotal' },
    { title: 'SHA256', name: 'sha256', type: 'VirusTotal' }
  ];
  const selectedList = [{ fileName: 'abc', checksumMd5: 'abc', checksumSha1: 'abc', checksumSha256: 'abc' }];
  action.map((a) => {
    const actionSpy = sinon.spy(window, 'open');
    const result = externalLookup(a, selectedList);
    assert.ok(actionSpy.calledOnce);
    assert.equal(actionSpy.args[0][0], 'https://www.virustotal.com/latest-scan/abc');
    assert.equal(actionSpy.args[0][1], '_blank');
    actionSpy.resetHistory();
    actionSpy.restore();
    assert.equal(result, true, 'External virusTotal lookup should true');
  });
});