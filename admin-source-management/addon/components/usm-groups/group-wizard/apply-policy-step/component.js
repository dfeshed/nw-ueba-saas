import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import Notifications from 'component-lib/mixins/notifications';
import {
  assignedPolicyList
} from 'admin-source-management/reducers/usm/group-wizard-selectors';
import {
  addSourceType,
  editGroup
} from 'admin-source-management/actions/creators/group-wizard-creators';

const stateToComputed = (state) => ({
  assignedPolicyList: assignedPolicyList(state)
});

const dispatchToActions = {
  addSourceType,
  editGroup
};

const ApplyPolicyStep = Component.extend(Notifications, {
  tagName: 'hbox',
  classNames: ['apply-policy-step'],
  sourceType: null,
  policy: null,

  @computed('assignedPolicyList')
  hasAssignments(assignedPolicyList) {
    return (assignedPolicyList.length > 0);
  }
});

export default connect(stateToComputed, dispatchToActions)(ApplyPolicyStep);
