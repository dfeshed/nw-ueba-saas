import Ember from 'ember';
import layout from './template';
import computed from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';
import riskLevels from './risk-levels';
import confidenceLevels from './confidence-levels';

const {
  inject: {
    service
  },
  Component,
  isEmpty,
  Logger
} = Ember;

const stateToComputed = ({ context }) => ({
  activeTabName: context.activeTabName
});

const FeedbackComponent = Component.extend({
  layout,
  riskLevels,
  confidenceLevels,

  flashMessages: service(),
  request: service(),

  selectedRiskLevel: null,
  selectedConfidenceLevel: null,
  selectedTags: [],

  riskLevelTriggerClass: null,
  confidenceLevelTriggerClass: null,

  inProgress: false,

  skillLevels: [1, 2, 3],

  init() {
    this._super(...arguments);
    this._fetchSkillLevel();
  },

  @computed('activeTabName', 'model.contextData.liveConnectData')
  showFeedbackPanel: (activeTabName, lcData) => activeTabName === 'LiveConnect-Ip' && lcData,

  @computed('model.contextData.liveConnectData.allTags')
  riskTags: (tags) => {
    const groups = {};
    if (!isEmpty(tags)) {
      tags.forEach((tag) => {
        if (!groups.hasOwnProperty(tag.category)) {
          groups[tag.category] = {
            groupName: tag.categoryText,
            options: [] // array to hold all tags belonging to this category
          };
        }
        groups[tag.category].options.push(tag);
      });
    }
    return Object.keys(groups).map((key) => groups[key]);
  },

  actions: {
    setSelectedRiskLevel(option) {
      this.set('selectedRiskLevel', option);
      this.set('riskLevelTriggerClass', null);
    },

    setSelectedConfidenceLevel(option) {
      this.set('selectedConfidenceLevel', option);
      this.set('confidenceLevelTriggerClass', null);
    },

    setSelectedTags(tags) {
      this.set('selectedTags', tags);
    },

    submit() {
      if (!this._validateForm()) {
        const message = this.get('i18n').t('context.lc.feedbackFormInvalid');
        this.get('flashMessages').error(message);
        return;
      }
      const params = this._getParams();
      this._sendFeedbackRequest(params);
    }
  },

  _validateForm() {
    let success = true;
    if (!this.get('selectedRiskLevel')) {
      this.set('riskLevelTriggerClass', 'is-error');
      success = false;
    }

    if (!this.get('selectedConfidenceLevel')) {
      this.set('confidenceLevelTriggerClass', 'is-error');
      success = false;
    }
    return success;
  },

  _getParams() {
    const tagValues = (this.get('selectedTags') || []).map((tag) => tag.value);
    return {
      source: 'NW-UI',
      meta: this.get('model.meta'),
      metaValue: this.get('model.lookupKey'),
      feedback: this.get('selectedRiskLevel').value,
      confidenceType: this.get('selectedConfidenceLevel').value,
      riskTagTypes: tagValues,
      skillLevel: this.get('selectedSkillLevel')
    };
  },

  _sendFeedbackRequest(params) {
    this.set('inProgress', true);
    this.get('request').promiseRequest({
      method: 'createRecord',
      modelName: 'liveconnect-feedback',
      query: params,
      streamOptions: { requireRequestId: false }
    }).then(() => {
      Logger.debug('Submitted feedback to LC successfully');
      const message = this.get('i18n').t('context.lc.feedbackSubmitted');
      this.get('flashMessages').success(message);
    }).catch((reason) => {
      const errorMsg = reason.meta ? reason.meta.message : '';
      Logger.error(`Could not submit feedback: ${errorMsg}`);
      const message = this.get('i18n').t('context.lc.feedbackSubmissionFailed');
      this.get('flashMessages').error(message);
    }).finally(() => {
      this.set('inProgress', false);
    });
  },

  _fetchSkillLevel() {
    this.get('request').promiseRequest({
      method: 'queryRecord',
      modelName: 'skill-level',
      query: {}
    }).then(({ data }) => {
      Logger.debug(`Fetched skill level ${data.skillLevel}`);
      this.set('selectedSkillLevel', data.skillLevel || 1);
    }).catch((reason) => {
      Logger.error(reason);
      this.set('selectedSkillLevel', 1); // default level
    });
  }
});

export default connect(stateToComputed)(FeedbackComponent);
