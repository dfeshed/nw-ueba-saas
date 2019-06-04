import Component from '@ember/component';
import { connect } from 'ember-redux';
import { run, next } from '@ember/runloop';
import computed from 'ember-computed-decorators';
import Notifications from 'component-lib/mixins/notifications';
import { inject as service } from '@ember/service';

import {
  policy,
  isPolicySettingsEmpty,
  hasPolicyChanged,
  isIdentifyPolicyStepValid,
  identifyPolicyStepShowErrors,
  isDefinePolicyStepValid,
  definePolicyStepShowErrors,
  isWizardValid
} from 'admin-source-management/reducers/usm/policy-wizard/policy-wizard-selectors';

import {
  updatePolicyStep,
  savePolicy,
  savePublishPolicy,
  discardPolicyChanges
} from 'admin-source-management/actions/creators/policy-wizard-creators';

const stateToComputed = (state) => ({
  policy: policy(state),
  hasPolicyChanged: hasPolicyChanged(state),
  isPolicySettingsEmpty: isPolicySettingsEmpty(state),
  isIdentifyPolicyStepValid: isIdentifyPolicyStepValid(state),
  identifyPolicyStepShowErrors: identifyPolicyStepShowErrors(state),
  isDefinePolicyStepValid: isDefinePolicyStepValid(state),
  definePolicyStepShowErrors: definePolicyStepShowErrors(state),
  isWizardValid: isWizardValid(state)
});

const dispatchToActions = {
  updatePolicyStep,
  savePolicy,
  savePublishPolicy,
  discardPolicyChanges
};

const PolicyWizardToolbar = Component.extend(Notifications, {
  tagName: 'hbox',
  classNames: ['policy-wizard-toolbar'],
  i18n: service(),
  eventBus: service(),

  // step object required to be passed in
  step: null,
  // closure action required to be passed in
  transitionToStep: null,
  _showConfirmationModal: false,

  @computed(
    'step',
    'isIdentifyPolicyStepValid',
    'identifyPolicyStepShowErrors',
    'isDefinePolicyStepValid',
    'definePolicyStepShowErrors'
  )
  isStepValid(step,
    isIdentifyPolicyStepValid, identifyPolicyStepShowErrors,
    isDefinePolicyStepValid, definePolicyStepShowErrors) {
    switch (this.step.id) {
      case 'identifyPolicyStep':
        if (isIdentifyPolicyStepValid && identifyPolicyStepShowErrors) {
          run.next(() => {
            this.setShowErrors(false);
          });
        }
        return isIdentifyPolicyStepValid;

      case 'definePolicyStep':
        if (isDefinePolicyStepValid && definePolicyStepShowErrors) {
          run.next(() => {
            this.setShowErrors(false);
          });
        }
        return isDefinePolicyStepValid;

      default:
        return false;
    }
  },

  setShowErrors(show) {
    switch (this.step.id) {
      case 'identifyPolicyStep':
        this.send('updatePolicyStep', 'steps.0.showErrors', show);
        break;
      case 'definePolicyStep':
        this.send('updatePolicyStep', 'steps.1.showErrors', show);
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
        this.setShowErrors(true);
        if ((this.step.id === 'definePolicyStep') && this.isPolicySettingsEmpty) {
          this.send('failure', 'adminUsm.policyWizard.actionMessages.nextEmptyFailure');
        } else {
          this.send('failure', 'adminUsm.policyWizard.actionMessages.nextFailure');
        }
      }
    },

    save() {
      if (this.hasPolicyChanged && this.isWizardValid) {
        const saveCallbacks = {
          onSuccess: () => {
            if (!this.isDestroyed && !this.isDestroying) {
              this.send('success', 'adminUsm.policyWizard.actionMessages.saveSuccess');
              this.get('transitionToClose')();
            }
          },
          onFailure: (response = {}) => {
            if (!this.isDestroyed && !this.isDestroying) {
              const { code } = response;
              const codeKey = `adminUsm.errorCodeResponse.${(code || 'default')}`;
              const codeResponse = this.get('i18n').t(codeKey);
              this.send('failure', 'adminUsm.policyWizard.actionMessages.saveFailure', { errorType: codeResponse });
            }
          }
        };
        this.send('savePolicy', this.get('policy'), saveCallbacks);
      } else {
        // validation issues found
        if ((this.step.id === 'definePolicyStep') && this.isPolicySettingsEmpty) {
          this.send('failure', 'adminUsm.policyWizard.actionMessages.saveEmptyFailure');
        } else if (this.hasPolicyChanged) {
          this.send('failure', 'adminUsm.policyWizard.actionMessages.saveValidationFailure');
        } else {
          this.send('failure', 'adminUsm.policyWizard.actionMessages.saveNoChangeFailure');
        }
        this.setShowErrors(true);
      }
    },

    publish() {
      if ((this.hasPolicyChanged || this.policy.dirty) && this.isWizardValid) {
        const saveCallbacks = {
          onSuccess: () => {
            if (!this.isDestroyed && !this.isDestroying) {
              this.send('success', 'adminUsm.policyWizard.actionMessages.savePublishSuccess');
              this.get('transitionToClose')();
            }
          },
          onFailure: (response = {}) => {
            if (!this.isDestroyed && !this.isDestroying) {
              const { code } = response;
              const codeKey = `adminUsm.errorCodeResponse.${(code || 'default')}`;
              const codeResponse = this.get('i18n').t(codeKey);
              this.send('failure', 'adminUsm.policyWizard.actionMessages.savePublishFailure', { errorType: codeResponse });
            }
          }
        };
        this.send('savePublishPolicy', this.get('policy'), saveCallbacks);
      } else {
        // validation issues found
        if ((this.step.id === 'definePolicyStep') && this.isPolicySettingsEmpty) {
          this.send('failure', 'adminUsm.policyWizard.actionMessages.savePublishEmptyFailure');
        } else if (this.hasPolicyChanged) {
          this.send('failure', 'adminUsm.policyWizard.actionMessages.savePublishValidationFailure');
        } else {
          this.send('failure', 'adminUsm.policyWizard.actionMessages.savePublishNoChangeFailure');
        }
        this.setShowErrors(true);
      }
    },

    cancel() {
      if (this.hasPolicyChanged) {
        this._showDiscardConfirmation();
      } else {
        this.get('transitionToClose')();
      }
    },

    discardChanges() {
      this.send('discardPolicyChanges');
      this._closeModal();
      this.get('transitionToClose')();
    },

    continueEditing() {
      this._closeModal();
    },

    onModalClose() {
      this.set('_showConfirmationModal', false);
    }
  },

  _showDiscardConfirmation() {
    this.set('_showConfirmationModal', true);
    next(() => {
      this.get('eventBus').trigger('rsa-application-modal-open-confirm-modal');
    });
  },

  _closeModal() {
    this.get('eventBus').trigger('rsa-application-modal-close-confirm-modal');
  },

  listen() {
    this.get('eventBus').on('rsa-application-modal-open-discard-policy-changes', this, '_showDiscardConfirmation');
  },

  init() {
    this.listen();
    this._super(arguments);
  }

});

export default connect(stateToComputed, dispatchToActions)(PolicyWizardToolbar);