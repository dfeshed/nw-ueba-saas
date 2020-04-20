import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | context-panel/live-connect/lc-domain-whois', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {

    const liveConnectData = {
      'whoisRegType': 'test-regtype',
      'whoisRegName': 'test-regname',
      'whoisRegOrg': 'test-regorg',
      'whoisRegStreet': 'test-regstreet',
      'whoisRegCity': 'test-regcity',
      'whoisRegState': 'test-regstate',
      'whoisPostalCode': 'test-postalcode',
      'whoisCountry': 'test-country',
      'whoisPhone': 'test-phone',
      'whoisFax': 'test-fax',
      'whoisEmail': 'test-email'
    };

    this.set('liveConnectData', liveConnectData);

    await render(hbs`{{context-panel/live-connect/lc-domain-whois liveConnectData=liveConnectData}}`);

    assert.equal(findAll('.rsa-content-definition').length, 14, 'Correct number of rsa-content-definition elements found');

  });
});
