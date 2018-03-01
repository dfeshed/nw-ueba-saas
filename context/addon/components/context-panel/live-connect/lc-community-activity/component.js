import Component from '@ember/component';
import { set } from '@ember/object';
import { isEmpty } from '@ember/utils';
import computed from 'ember-computed-decorators';
import ContextHelper from 'context/util/util';
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

  fixedYDomain: [0, 100], // This is to show 0-100 on the Y-axes, irrespective of max y-value in the data.

  @computed('liveConnectData.customerPercentageTrend')
  trendingCommunityActivity: (seenTrend) => [ContextHelper.filterLast30Days(seenTrend)],

  @computed('liveConnectData')
  trendingSubmissionActivity: (lcData) => ([
    ContextHelper.filterLast30Days(lcData ? lcData.customerHighRiskFeedbackPercentageTrend : []),
    ContextHelper.filterLast30Days(lcData ? lcData.customerRiskyFeedbackPercentageTrend : []),
    ContextHelper.filterLast30Days(lcData ? lcData.customerSuspiciousFeedbackPercentageTrend : [])
  ]),

  @computed('trendingSubmissionActivity')
  showSubmissionTrend: (submissionTrend) => {
    return submissionTrend.any((arr) => {
      return !isEmpty(arr);
    });
  },

  @computed('liveConnectData.tags', 'allTags')
  riskIndicatorCategories: (riskIndicatorTags, allTags) => {
    // Collect tags to be highlighted
    const tagsToHighlight = (riskIndicatorTags || []).reduce((hash, tag) => {
      hash[tag] = true;
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
  },

  @computed('liveConnectData.riskReasonTypeList', 'allReasons')
  riskReasonList: (riskReasons, allReasons) => {
    const riskReasonMap = allReasons.reduce((hash, reason) => {
      hash[reason.value] = reason.description;
      return hash;
    }, {});

    const reasonsToDisplay = (riskReasons || []).map((reason) => {
      return riskReasonMap[reason];
    });
    return reasonsToDisplay;
  },

  @computed('liveConnectData.risk')
  riskDescription: (risk) => {
    return riskTemplate[risk].desc;
  },

  @computed('liveConnectData.risk')
  riskBadgeStyle: (risk) => {
    return riskTemplate[risk].style;
  }
});
