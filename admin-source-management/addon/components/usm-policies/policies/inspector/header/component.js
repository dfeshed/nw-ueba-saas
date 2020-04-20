import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  focusedPolicy
} from 'admin-source-management/reducers/usm/policies-selectors';

// placeholder for future actions
const dispatchToActions = () => {
};

const stateToComputed = (state) => ({
  focusedPolicy: focusedPolicy(state)
});

const UsmPoliciesInspectorHeader = Component.extend({
  classNames: ['usm-policies-inspector-header']
});

export default connect(stateToComputed, dispatchToActions)(UsmPoliciesInspectorHeader);