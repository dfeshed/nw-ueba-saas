import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import Notifications from 'component-lib/mixins/notifications';
import { inject } from '@ember/service';

import {
  group,
  isIdentifyGroupStepValid,
  isDefineGroupStepValid,
  isApplyPolicyStepValid,
  isReviewGroupStepValid,
  isWizardValid
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

import {
  editGroup,
  saveGroup,
  savePublishGroup
} from 'admin-source-management/actions/creators/group-wizard-creators';

const stateToComputed = (state) => ({
  group: group(state),
  isIdentifyGroupStepValid: isIdentifyGroupStepValid(state),
  isDefineGroupStepValid: isDefineGroupStepValid(state),
  isApplyPolicyStepValid: isApplyPolicyStepValid(state),
  isReviewGroupStepValid: isReviewGroupStepValid(state),
  isWizardValid: isWizardValid(state)
});

const dispatchToActions = {
  editGroup,
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

  @computed('step', 'isIdentifyGroupStepValid', 'isDefineGroupStepValid', 'isApplyPolicyStepValid', 'isReviewGroupStepValid')
  isStepValid(step, isIdentifyGroupStepValid, isDefineGroupStepValid, isApplyPolicyStepValid, isReviewGroupStepValid) {
    if (step.id === 'identifyGroupStep') {
      return isIdentifyGroupStepValid;
    } else if (step.id === 'defineGroupStep') {
      return isDefineGroupStepValid;
    } else if (step.id === 'applyPolicyStep') {
      return isApplyPolicyStepValid;
    } else if (step.id === 'reviewGroupStep') {
      return isReviewGroupStepValid;
    }
    return false;
  },

  setVisited() {
    switch (this.step.id) {
      case 'identifyGroupStep':
        this.send('editGroup', 'steps.0.isVisited', true);
        break;
      case 'defineGroupStep':
        this.send('editGroup', 'steps.1.isVisited', true);
        break;
      case 'applyPolicyStep':
        this.send('editGroup', 'steps.2.isVisited', true);
        break;
      default:
        break;
    }
  },

  actions: {

    transitionToPrevStep() {
      this.get('transitionToStep')(this.get('step').prevStepId);
    },

    transitionToNextStep() {
      if (this.isStepValid) {
        this.get('transitionToStep')(this.get('step').nextStepId);
      } else {
        this.setVisited();
        this.send('failure', 'adminUsm.groupWizard.actionMessages.nextFailure');
      }
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