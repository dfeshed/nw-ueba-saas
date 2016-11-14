import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
const liveConnectData = {
  'id': '1a708f247cc6a7364b873c029bbdf459',
  'firstSeen': 1476077742000,
  'risk': 'UNSAFE',
  'feedback': {
    'status': 'VIEWED',
    'dateMarked': 1477388194946
  },
  'riskReasonTypeList': [
    'BLACKLISTED_BY_ONE_OR_MORE_CUSTOMER',
    'ASSOCIATED_WITH_RISKY_DOMAIN'
  ],
  'customerPercentage': 34.24,
  'customerPercentageTrend': [
    {
      'time': 1401926400000,
      'percentage': 9.45
    },
    {
      'time': 1445731200000,
      'percentage': 34.24
    }
  ],
  'customerRiskyFeedbackPercentage': 100.0,
  'customerRiskyFeedbackPercentageTrend': [
    {
      'time': 1401926400000,
      'percentage': 55.5
    },
    {
      'time': 1445731200000,
      'percentage': 45.5
    }
  ],
  'customerNotRiskyFeedbackPercentage': 0.0,
  'customerNotRiskyFeedbackPercentageTrend': [
    {
      'time': 1401926400000,
      'percentage': 9.45
    },
    {
      'time': 1445731200000,
      'percentage': 34.24
    }
  ],
  'customerInvestigatedPercentage': 9.45,
  'customerInvestigatedPercentageTrend': [
    {
      'time': 1474934400000,
      'percentage': 9.45
    }
  ],
  'customerUnknownFeedbackPercentage': 0.0,
  'customerUnknownFeedbackPercentageTrend': [
    {
      'time': 1401926400000,
      'percentage': 9.45
    },
    {
      'time': 1445731200000,
      'percentage': 34.24
    }
  ],
  'customerSuspiciousFeedbackPercentage': 0.0,
  'customerSuspiciousFeedbackPercentageTrend': [
    {
      'time': 1401926400000,
      'percentage': 20.5
    },
    {
      'time': 1445731200000,
      'percentage': 34.24
    }
  ],
  'customerHighRiskFeedbackPercentage': 0.0,
  'customerHighRiskFeedbackPercentageTrend': [
    {
      'time': 1401926400000,
      'percentage': 75.5
    },
    {
      'time': 1445731200000,
      'percentage': 64.5
    }
  ]
};
moduleForComponent('rsa-context-panel/lc-community-activity', 'Integration | Component | rsa context panel/lc community activity', {
  integration: true
});

test('it renders correctly with the correct number of expected elements', function(assert) {
  this.set('liveConnectData', liveConnectData);
  this.render(hbs`{{rsa-context-panel/lc-community-activity liveConnectData=liveConnectData}}`);

  assert.equal(this.$('.rsa-context-panel__liveconnect__reviewstatus__col-1').length, 1, 'review status element exists.');
  assert.equal(this.$('.rsa-form-radio-group-label').length, 2, 'Radio group label elements present');
  assert.equal(this.$('.rsa-context-panel__liveconnect__comm-activity__desc').length, 4, 'Community Activity description element exists');
  assert.equal(this.$('.rsa-context-panel__liveconnect__comm-activity').length, 1, 'Community Activity element exists');
  // assert.equal(this.$('.rsa-context-panel__liveconnect__risk-indicators').length, 1, 'Risk Indicators element exists');
  assert.equal(this.$('.rsa-content-section-header').length, 2, 'Correct number of section header elements found');
  assert.equal(this.$('.rsa-content-datetime').length, 2, 'Correct number of rsa-content-datetime elements found');

});

test('Radio button,Button and Drop down renders correctly', function(assert) {
  this.set('liveConnectData', liveConnectData);
  this.render(hbs`{{rsa-context-panel/lc-community-activity liveConnectData=liveConnectData}}`);

  assert.equal(this.$('.rsa-form-radio').length, 6, 'Correct number of Radio group inputs is present');

  assert.equal(this.$('.rsa-form-select').length, 2, 'Correct number of Drop downs is present');

  assert.equal(this.$('.rsa-form-button').length, 1, 'Submit button exists');

});

test('line charts are rendered correctly', function(assert) {
  this.set('liveConnectData', liveConnectData);
  this.render(hbs`{{rsa-context-panel/lc-community-activity liveConnectData=liveConnectData}}`);

  assert.equal(this.$('.rsa-chart').length, 2, 'Correct number of .rsa-chart  is present');
  assert.equal(this.$('.rsa-area-series').length, 4, 'Correct number of .rsa-area-series is present');
  assert.equal(this.$('.rsa-x-axis').length, 2, 'Correct number of .rsa-x-axis is present');
  assert.equal(this.$('.rsa-y-axis').length, 2, 'Correct number of .rsa-y-axis is present');
  assert.equal(this.$('.grids').length, 2, 'Correct number of .grids is present');
});