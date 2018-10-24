import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import Notifications from 'component-lib/mixins/notifications';
import { inject } from '@ember/service';

import {
  policy,
  isIdentifyPolicyStepValid,
  isDefinePolicyStepValid,
  isWizardValid
} from 'admin-source-management/reducers/usm/policy-wizard/policy-wizard-selectors';

import {
  savePolicy,
  savePublishPolicy
} from 'admin-source-management/actions/creators/policy-wizard-creators';

const stateToComputed = (state) => ({
  policy: policy(state),
  isIdentifyPolicyStepValid: isIdentifyPolicyStepValid(state),
  isDefinePolicyStepValid: isDefinePolicyStepValid(state),
  isWizardValid: isWizardValid(state)
});

const dispatchToActions = {
  savePolicy,
  savePublishPolicy
};

const PolicyWizardToolbar = Component.extend(Notifications, {
  tagName: 'hbox',
  classNames: ['policy-wizard-toolbar'],
  i18n: inject(),

  // step object required to be passed in
  step: null,
  // closure action required to be passed in
  transitionToStep: null,
  // closure action expected to be passed in
  transitionToClose: null,

  @computed('step', 'isIdentifyPolicyStepValid', 'isDefinePolicyStepValid')
  isStepValid(step, isIdentifyPolicyStepValid, isDefinePolicyStepValid) {
    if (step.id === 'identifyPolicyStep') {
      return isIdentifyPolicyStepValid;
    } else if (step.id === 'definePolicyStep') {
      return isDefinePolicyStepValid;
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
      let successMessage = 'adminUsm.policyWizard.actionMessages.saveSuccess';
      let failureMessage = 'adminUsm.policyWizard.actionMessages.saveFailure';
      let dispatchAction = 'savePolicy';

      if (publish) {
        successMessage = 'adminUsm.policyWizard.actionMessages.savePublishSuccess';
        failureMessage = 'adminUsm.policyWizard.actionMessages.savePublishFailure';
        dispatchAction = 'savePublishPolicy';
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
      this.send(dispatchAction, this.get('policy'), saveCallbacks);
    },

    cancel() {
      this.get('transitionToClose')();
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(PolicyWizardToolbar);