import Component from '@ember/component';
import { connect } from 'ember-redux';
import { computed } from '@ember/object';
import Notifications from 'component-lib/mixins/notifications';
import { inject } from '@ember/service';

import {
  groupRankingStatus,
  hasGroupRankingChanged,
  selectedGroupRanking,
  groupRankingSelectedIndex
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

import {
  resetRanking,
  saveGroupRanking,
  setTopRanking
} from 'admin-source-management/actions/creators/group-wizard-creators';

const stateToComputed = (state) => ({
  groupRankingStatus: groupRankingStatus(state),
  hasGroupRankingChanged: hasGroupRankingChanged(state),
  selectedGroupRanking: selectedGroupRanking(state),
  groupRankingSelectedIndex: groupRankingSelectedIndex(state)
});

const dispatchToActions = {
  resetRanking,
  saveGroupRanking,
  setTopRanking
};

const GroupWizardToolbar = Component.extend(Notifications, {
  tagName: 'hbox',
  classNames: ['group-wizard-toolbar'],
  i18n: inject(),
  accessControl: inject(),

  cannotPublishRanking: computed(
    'hasGroupRankingChanged',
    'accessControl.canManageSourceServerGroups',
    function() {
      return !this.hasGroupRankingChanged || !this.accessControl?.canManageSourceServerGroups;
    }
  ),

  // step object required to be passed in
  step: undefined,

  // closure action required to be passed in
  transitionToStep: undefined,

  isStepValid: computed('groupRankingStatus', function() {
    return this.groupRankingStatus == 'complete';
  }),

  hasSelectedGroup: computed('selectedGroupRanking', 'groupRankingSelectedIndex', function() {
    return this.selectedGroupRanking !== null && this.groupRankingSelectedIndex !== 0;
  }),

  actions: {
    transitionToPrevStep() {
      this.get('transitionToStep')(this.get('step').prevStepId);
    },

    transitionToNextStep() {
      this.get('transitionToStep')(this.get('step').nextStepId);
    },

    cancel() {
      this.get('transitionToClose')();
    },
    handleSaveRanking() {
      this.send('saveGroupRanking', {
        onSuccess: () => {
          this.send('success', 'adminUsm.groupRankingWizard.rankingSavedSuccessful');
          this.get('transitionToClose')();
        },
        onFailure: () => {
          this.send('failure', 'adminUsm.groupRankingWizard.rankingSavedFailed');
        }
      });
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(GroupWizardToolbar);