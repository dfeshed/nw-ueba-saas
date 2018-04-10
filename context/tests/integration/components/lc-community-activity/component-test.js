import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const liveConnectData = {
  'id': '1a708f247cc6a7364b873c029bbdf459',
  'firstSeen': 1488452350455,
  'risk': 'UNSAFE',
  'feedback': {
    'status': 'VIEWED',
    'dateMarked': 1488452350655
  },
  'riskReasonTypeList': [
    'BLACKLISTED_BY_ONE_OR_MORE_CUSTOMER',
    'ASSOCIATED_WITH_RISKY_DOMAIN'
  ],
  'customerPercentage': 34.24,
  'customerPercentageTrend': [
    {
      'time': new Date().getTime() - 10000,
      'percentage': 9.45
    },
    {
      'time': new Date().getTime(), // Only data points for past 30 days are rendered, hence it has to be the time when the test runs
      'percentage': 34.24
    }
  ],
  'customerRiskyFeedbackPercentage': 100.0,
  'customerRiskyFeedbackPercentageTrend': [
    {
      'time': new Date().getTime() - 10000,
      'percentage': 55.5
    },
    {
      'time': new Date().getTime(),
      'percentage': 45.5
    }
  ],
  'tags': [
    {
      'value': 'RECONNAISSANCE_SCANNING',
      'category': 'RECONNAISSANCE',
      'categoryText': 'RECONNAISSANCE',
      'name': 'SCANNING',
      'nameText': 'Scanning',
      'description': 'Some description'
    },
    {
      'value': 'RECONNAISSANCE_TOR',
      'category': 'RECONNAISSANCE',
      'categoryText': 'RECONNAISSANCE',
      'name': 'TOR',
      'nameText': 'Tor',
      'description': 'Some description'
    }
  ],
  'customerNotRiskyFeedbackPercentage': 0.0,
  'customerNotRiskyFeedbackPercentageTrend': [
    {
      'time': new Date().getTime() - 10000,
      'percentage': 9.45
    },
    {
      'time': new Date().getTime(),
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
      'time': new Date().getTime(),
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
      'time': new Date().getTime() - 10000,
      'percentage': 75.5
    },
    {
      'time': new Date().getTime(),
      'percentage': 64.5
    }
  ]
};
const allTags = [
  {
    'value': 'RECONNAISSANCE_SCANNING',
    'category': 'RECONNAISSANCE',
    'categoryText': 'RECONNAISSANCE',
    'name': 'SCANNING',
    'nameText': 'Scanning',
    'description': 'Some description'
  }
];
const allReasons = [
  {
    'value': 'STATIC_ANALYSIS',
    'description': 'Static analysis'
  }
];

moduleForComponent('rsa-context-panel/live-connect/lc-community-activity', 'Integration | Component | rsa context panel/lc community activity', {
  integration: true,
  beforeEach() {
    initialize(this);
  }
});

test('it renders correctly with the correct number of expected elements', function(assert) {
  this.set('liveConnectData', liveConnectData);
  this.set('allTags', allTags);
  this.set('allReasons', allReasons);
  this.render(hbs`{{context-panel/live-connect/lc-community-activity liveConnectData=liveConnectData allTags=allTags  allReasons=allReasons}}`);

  assert.equal(this.$('.rsa-context-panel__liveconnect__reviewstatus__col-1').length, 1, 'review status element exists.');
  assert.equal(this.$('.rsa-context-panel__liveconnect__comm-activity__desc').length, 4, 'Community Activity description element exists');
  assert.equal(this.$('.rsa-context-panel__liveconnect__comm-activity').length, 1, 'Community Activity element exists');
  assert.equal(this.$('.rsa-context-panel__liveconnect__heading').length, 4, 'Correct number of panel headers found');
  assert.equal(this.$('.rsa-content-datetime').length, 2, 'Correct number of rsa-content-datetime elements found');

});

test('line charts are rendered correctly', function(assert) {
  this.set('liveConnectData', liveConnectData);
  this.set('allTags', allTags);
  this.set('allReasons', allReasons);
  this.render(hbs`{{context-panel/live-connect/lc-community-activity liveConnectData=liveConnectData allTags=allTags allReasons=allReasons}}`);

  assert.equal(this.$('.rsa-chart').length, 2, 'Correct number of .rsa-chart  is present');
  assert.equal(this.$('.rsa-area-series').length, 4, 'Correct number of .rsa-area-series is present');
  assert.equal(this.$('.rsa-x-axis').length, 2, 'Correct number of .rsa-x-axis is present');
  assert.equal(this.$('.rsa-y-axis').length, 2, 'Correct number of .rsa-y-axis is present');
  assert.equal(this.$('.grids').length, 2, 'Correct number of .grids is present');
});
