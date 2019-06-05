import Component from '@ember/component';
import { connect } from 'ember-redux';
import Notifications from 'component-lib/mixins/notifications';
import computed from 'ember-computed-decorators';
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

  @computed('availablePolicySourceTypes', 'selectedSourceType')
  selectedSourceTypeObj(policySourceTypesAsObjs, sourceType) {
    const sourceTypeObj = selectedSourceTypeAsObj(policySourceTypesAsObjs, sourceType);
    if (sourceTypeObj && sourceTypeObj.disabled) {
      return null;
    }
    return sourceTypeObj;
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
    // sort the list by policy name
    const sortColumn = 'name';
    const descending = false;
    const sortedList = list.sort(sortBy(sortColumn, descending, function(a) {
      return String(a).toUpperCase();
    }));
    return sortedList;
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

  @computed('selectedPolicy')
  hasSelectedPolicy(selectedPolicy) {
    return !!selectedPolicy;
  },

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
      const newAssignments = { ...this.get('assignedPolicies') };
      delete newAssignments[value];
      this.send('editGroup', 'group.assignedPolicies', newAssignments);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ApplyPolicySourceType);
