import Component from '@ember/component';
import { connect } from 'ember-redux';
import Notifications from 'component-lib/mixins/notifications';
import computed from 'ember-computed-decorators';
import { sourceTypes } from 'admin-source-management/reducers/usm/policy-wizard/policy-wizard-selectors';
import {
  policyList,
  assignedPolicies,
  assignedPolicyList,
  availablePolicySourceTypes,
  applyPolicyStepShowErrors
} from 'admin-source-management/reducers/usm/group-wizard-selectors';
import {
  editGroup,
  placeholderPrep
} from 'admin-source-management/actions/creators/group-wizard-creators';

const stateToComputed = (state) => ({
  sourceTypes: sourceTypes(state),
  assignedPolicies: assignedPolicies(state),
  assignedPolicyList: assignedPolicyList(state),
  policyList: policyList(state),
  availablePolicySourceTypes: availablePolicySourceTypes(state),
  stepShowErrors: applyPolicyStepShowErrors(state)
});

const dispatchToActions = {
  editGroup,
  placeholderPrep
};

const ApplyPolicySourceType = Component.extend(Notifications, {
  tagName: 'hbox',
  classNames: ['source-type'],
  selectedSourceType: null,
  selectedPolicy: null,

  // Computed value to filter out a list of possible remaining source types based on existing group policy assignments
  @computed('availablePolicySourceTypes', 'assignedPolicyList', 'selectedPolicy')
  limitedPolicySourceTypes(sourceTypes, assignedPolicyList, selectedPolicy) {
    const list = [];
    for (let index = 0; index < sourceTypes.length; index++) {
      if (selectedPolicy && (sourceTypes[index] === selectedPolicy.policyType)) {
        list.push(sourceTypes[index]);
      } else {
        const found = assignedPolicyList.some(function(element) {
          return element.policyType === sourceTypes[index];
        });
        if (!found) {
          list.push(sourceTypes[index]);
        }
      }
    }
    return list;
  },

  @computed('policyList', 'selectedSourceType')
  availablePoliciesForSourceType(policyList, sourceType) {
    const list = [];
    for (let index = 0; index < policyList.length; index++) {
      const policy = policyList[index];
      if ((policy.policyType === sourceType) && (policy.lastPublishedOn > 0)) {
        list.push(policy);
      }
    }
    return list;
  },

  @computed('selectedSourceType', 'selectedPolicy', 'stepShowErrors')
  validator(selectedSourceType, selectedPolicy, stepShowErrors) {
    let inputValid = true;
    if (selectedSourceType && !selectedPolicy) {
      inputValid = false;
    }
    return {
      isError: !inputValid,
      showError: stepShowErrors ? !inputValid : false
    };
  },

  actions: {
    handleSourceTypeChange(value) {
      const previousType = this.get('selectedSourceType');
      this.send('placeholderPrep', 'group.assignedPolicies', value, 'change', previousType);
    },

    handlePolicyAssignment(value) {
      const pathGroupAssignedPolicies = 'group.assignedPolicies.';
      const reference = { referenceId: value.id, name: value.name };
      this.send('editGroup', pathGroupAssignedPolicies + value.policyType, reference);
    },
    handlePolicyRemove(value) {
      const pathGroupAssignedPolicies = 'group.assignedPolicies.';
      this.send('placeholderPrep', pathGroupAssignedPolicies + value.policyType, null, 'remove');
    },
    handleSourceTypeRemove(value) {
      const newAssignments = { ...this.get('assignedPolicies') };
      delete newAssignments[value];
      this.send('editGroup', 'group.assignedPolicies', newAssignments);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ApplyPolicySourceType);
