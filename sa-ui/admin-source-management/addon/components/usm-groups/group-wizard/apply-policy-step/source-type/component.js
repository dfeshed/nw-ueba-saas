import Component from '@ember/component';
import { connect } from 'ember-redux';
import Notifications from 'component-lib/mixins/notifications';
import { computed } from '@ember/object';
// import { sourceTypes } from 'admin-source-management/reducers/usm/policy-wizard/policy-wizard-selectors';
import { sortBy } from 'admin-source-management/reducers/usm/util/selector-helpers';
import {
  policyList,
  assignedPolicies,
  assignedPolicyList,
  availablePolicySourceTypes,
  limitedPolicySourceTypes,
  enabledPolicySourceTypesAsObjs,
  selectedSourceTypeAsObj,
  applyPolicyStepShowErrors
} from 'admin-source-management/reducers/usm/group-wizard-selectors';
import {
  editGroup,
  placeholderPrep
} from 'admin-source-management/actions/creators/group-wizard-creators';

const stateToComputed = (state) => ({
  // sourceTypes: sourceTypes(state),
  assignedPolicies: assignedPolicies(state),
  assignedPolicyList: assignedPolicyList(state),
  policyList: policyList(state),
  availablePolicySourceTypes: enabledPolicySourceTypesAsObjs(availablePolicySourceTypes(state)),
  limitedPolicySourceTypes: enabledPolicySourceTypesAsObjs(limitedPolicySourceTypes(state)),
  stepShowErrors: applyPolicyStepShowErrors(state)
});

const dispatchToActions = {
  editGroup,
  placeholderPrep
};

const ApplyPolicySourceType = Component.extend(Notifications, {
  classNames: ['source-type'],
  selectedSourceType: null,
  selectedPolicy: null,

  selectedSourceTypeObj: computed('availablePolicySourceTypes', 'selectedSourceType', function() {
    const sourceTypeObj = selectedSourceTypeAsObj(this.availablePolicySourceTypes, this.selectedSourceType);
    if (sourceTypeObj && sourceTypeObj.disabled) {
      return null;
    }
    return sourceTypeObj;
  }),

  availablePoliciesForSourceType: computed('policyList', 'selectedSourceType', function() {
    const list = [];
    for (let index = 0; index < this.policyList.length; index++) {
      const policy = this.policyList[index];
      if ((policy.policyType === this.selectedSourceType) && (policy.lastPublishedOn > 0)) {
        list.push(policy);
      }
    }
    // sort the list by policy name
    const sortColumn = 'name';
    const descending = false;
    const sortedList = list.sort(sortBy(sortColumn, descending, function(a) {
      return String(a).toUpperCase();
    }));
    return sortedList;
  }),

  validator: computed('selectedSourceType', 'selectedPolicy', 'stepShowErrors', function() {
    let inputValid = true;
    if (this.selectedSourceType && !this.selectedPolicy) {
      inputValid = false;
    }
    return {
      isError: !inputValid,
      showError: this.stepShowErrors ? !inputValid : false
    };
  }),

  hasSelectedPolicy: computed('selectedPolicy', function() {
    return !!this.selectedPolicy;
  }),

  actions: {
    handleSourceTypeChange(value) {
      const previousType = this.get('selectedSourceType');
      // power-select passes the whole object, we only want the policy type
      this.send('placeholderPrep', 'group.assignedPolicies', value.policyType, 'change', previousType);
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
      // action will work on click, Enter key or Spacebar
      if (event.type === 'click' || event.key === 'Enter' || event.key === ' ') {
        const newAssignments = { ...this.get('assignedPolicies') };
        delete newAssignments[value];
        this.send('editGroup', 'group.assignedPolicies', newAssignments);
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ApplyPolicySourceType);
