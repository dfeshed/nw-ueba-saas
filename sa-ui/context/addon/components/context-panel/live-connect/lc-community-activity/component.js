import { computed } from '@ember/object';
import Component from '@ember/component';
import { isEmpty } from '@ember/utils';
import ContextHelper from 'context/utils/util';
import layout from './template';

const riskTemplate = {
  'HIGH RISK': {
    desc: 'context.lc.highRiskDesc',
    style: 'rsa-context-panel__liveconnect__risk-badge__high-risk'
  },
  UNSAFE: {
    desc: 'context.lc.unsafeRiskDesc',
    style: 'rsa-context-panel__liveconnect__risk-badge__unsafe'
  },
  SUSPICIOUS: {
    desc: 'context.lc.suspiciousRiskDesc',
    style: 'rsa-context-panel__liveconnect__risk-badge__suspicious'
  },
  SAFE: {
    desc: 'context.lc.safeRiskDesc',
    style: 'rsa-context-panel__liveconnect__risk-badge__safe'
  },
  UNKNOWN: {
    desc: 'context.lc.unknownRiskDesc',
    style: 'rsa-context-panel__liveconnect__risk-badge__unknown'
  }
};

export default Component.extend({
  layout,
  classNames: 'rsa-context-panel__liveconnect',

  init() {
    this._super(...arguments);
    // This is to show 0-100 on the Y-axes, irrespective of max y-value in the data.
    this.fixedYDomain = this.fixedYDomain || [0, 100];
  },

  trendingCommunityActivity: computed('liveConnectData.customerPercentageTrend', function() {
    return [ContextHelper.filterLast30Days(this.liveConnectData?.customerPercentageTrend)];
  }),

  trendingSubmissionActivity: computed('liveConnectData', function() {
    return [
      ContextHelper.filterLast30Days(this.liveConnectData ? this.liveConnectData.customerHighRiskFeedbackPercentageTrend : []),
      ContextHelper.filterLast30Days(this.liveConnectData ? this.liveConnectData.customerRiskyFeedbackPercentageTrend : []),
      ContextHelper.filterLast30Days(this.liveConnectData ? this.liveConnectData.customerSuspiciousFeedbackPercentageTrend : [])
    ];
  }),

  showSubmissionTrend: computed('trendingSubmissionActivity', function() {
    return this.trendingSubmissionActivity.any((arr) => {
      return !isEmpty(arr);
    });
  }),

  riskIndicatorCategories: computed('liveConnectData.tags', 'allTags', function() {
    // Collect tags to be highlighted
    const tagsToHighlight = (this.liveConnectData?.tags || []).reduce((hash, tag) => {
      hash[tag] = true;
      return hash;
    }, {});

    // Map all tags to respective fields identified by the category name
    const categories = {};
    if (this.allTags && this.allTags.length > 0) {
      this.allTags.forEach((tag) => {
        if (!categories.hasOwnProperty(tag.category)) {
          categories[tag.category] = {
            categoryText: tag.categoryText,
            tags: [] // array to hold all tags belonging to this category
          };
        }
        if (tagsToHighlight[tag.value]) {
          tag.set('highlight', true); // set highlight flag for indicated tags
          categories[tag.category].tags.unshift(tag); // If highlighted, add to the start
        } else {
          categories[tag.category].tags.push(tag);
        }
      });
    }

    // Finally return all values as array
    return Object.keys(categories).map((key) => categories[key]);
  }),

  riskReasonList: computed('liveConnectData.riskReasonTypeList', 'allReasons', function() {
    const riskReasonMap = this.allReasons.reduce((hash, reason) => {
      hash[reason.value] = reason.description;
      return hash;
    }, {});

    const reasonsToDisplay = (this.liveConnectData?.riskReasonTypeList || []).map((reason) => {
      return riskReasonMap[reason];
    });
    return reasonsToDisplay;
  }),

  riskDescription: computed('liveConnectData.risk', function() {
    return riskTemplate[this.liveConnectData?.risk].desc;
  }),

  riskBadgeStyle: computed('liveConnectData.risk', function() {
    return riskTemplate[this.liveConnectData?.risk].style;
  })
});
