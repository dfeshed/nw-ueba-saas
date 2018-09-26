import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import Notifications from 'component-lib/mixins/notifications';
import { inject } from '@ember/service';

import {
  group,
  isIdentifyGroupStepValid,
  isDefineGroupStepvalid,
  isApplyPolicyStepvalid,
  isReviewGroupStepvalid,
  isWizardValid
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

import {
  saveGroup,
  savePublishGroup
} from 'admin-source-management/actions/creators/group-wizard-creators';

const stateToComputed = (state) => ({
  group: group(state),
  isIdentifyGroupStepValid: isIdentifyGroupStepValid(state),
  isDefineGroupStepvalid: isDefineGroupStepvalid(state),
  isApplyPolicyStepvalid: isApplyPolicyStepvalid(state),
  isReviewGroupStepvalid: isReviewGroupStepvalid(state),
  isWizardValid: isWizardValid(state)
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

  @computed('step', 'isIdentifyGroupStepValid', 'isDefineGroupStepvalid', 'isApplyPolicyStepvalid', 'isReviewGroupStepvalid')
  isStepValid(step, isIdentifyGroupStepValid, isDefineGroupStepvalid, isApplyPolicyStepvalid, isReviewGroupStepvalid) {
    if (step.id === 'identifyGroupStep') {
      return isIdentifyGroupStepValid;
    } else if (step.id === 'defineGroupStep') {
      return isDefineGroupStepvalid;
    } else if (step.id === 'applyPolicyStep') {
      return isApplyPolicyStepvalid;
    } else if (step.id === 'reviewGroupStep') {
      return isReviewGroupStepvalid;
    }
    return false;
  },

  actions: {

    transitionToPrevStep() {
      this.get('transitionToStep')(this.get('step').prevStepId);
    },

    transitionToNextStep() {
      this.get('transitionToStep')(this.get('step').nextStepId);
    },

    save(publish) {
      let successMessage = 'adminUsm.groupWizard.actionMessages.saveSuccess';
      let failureMessage = 'adminUsm.groupWizard.actionMessages.saveFailure';
      let dispatchAction = 'saveGroup';

      if (publish) {
        successMessage = 'adminUsm.groupWizard.actionMessages.savePublishSuccess';
        failureMessage = 'adminUsm.groupWizard.actionMessages.savePublishFailure';
        dispatchAction = 'savePublishGroup';
      }

      const saveCallbacks = {
        onSuccess: () => {
          if (!this.isDestroyed) {
            this.send('success', successMessage);
            this.get('transitionToClose')();
          }
        },
        onFailure: (response = {}) => {
          if (!this.isDestroyed) {
            const { code } = response;
            const codeKey = `adminUsm.errorCodeResponse.${(code || 'default')}`;
            const codeResponse = this.get('i18n').t(codeKey);
            this.send('failure', failureMessage, { errorType: codeResponse });
          }
        }
      };
      this.send(dispatchAction, this.get('group'), saveCallbacks);
    },

    cancel() {
      this.get('transitionToClose')();
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(GroupWizardToolbar);