import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | context-panel/live-connect/lc-domain-identity', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {

    const liveConnectData = {
      'id': 'test-id',
      'ip': '10.10.10.10'
    };

    this.set('liveConnectData', liveConnectData);

    await render(hbs`{{context-panel/live-connect/lc-domain-identity liveConnectData=liveConnectData}}`);

    assert.equal(findAll('.rsa-content-definition').length, 2, 'Correct number of rsa-content-definition elements found');

  });
});
