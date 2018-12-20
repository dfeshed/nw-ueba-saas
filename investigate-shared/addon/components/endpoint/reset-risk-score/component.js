import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { success, failure, warning } from 'investigate-shared/utils/flash-messages';

export default Component.extend({
  layout,

  classNames: ['reset-risk-score'],

  showResetScoreModal: false,

  buttonType: 'button',

  selectedList: [],

  riskType: 'HOST',

  resetRiskScore: null,

  @computed('selectedList')
  isMaxResetRiskScoreLimit(selectedList) {
    return selectedList.length > 1;
  },

  @computed('selectedList')
  disableButton(selectedList) {
    return !(selectedList.length > 0);
  },

  actions: {
    onResetAction() {
      this.set('showResetScoreModal', true);
    },

    onResetScoreModalClose() {
      this.set('showResetScoreModal', false);
    },

    handleResetHostsRiskScore() {
      const limitedList = this.get('selectedList').slice(0, 100);
      const callBackOptions = {
        onSuccess: (response) => {
          const { data } = response;
          if (data === limitedList.length) {
            success('investigateHosts.hosts.resetHosts.success');
          } else {
            warning('investigateHosts.hosts.resetHosts.warning');
          }
        },
        onFailure: () => failure('investigateHosts.hosts.resetHosts.error')
      };
      this.resetRiskScore(limitedList, this.get('riskType'), callBackOptions);
      this.set('showResetScoreModal', false);
    }
  }
});
