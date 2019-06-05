import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
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

  @computed('assignedPolicyList')
  hasAssignments(assignedPolicyList) {
    return assignedPolicyList.length > 0;
  },

  @computed('availablePolicySourceTypes', 'assignedPolicyList')
  hasSourceTypesAvailable(sourceTypes, assignedPolicyList) {
    // first filter out disabled policy source types
    const onlyEnabledTypes = sourceTypes.filter((sourceType) => !sourceType.disabled);
    return (assignedPolicyList.length > 0 && onlyEnabledTypes.length != assignedPolicyList.length);
  },

  actions: {
    addSourceType() {
      this.send('placeholderPrep', 'group.assignedPolicies', null, 'add');
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ApplyPolicyStep);
