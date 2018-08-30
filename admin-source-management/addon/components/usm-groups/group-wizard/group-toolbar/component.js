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
  isWizardValid,
  isGroupLoading,
  hasMissingRequiredData
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

import {
  saveGroup
} from 'admin-source-management/actions/creators/group-creators';

const stateToComputed = (state) => ({
  group: group(state),
  isIdentifyGroupStepValid: isIdentifyGroupStepValid(state),
  isDefineGroupStepvalid: isDefineGroupStepvalid(state),
  isApplyPolicyStepvalid: isApplyPolicyStepvalid(state),
  isReviewGroupStepvalid: isReviewGroupStepvalid(state),
  isWizardValid: isWizardValid(state),
  isGroupLoading: isGroupLoading(state),
  hasMissingRequiredData: hasMissingRequiredData(state)
});

const dispatchToActions = {
  saveGroup
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
    save() {
      const onSuccess = () => {
        this.send('success', 'adminUsm.group.saveSuccess');
      };
      const onFailure = () => {
        this.send('failure', 'adminUsm.group.saveFailure');
      };
      this.send('saveGroup', this.get('group'), { onSuccess, onFailure });
    },
    // cancel changes to the group
    cancel() {
      const transitionToGroups = this.get('transitionToGroups');
      transitionToGroups();
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(GroupWizardToolbar);