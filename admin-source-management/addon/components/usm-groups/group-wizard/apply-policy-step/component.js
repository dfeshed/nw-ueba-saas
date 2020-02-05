import Component from '@ember/component';
import { connect } from 'ember-redux';
import { computed } from '@ember/object';
import Notifications from 'component-lib/mixins/notifications';
import {
  assignedPolicyList,
  assignedPolicies,
  availablePolicySourceTypes,
  enabledPolicySourceTypesAsObjs
} from 'admin-source-management/reducers/usm/group-wizard-selectors';
import {
  addSourceType,
  placeholderPrep
} from 'admin-source-management/actions/creators/group-wizard-creators';

const stateToComputed = (state) => ({
  assignedPolicyList: assignedPolicyList(state),
  assignedPolicies: assignedPolicies(state),
  availablePolicySourceTypes: enabledPolicySourceTypesAsObjs(availablePolicySourceTypes(state))
});

const dispatchToActions = {
  addSourceType,
  placeholderPrep
};

const ApplyPolicyStep = Component.extend(Notifications, {
  tagName: 'hbox',
  classNames: ['apply-policy-step'],
  sourceType: null,
  policy: null,

  hasAssignments: computed('assignedPolicyList', function() {
    return this.assignedPolicyList.length > 0;
  }),

  hasSourceTypesAvailable: computed('availablePolicySourceTypes', 'assignedPolicyList', function() {
    // first filter out disabled policy source types
    const onlyEnabledTypes = this.availablePolicySourceTypes.filter((sourceType) => !sourceType.disabled);
    return this.assignedPolicyList.length > 0 && onlyEnabledTypes.length != this.assignedPolicyList.length;
  }),

  actions: {
    addSourceType() {
      this.send('placeholderPrep', 'group.assignedPolicies', null, 'add');
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ApplyPolicyStep);
