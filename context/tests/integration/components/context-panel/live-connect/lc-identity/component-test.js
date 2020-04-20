import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | context-panel/live-connect/lc-identity', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    const liveConnectData = {
      'customerRiskyFeedbackPercentageTrend': [],
      'customerPercentageTrend': [],
      'feedback': {},
      'customerInvestigatedPercentageTrend': [],
      'customerPercentage': 10.0,
      'customerInvestigatedPercentage': 20.0,
      'risk': null,
      'unsafeModulesDownloaded': 'NONE',
      'riskReasonTypeList': [],
      'customerRiskyFeedbackPercentage': 23.0,
      'customerNotRiskyFeedbackPercentage': 4.0,
      'unsafeModulesCommunicated': [],
      'relatedDomains': [],
      'customerNotRiskyFeedbackPercentageTrend': [],
      'riskScore': 31,
      'id': '12.31.23.45',
      'firstSeen': 1452485774539,
      'asn': 'AS12257',
      'prefix': 'AS',
      'countryCode': '01772',
      'country': 'US',
      'registrant': 'XYZ/LLC',
      'fileDate': 1401972584000
    };
    this.set('liveConnectData', liveConnectData);
    await render(hbs`{{context-panel/live-connect/lc-identity liveConnectData=liveConnectData}}`);
    assert.equal(findAll('.rsa-content-definition').length, 4, 'Correct number of rsa-content-definition elements found');

  });

});
