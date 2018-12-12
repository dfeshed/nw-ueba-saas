import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import Notifications from 'component-lib/mixins/notifications';
import { inject } from '@ember/service';

import {
  groupRankingStatus,
  hasGroupRankingChanged,
  selectedGroupRanking
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

import {
  resetRanking,
  saveGroupRanking,
  setTopRanking
} from 'admin-source-management/actions/creators/group-wizard-creators';

const stateToComputed = (state) => ({
  groupRankingStatus: groupRankingStatus(state),
  hasGroupRankingChanged: hasGroupRankingChanged(state),
  selectedGroupRanking: selectedGroupRanking(state)
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

  @computed('hasGroupRankingChanged', 'accessControl.canManageSourceServerGroups')
  cannotPublishRanking(hasGroupRankingChanged, canManageSourceServerGroups) {
    return !hasGroupRankingChanged || !canManageSourceServerGroups;
  },
  // step object required to be passed in
  step: undefined,
  // closure action required to be passed in
  transitionToStep: undefined,

  @computed('groupRankingStatus')
  isStepValid(groupRankingStatus) {
    return groupRankingStatus == 'complete';
  },
  @computed('selectedGroupRanking')
  hasSelectedGroup(selectedGroupRanking) {
    return selectedGroupRanking !== null;
  },
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