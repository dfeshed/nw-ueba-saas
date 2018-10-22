import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import Notifications from 'component-lib/mixins/notifications';
import { inject } from '@ember/service';

import {
  groupRankingStatus
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

import {
  saveGroup,
  savePublishGroup
} from 'admin-source-management/actions/creators/group-wizard-creators';

const stateToComputed = (state) => ({
  groupRankingStatus: groupRankingStatus(state)
});

const dispatchToActions = {
  saveGroup,
  savePublishGroup
};

const GroupWizardToolbar = Component.extend(Notifications, {
  tagName: 'hbox',
  classNames: ['group-wizard-toolbar'],
  i18n: inject(),

  // step object required to be passed in
  step: undefined,
  // closure action required to be passed in
  transitionToStep: undefined,

  @computed('groupRankingStatus')
  isStepValid(groupRankingStatus) {
    return groupRankingStatus == 'complete';
  },

  actions: {
    transitionToPrevStep() {
      this.get('transitionToStep')(this.get('step').prevStepId);
    },

    transitionToNextStep() {
      this.get('transitionToStep')(this.get('step').nextStepId);
    },

    save() {

    },

    cancel() {
      this.get('transitionToClose')();
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(GroupWizardToolbar);