import Component from '@ember/component';
import { connect } from 'ember-redux';
import { run } from '@ember/runloop';
import computed from 'ember-computed-decorators';
import Notifications from 'component-lib/mixins/notifications';
import { inject } from '@ember/service';

import {
  group,
  isGroupCriteriaEmpty,
  isIdentifyGroupStepValid,
  isDefineGroupStepValid,
  defineGroupStepShowErrors,
  isApplyPolicyStepValid,
  applyPolicyStepShowErrors,
  isWizardValid
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

import {
  updateGroupStep,
  saveGroup,
  savePublishGroup,
  updateCriteriaFromCache
} from 'admin-source-management/actions/creators/group-wizard-creators';

const stateToComputed = (state) => ({
  group: group(state),
  isGroupCriteriaEmpty: isGroupCriteriaEmpty(state),
  isIdentifyGroupStepValid: isIdentifyGroupStepValid(state),
  isDefineGroupStepValid: isDefineGroupStepValid(state),
  defineGroupStepShowErrors: defineGroupStepShowErrors(state),
  isApplyPolicyStepValid: isApplyPolicyStepValid(state),
  applyPolicyStepShowErrors: applyPolicyStepShowErrors(state),
  isWizardValid: isWizardValid(state)
});

const dispatchToActions = {
  updateGroupStep,
  saveGroup,
  savePublishGroup,
  updateCriteriaFromCache
};

const GroupWizardToolbar = Component.extend(Notifications, {
  tagName: 'hbox',
  classNames: ['group-wizard-toolbar'],
  i18n: inject(),

  // step object required to be passed in
  step: undefined,
  // closure action required to be passed in
  transitionToStep: undefined,

  @computed('step',
   'isIdentifyGroupStepValid', 'identifyGroupStepShowErrors',
   'isDefineGroupStepValid', 'defineGroupStepShowErrors',
   'isApplyPolicyStepValid', 'applyPolicyStepShowErrors')
  isStepValid(step,
    isIdentifyGroupStepValid, identifyGroupStepShowErrors,
    isDefineGroupStepValid, defineGroupStepShowErrors,
    isApplyPolicyStepValid, applyPolicyStepShowErrors) {
    switch (this.step.id) {
      case 'identifyGroupStep':
        if (isIdentifyGroupStepValid && identifyGroupStepShowErrors) {
          run.next(() => {
            this.setShowErrors(false);
          });
        }
        return isIdentifyGroupStepValid;

      case 'defineGroupStep':
        if (isDefineGroupStepValid && defineGroupStepShowErrors) {
          run.next(() => {
            this.setShowErrors(false);
          });
        }
        return isDefineGroupStepValid;

      case 'applyPolicyStep':
        if (isApplyPolicyStepValid && applyPolicyStepShowErrors) {
          run.next(() => {
            this.setShowErrors(false);
          });
        }
        return isApplyPolicyStepValid;

      default:
        return false;
    }
  },

  setShowErrors(show) {
    switch (this.step.id) {
      case 'identifyGroupStep':
        this.send('updateGroupStep', 'steps.0.showErrors', show);
        break;
      case 'defineGroupStep':
        this.send('updateGroupStep', 'steps.1.showErrors', show);
        break;
      case 'applyPolicyStep':
        this.send('updateGroupStep', 'steps.2.showErrors', show);
        break;
      default:
        break;
    }
  },

  actions: {

    transitionToPrevStep() {
      this.send('updateCriteriaFromCache');
      if (this.isStepValid) {
        this.get('transitionToStep')(this.get('step').prevStepId);
      } else {
        this.setShowErrors(true);
        if ((this.step.id === 'defineGroupStep') && this.isGroupCriteriaEmpty) {
          this.send('failure', 'adminUsm.groupWizard.actionMessages.prevEmptyFailure');
        } else {
          this.send('failure', 'adminUsm.groupWizard.actionMessages.prevFailure');
        }
      }
    },

    transitionToNextStep() {
      this.send('updateCriteriaFromCache');
      if (this.isStepValid) {
        this.get('transitionToStep')(this.get('step').nextStepId);
      } else {
        this.setShowErrors(true);
        if ((this.step.id === 'defineGroupStep') && this.isGroupCriteriaEmpty) {
          this.send('failure', 'adminUsm.groupWizard.actionMessages.nextEmptyFailure');
        } else {
          this.send('failure', 'adminUsm.groupWizard.actionMessages.nextFailure');
        }
      }
    },

    save(publish) {
      let validationMessage = 'adminUsm.groupWizard.actionMessages.saveValidationFailure';
      if ((this.step.id === 'defineGroupStep') && this.isGroupCriteriaEmpty) {
        validationMessage = 'adminUsm.groupWizard.actionMessages.saveEmptyFailure';
      }
      let successMessage = 'adminUsm.groupWizard.actionMessages.saveSuccess';
      let failureMessage = 'adminUsm.groupWizard.actionMessages.saveFailure';
      let dispatchAction = 'saveGroup';

      if (publish) {
        validationMessage = 'adminUsm.groupWizard.actionMessages.savePublishValidationFailure';
        if ((this.step.id === 'defineGroupStep') && this.isGroupCriteriaEmpty) {
          validationMessage = 'adminUsm.groupWizard.actionMessages.savePublishEmptyFailure';
        }
        successMessage = 'adminUsm.groupWizard.actionMessages.savePublishSuccess';
        failureMessage = 'adminUsm.groupWizard.actionMessages.savePublishFailure';
        dispatchAction = 'savePublishGroup';
      }

      if (this.isWizardValid) {

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
        this.send('updateCriteriaFromCache');
        this.send(dispatchAction, this.get('group'), saveCallbacks);
      } else {
        this.setShowErrors(true);
        this.send('failure', validationMessage);
      }
    },

    cancel() {
      this.get('transitionToClose')();
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(GroupWizardToolbar);