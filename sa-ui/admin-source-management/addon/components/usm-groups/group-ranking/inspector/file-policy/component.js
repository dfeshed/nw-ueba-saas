import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  selectedFilePolicy
} from 'admin-source-management/reducers/usm/policy-details/file-policy/file-selectors';

// placeholder for future actions
const dispatchToActions = () => {
};

const stateToComputed = (state) => ({
  selectedFilePolicy: selectedFilePolicy(state)
});

const UsmPoliciesFileInspector = Component.extend({
  classNames: ['usm-policies-inspector-file']
});

export default connect(stateToComputed, dispatchToActions)(UsmPoliciesFileInspector);