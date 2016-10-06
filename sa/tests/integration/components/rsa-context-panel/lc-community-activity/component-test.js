import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-context-panel/lc-community-activity', 'Integration | Component | rsa context panel/lc community activity', {
  integration: true
});

test('it renders correctly with the correct number of expected elements', function(assert) {
  let liveConnectData = {
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
    'firstSeen': 1452485774539
  };
  this.set('liveConnectData', liveConnectData);
  this.render(hbs`{{rsa-context-panel/lc-community-activity liveConnectData=liveConnectData}}`);

  assert.equal(this.$('.rsa-context-panel__liveconnect__reviewstatus__col-1').length, 1, 'review status element exists.');
  assert.equal(this.$('.rsa-form-radio-group-label').length, 2, 'Radio group label elements present');
  assert.equal(this.$('.rsa-context-panel__liveconnect__comm-activity__desc').length, 2, 'Community Activity description element exists');
  assert.equal(this.$('.rsa-context-panel__liveconnect__comm-activity').length, 1, 'Community Activity element exists');
  // assert.equal(this.$('.rsa-context-panel__liveconnect__risk-indicators').length, 1, 'Risk Indicators element exists');
  assert.equal(this.$('.rsa-content-section-header').length, 2, 'Correct number of section header elements found');
  assert.equal(this.$('.rsa-content-datetime').length, 2, 'Correct number of rsa-content-datetime elements found');

});

test('Radio button,Button and Drop down renders correctly', function(assert) {

  let liveConnectData = {
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
    'firstSeen': 1452485774539
  };
  this.set('liveConnectData', liveConnectData);
  this.render(hbs`{{rsa-context-panel/lc-community-activity liveConnectData=liveConnectData}}`);

  assert.equal(this.$('.rsa-form-radio').length, 6, 'Correct number of Radio group inputs is present');

  assert.equal(this.$('.rsa-form-select').length, 2, 'Correct number of Drop downs is present');

  assert.equal(this.$('.rsa-form-button').length, 1, 'Submit button exists');

});

test('line charts are rendered correctly', function(assert) {
  let liveConnectData = {
    'customerRiskyFeedbackPercentageTrend': [{
      percentage: 3.62,
      time: 1470182400000
    },
      {
        percentage: 28.22,
        time: 1471910400000
      },
      {
        percentage: 39.22,
        time: 1472910400000
      },
      {
        percentage: 58.22,
        time: 1473910400000
      },
      {
        percentage: 78.22,
        time: 1474910400000
      }],
    'customerPercentageTrend': [{
      percentage: 10.45,
      time: 1468972800000
    },
      {
        percentage: 25.67,
        time: 1469404800000
      },
      {
        percentage: 33.81,
        time: 1469491200000
      },
      {
        percentage: 53.28,
        time: 1469664000000
      },
      {
        percentage: 80.61,
        time: 1470355200000
      },
      {
        percentage: 96.81,
        time: 1471910400000
      }],
    'feedback': {},
    'customerInvestigatedPercentageTrend': [{
      percentage: 30.06,
      time: 1469491200000
    },
      {
        percentage: 15.0,
        time: 1469577600000
      },
      {
        percentage: 43.65,
        time: 1470787200000
      },
      {
        percentage: 30.0,
        time: 1471219200000
      },
      {
        percentage: 48.69,
        time: 1472169600000
      }],
    'customerPercentage': 10.0,
    'customerInvestigatedPercentage': 20.0,
    'risk': null,
    'unsafeModulesDownloaded': 'NONE',
    'riskReasonTypeList': [],
    'customerRiskyFeedbackPercentage': 23.0,
    'customerNotRiskyFeedbackPercentage': 4.0,
    'unsafeModulesCommunicated': [],
    'relatedDomains': [],
    'customerNotRiskyFeedbackPercentageTrend': [{
      percentage: 73.62,
      time: 1470182400000
    },
      {
        percentage: 63.22,
        time: 1471910400000
      },
      {
        percentage: 18.22,
        time: 1472910400000
      },
      {
        percentage: 28.22,
        time: 1473910400000
      },
      {
        percentage: 10.22,
        time: 1474910400000
      }],
    'riskScore': 31,
    'id': '12.31.23.45',
    'firstSeen': 1452485774539
  };
  this.set('liveConnectData', liveConnectData);
  this.render(hbs`{{rsa-context-panel/lc-community-activity liveConnectData=liveConnectData}}`);

  assert.equal(this.$('.rsa-chart').length, 2, 'Correct number of .rsa-chart  is present');
  assert.equal(this.$('.rsa-area-series').length, 3, 'Correct number of .rsa-area-series is present');
  assert.equal(this.$('.rsa-x-axis').length, 2, 'Correct number of .rsa-x-axis is present');
  assert.equal(this.$('.rsa-y-axis').length, 2, 'Correct number of .rsa-y-axis is present');
  assert.equal(this.$('.grids').length, 2, 'Correct number of .grids is present');
});