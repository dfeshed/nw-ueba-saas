import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  selectedWindowsLogPolicy
} from 'admin-source-management/reducers/usm/policy-details/windows-log-policy/windows-log-selectors';

// placeholder for future actions
const dispatchToActions = () => {
};

const stateToComputed = (state) => ({
  selectedWindowsLogPolicy: selectedWindowsLogPolicy(state)
});

const UsmPoliciesWindowsLogInspector = Component.extend({
  classNames: ['usm-policies-inspector-windows-log']
});

export default connect(stateToComputed, dispatchToActions)(UsmPoliciesWindowsLogInspector);