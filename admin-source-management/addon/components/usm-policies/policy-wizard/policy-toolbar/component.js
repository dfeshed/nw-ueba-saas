import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import Notifications from 'component-lib/mixins/notifications';

import {
  policy,
  isIdentifyPolicyStepValid,
  isDefinePolicyStepvalid,
  isApplyToGroupStepvalid,
  isReviewPolicyStepvalid,
  isWizardValid
} from 'admin-source-management/reducers/usm/policy-wizard-selectors';

import {
  savePolicy
} from 'admin-source-management/actions/creators/policy-wizard-creators';

const stateToComputed = (state) => ({
  policy: policy(state),
  isIdentifyPolicyStepValid: isIdentifyPolicyStepValid(state),
  isDefinePolicyStepvalid: isDefinePolicyStepvalid(state),
  isApplyToGroupStepvalid: isApplyToGroupStepvalid(state),
  isReviewPolicyStepvalid: isReviewPolicyStepvalid(state),
  isWizardValid: isWizardValid(state)
});

const dispatchToActions = {
  savePolicy
};

const PolicyWizardToolbar = Component.extend(Notifications, {
  tagName: 'hbox',
  classNames: ['policy-wizard-toolbar'],

  // step object required to be passed in
  step: null,
  // closure action required to be passed in
  transitionToStep: null,
  // closure action expected to be passed in
  transitionToClose: null,

  @computed('step', 'isIdentifyPolicyStepValid', 'isDefinePolicyStepvalid', 'isApplyToGroupStepvalid', 'isReviewPolicyStepvalid')
  isStepValid(step, isIdentifyPolicyStepValid, isDefinePolicyStepvalid, isApplyToGroupStepvalid, isReviewPolicyStepvalid) {
    if (step.id === 'identifyPolicyStep') {
      return isIdentifyPolicyStepValid;
    } else if (step.id === 'definePolicyStep') {
      return isDefinePolicyStepvalid;
    } else if (step.id === 'applyToGroupStep') {
      return isApplyToGroupStepvalid;
    } else if (step.id === 'reviewPolicyStep') {
      return isReviewPolicyStepvalid;
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

    publish() {
      // console.log('PolicyWizardToolbar.publish()');
    },

    save() {
      const saveCallbacks = {
        onSuccess: () => {
          if (!this.isDestroyed) {
            this.send('success', 'adminUsm.policyWizard.saveSuccess');
            this.get('transitionToClose')();
          }
        },
        onFailure: () => {
          if (!this.isDestroyed) {
            this.send('failure', 'adminUsm.policyWizard.saveFailure');
          }
        }
      };
      this.send('savePolicy', this.get('policy'), saveCallbacks);
    },

    cancel() {
      this.get('transitionToClose')();
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(PolicyWizardToolbar);