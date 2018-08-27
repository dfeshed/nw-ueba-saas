import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';

import {
  policy,
  isIdentifyPolicyStepValid,
  isDefinePolicyStepvalid,
  isApplyToGroupStepvalid,
  isReviewPolicyStepvalid,
  isWizardValid
} from 'admin-source-management/reducers/usm/policy-wizard-selectors';

// import {
//   editPolicy
// } from 'admin-source-management/actions/creators/policy-wizard-creators';

const stateToComputed = (state) => ({
  policy: policy(state),
  isIdentifyPolicyStepValid: isIdentifyPolicyStepValid(state),
  isDefinePolicyStepvalid: isDefinePolicyStepvalid(state),
  isApplyToGroupStepvalid: isApplyToGroupStepvalid(state),
  isReviewPolicyStepvalid: isReviewPolicyStepvalid(state),
  isWizardValid: isWizardValid(state)
});

const dispatchToActions = (/* dispatch */) => ({
});

const PolicyWizardToolbar = Component.extend({
  tagName: 'hbox',
  classNames: ['policy-wizard-toolbar'],

  // step object required to be passed in
  step: undefined,
  // closure action required to be passed in
  transitionToStep: undefined,

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
      // console.log('PolicyWizardToolbar.save()');
    },
    cancel() {
      // console.log('PolicyWizardToolbar.cancel()');
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(PolicyWizardToolbar);