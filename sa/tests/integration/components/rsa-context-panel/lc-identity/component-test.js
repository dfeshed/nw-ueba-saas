import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-context-panel/lc-identity', 'Integration | Component | rsa context panel/lc identity', {
  integration: true
});

test('it renders', function(assert) {
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
  this.render(hbs`{{rsa-context-panel/lc-identity liveConnectData=liveConnectData}}`);
  assert.equal(this.$('.rsa-content-definition').length, 4, 'Correct number of rsa-content-definition elements found');


});
