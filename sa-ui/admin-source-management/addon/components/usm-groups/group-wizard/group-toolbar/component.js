import Component from '@ember/component';
import { connect } from 'ember-redux';
import { run, next } from '@ember/runloop';
import { computed } from '@ember/object';
import Notifications from 'component-lib/mixins/notifications';
import { inject as service } from '@ember/service';

import {
  group,
  isGroupCriteriaEmpty,
  hasGroupChanged,
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
  discardGroupChanges,
  updateCriteriaFromCache,
  removePlaceholderPolicyAssignments
} from 'admin-source-management/actions/creators/group-wizard-creators';

const stateToComputed = (state) => ({
  group: group(state),
  isGroupCriteriaEmpty: isGroupCriteriaEmpty(state),
  hasGroupChanged: hasGroupChanged(state),
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
  discardGroupChanges,
  updateCriteriaFromCache,
  removePlaceholderPolicyAssignments
};

const GroupWizardToolbar = Component.extend(Notifications, {
  tagName: 'hbox',
  classNames: ['group-wizard-toolbar'],
  i18n: service(),
  eventBus: service(),

  // step object required to be passed in
  step: undefined,

  // closure action required to be passed in
  transitionToStep: undefined,

  _showConfirmationModal: false,

  isStepValid: computed(
    'step',
    'isIdentifyGroupStepValid',
    'identifyGroupStepShowErrors',
    'isDefineGroupStepValid',
    'defineGroupStepShowErrors',
    'isApplyPolicyStepValid',
    'applyPolicyStepShowErrors',
    function() {
      switch (this.step.id) {
        case 'identifyGroupStep':
          if (this.isIdentifyGroupStepValid && this.identifyGroupStepShowErrors) {
            run.next(() => {
              this.setShowErrors(false);
            });
          }
          return this.isIdentifyGroupStepValid;

        case 'defineGroupStep':
          if (this.isDefineGroupStepValid && this.defineGroupStepShowErrors) {
            run.next(() => {
              this.setShowErrors(false);
            });
          }
          return this.isDefineGroupStepValid;

        case 'applyPolicyStep':
          if (this.isApplyPolicyStepValid && this.applyPolicyStepShowErrors) {
            run.next(() => {
              this.setShowErrors(false);
            });
          }
          return this.isApplyPolicyStepValid;

        default:
          return false;
      }
    }
  ),

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

    save() {
      this.send('updateCriteriaFromCache');
      if (this.hasGroupChanged && (this.step.id === 'applyPolicyStep')) {
        this.send('removePlaceholderPolicyAssignments');
      }
      if (this.hasGroupChanged && this.isWizardValid) {
        const saveCallbacks = {
          onSuccess: () => {
            if (!this.isDestroyed && !this.isDestroying) {
              this.send('success', 'adminUsm.groupWizard.actionMessages.saveSuccess');
              this.get('transitionToClose')();
            }
          },
          onFailure: (response = {}) => {
            if (!this.isDestroyed && !this.isDestroying) {
              const { code } = response;
              const codeKey = `adminUsm.errorCodeResponse.${(code || 'default')}`;
              const codeResponse = this.get('i18n').t(codeKey);
              this.send('failure', 'adminUsm.groupWizard.actionMessages.saveFailure', { errorType: codeResponse });
            }
          }
        };
        this.send('saveGroup', this.get('group'), saveCallbacks);
      } else {
        // validation issues found
        if ((this.step.id === 'defineGroupStep') && this.isGroupCriteriaEmpty) {
          this.send('failure', 'adminUsm.groupWizard.actionMessages.saveEmptyFailure');
        } else if (this.hasGroupChanged) {
          this.send('failure', 'adminUsm.groupWizard.actionMessages.saveValidationFailure');
        } else {
          this.send('failure', 'adminUsm.groupWizard.actionMessages.saveNoChangeFailure');
        }
        this.setShowErrors(true);
      }
    },

    publish() {
      this.send('updateCriteriaFromCache');
      if (this.hasGroupChanged && (this.step.id === 'applyPolicyStep')) {
        this.send('removePlaceholderPolicyAssignments');
      }
      if ((this.hasGroupChanged || this.group.dirty) && this.isWizardValid) {
        const saveCallbacks = {
          onSuccess: () => {
            if (!this.isDestroyed && !this.isDestroying) {
              this.send('success', 'adminUsm.groupWizard.actionMessages.savePublishSuccess');
              this.get('transitionToClose')();
            }
          },
          onFailure: (response = {}) => {
            if (!this.isDestroyed && !this.isDestroying) {
              const { code } = response;
              const codeKey = `adminUsm.errorCodeResponse.${(code || 'default')}`;
              const codeResponse = this.get('i18n').t(codeKey);
              this.send('failure', 'adminUsm.groupWizard.actionMessages.savePublishFailure', { errorType: codeResponse });
            }
          }
        };
        this.send('savePublishGroup', this.get('group'), saveCallbacks);
      } else {
        // validation issues found
        if ((this.step.id === 'defineGroupStep') && this.isGroupCriteriaEmpty) {
          this.send('failure', 'adminUsm.groupWizard.actionMessages.savePublishEmptyFailure');
        } else if (this.hasGroupChanged) {
          this.send('failure', 'adminUsm.groupWizard.actionMessages.savePublishValidationFailure');
        } else {
          this.send('failure', 'adminUsm.groupWizard.actionMessages.savePublishNoChangeFailure');
        }
        this.setShowErrors(true);
      }
    },

    cancel() {
      if (this.hasGroupChanged) {
        this._showDiscardConfirmation();
      } else {
        this.get('transitionToClose')();
      }
    },

    discardChanges() {
      this.send('discardGroupChanges');
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
    this.get('eventBus').on('rsa-application-modal-open-discard-group-changes', this, '_showDiscardConfirmation');
  },

  init() {
    this.listen();
    this._super(arguments);
  }

});

export default connect(stateToComputed, dispatchToActions)(GroupWizardToolbar);