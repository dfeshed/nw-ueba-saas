import Ember from 'ember';
import computed from 'ember-computed-decorators';

const { Component, set } = Ember;

export default Component.extend({
  classNames: 'rsa-context-panel__liveconnect',

  fixedYDomain: [0, 100], // This is to show 0-100 on the Y-axes, irrespective of max y-value in the data.

  @computed('liveConnectData.customerPercentageTrend')
  trendingCommunityActivity: (seenTrend) => [seenTrend],

  @computed('liveConnectData')
  trendingSubmissionActivity: (lcData) => ([
    lcData.customerHighRiskFeedbackPercentageTrend,
    lcData.customerRiskyFeedbackPercentageTrend,
    lcData.customerSuspiciousFeedbackPercentageTrend
  ]),

  @computed('liveConnectData.tags', 'allTags')
  riskIndicatorCategories: (riskIndicatorTags, allTags) => {
    // Collect tags to be highlighted
    const tagsToHighlight = riskIndicatorTags.reduce((hash, tag) => {
      hash[tag.value] = true;
      return hash;
    }, {});

    // Map all tags to respective fields identified by the category name
    const categories = {};
    if (allTags && allTags.length > 0) {
      allTags.forEach((tag) => {
        if (!categories.hasOwnProperty(tag.category)) {
          categories[tag.category] = {
            categoryText: tag.categoryText,
            tags: [] // array to hold all tags belonging to this category
          };
        }
        if (tagsToHighlight[tag.value]) {
          set(tag, 'highlight', true); // set highlight flag for indicated tags
          categories[tag.category].tags.unshift(tag); // If highlighted, add to the start
        } else {
          categories[tag.category].tags.push(tag);
        }
      });
    }

    // Finally return all values as array
    return Object.keys(categories).map((key) => categories[key]);
  }
});
