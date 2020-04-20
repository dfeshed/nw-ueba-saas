import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | context-panel/live-connect/lc-file-identity', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {

    const liveConnectData = {
      'id': 'test-id',
      'fileName': 'test.exe',
      'sha1': 'test-sha1',
      'sha256': 'test-sha256',
      'filesize': '20',
      'mimeType': 'exe'
    };

    this.set('liveConnectData', liveConnectData);

    await render(hbs`{{context-panel/live-connect/lc-file-identity liveConnectData=liveConnectData}}`);

    assert.equal(findAll('.rsa-content-definition').length, 7, 'Correct number of rsa-content-definition elements found');

  });
});
