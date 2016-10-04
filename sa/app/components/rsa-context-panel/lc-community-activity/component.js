import Ember from 'ember';
import computed from 'ember-computed-decorators';

const {
    Component
} = Ember;

export default Component.extend({
  classNames: 'rsa-context-panel__liveconnect',

  fixedYDomain: [0,100], // This is to show 0-100 on the Y-axes, irrespective of max y-value in the data.

  @computed('liveConnectData.customerPercentageTrend')
  trendingCommunityActivity: (seenTrend) => [seenTrend],

  @computed('liveConnectData')
  trendingSubmissionActivity: (lcData) => [lcData.customerRiskyFeedbackPercentageTrend, lcData.customerNotRiskyFeedbackPercentageTrend]
});
