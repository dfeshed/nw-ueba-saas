import Component from '@ember/component';
import { connect } from 'ember-redux';
import Notifications from 'component-lib/mixins/notifications';
import computed from 'ember-computed-decorators';
import { lookup } from 'ember-dependency-lookup';
import { sourceTypes } from 'admin-source-management/reducers/usm/policy-wizard-selectors';
import {
  policyList,
  assignedPolicies,
  assignedPolicyList,
  availablePolicySourceTypes
} from 'admin-source-management/reducers/usm/group-wizard-selectors';
import {
  editGroup
} from 'admin-source-management/actions/creators/group-wizard-creators';

const stateToComputed = (state) => ({
  sourceTypes: sourceTypes(state),
  assignedPolicies: assignedPolicies(state),
  assignedPolicyList: assignedPolicyList(state),
  policyList: policyList(state),
  availablePolicySourceTypes: availablePolicySourceTypes(state)
});

const dispatchToActions = {
  editGroup
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

  actions: {

    handleSourceTypeChange(value) {
      if (value) {
        const previousType = this.get('selectedSourceType');
        const newAssignments = { ...(this.get('assignedPolicies')) };
        delete newAssignments[previousType];
        // Build the placeholder for policy selection
        // We only need id and name
        const i18n = lookup('service:i18n');
        const placeholderName = i18n.t('adminUsm.groupWizard.applyPolicy.policyPlaceholder').toString();
        const reference = { referenceId: 'placeholder', name: placeholderName };
        newAssignments[value] = reference;
        this.send('editGroup', 'group.assignedPolicies', newAssignments);
      } else {
        this.send('editGroup', 'group.assignedPolicies', {});
      }
    },

    handlePolicyAssignment(value) {
      const pathGroupAssignedPolicies = 'group.assignedPolicies.';
      const reference = { referenceId: value.id, name: value.name };
      this.send('editGroup', pathGroupAssignedPolicies + value.policyType, reference);
    }

  }
});

export default connect(stateToComputed, dispatchToActions)(ApplyPolicySourceType);
