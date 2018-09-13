import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import Notifications from 'component-lib/mixins/notifications';

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
    },  // save changes to the group
    save(publish) {
      let successMessage = 'adminUsm.group.saveSuccess';
      let failureMessage = 'adminUsm.group.saveFailure';
      let dispatchAction = 'saveGroup';

      if (publish) {
        successMessage = 'adminUsm.groupWizard.savePublishSuccess';
        failureMessage = 'adminUsm.groupWizard.savePublishFailure';
        dispatchAction = 'savePublishGroup';
      }

      const onSuccess = () => {
        if (!this.isDestroyed) {
          this.send('success', successMessage);
          this.get('transitionToClose')();
        }
      };
      const onFailure = () => {
        if (!this.isDestroyed) {
          this.send('failure', failureMessage);
        }
      };
      this.send(dispatchAction, this.get('group'), { onSuccess, onFailure });
    },
    // cancel changes to the group
    cancel() {
      this.get('transitionToClose')();
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(GroupWizardToolbar);