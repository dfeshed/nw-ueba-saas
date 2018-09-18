import Component from '@ember/component';
import { connect } from 'ember-redux';
import Notifications from 'component-lib/mixins/notifications';
import {
  policyList,
  selectedPolicy
} from 'admin-source-management/reducers/usm/group-wizard-selectors';
import {
  editGroup
} from 'admin-source-management/actions/creators/group-wizard-creators';

const stateToComputed = (state) => ({
  policyList: policyList(state),
  selectedPolicy: selectedPolicy(state)
});

const dispatchToActions = {
  editGroup
};

const ApplyPolicyStep = Component.extend(Notifications, {
  tagName: 'hbox',
  classNames: ['apply-policy-step'],

  actions: {

    handlePolicyAssignment(value) {
      const assignedPolicies = {};
      const entity = {};
      entity.referenceId = value.id;
      assignedPolicies[value.policyType] = entity;
      this.send('editGroup', 'group.assignedPolicies', assignedPolicies);
    }

  }
});

export default connect(stateToComputed, dispatchToActions)(ApplyPolicyStep);
