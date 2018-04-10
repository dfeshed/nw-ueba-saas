import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { isEmpty } from '@ember/utils';
import layout from './template';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import riskLevels from './risk-levels';
import confidenceLevels from './confidence-levels';
import { debug, warn } from '@ember/debug';

const stateToComputed = ({ context: { tabs: { activeTabName } } }) => ({
  activeTabName
});

const liveConnectTabs = ['LiveConnect-Ip', 'LiveConnect-Domain', 'LiveConnect-File'];

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
  showFeedbackPanel: (activeTabName, lcData) => liveConnectTabs.includes(activeTabName) && lcData,

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
    const pojo = this.getProperties('model.lookupKey', 'model.meta', 'selectedRiskLevel', 'selectedConfidenceLevel', 'selectedSkillLevel');
    const metaType = pojo['model.meta'] === 'FILE_HASH' ? 'FILE' : pojo['model.meta'];
    return {
      source: 'NW-UI',
      meta: metaType,
      metaValue: pojo['model.lookupKey'],
      feedback: pojo.selectedRiskLevel.value,
      confidenceType: pojo.selectedConfidenceLevel.value,
      riskTagTypes: tagValues,
      skillLevel: pojo.selectedSkillLevel
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
      debug('Submitted feedback to LC successfully');
      const message = this.get('i18n').t('context.lc.feedbackSubmitted');
      this.get('flashMessages').success(message);
    }).catch((reason) => {
      const errorMsg = reason.meta ? reason.meta.message : '';
      warn(`Could not submit feedback: ${errorMsg}`, { id: 'context.components.context-panel.live-connect.feedback' });
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
      debug(`Fetched skill level ${data.skillLevel}`);
      if (this.isDestroyed || this.isDestroying) {
        return;
      }
      this.set('selectedSkillLevel', data.skillLevel || 1);
    }).catch((reason) => {
      warn(reason, { id: 'context.components.context-panel.live-connect.feedback' });
      if (this.isDestroyed || this.isDestroying) {
        return;
      }
      this.set('selectedSkillLevel', 1); // default level
    });
  }
});

export default connect(stateToComputed)(FeedbackComponent);
