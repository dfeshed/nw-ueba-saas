import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  selectedFilePolicy
} from 'investigate-hosts/reducers/details/policy-details/file-policy/file-selectors';

// placeholder for future actions
const dispatchToActions = () => {
};

const stateToComputed = (state) => ({
  selectedFilePolicy: selectedFilePolicy(state)
});

const UsmPoliciesFileInspector = Component.extend({
  classNames: ['file-policies']
});

export default connect(stateToComputed, dispatchToActions)(UsmPoliciesFileInspector);