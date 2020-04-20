import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | context-panel/live-connect/lc-certificate-info', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {

    const liveConnectFileData = {
      'certThumbprint': 'test-thumbprint',
      'certSubject': 'test-subject',
      'certSerial': 'test-serial',
      'certSigAlgo': 'test-algo',
      'certIssuer': 'test-issuer'
    };

    this.set('liveConnectData', liveConnectFileData);

    await render(hbs`{{context-panel/live-connect/lc-certificate-info liveConnectData=liveConnectData}}`);

    assert.equal(findAll('.rsa-content-definition').length, 7, 'Correct number of rsa-content-definition elements found');

  });
});
