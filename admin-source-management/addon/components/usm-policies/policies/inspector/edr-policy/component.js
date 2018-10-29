import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  selectedEdrPolicy
} from 'admin-source-management/reducers/usm/policy-details/edr-policy/edr-selectors';

// placeholder for future actions
const dispatchToActions = () => {
};

const stateToComputed = (state) => ({
  selectedEdrPolicy: selectedEdrPolicy(state)
});

const UsmPoliciesEdrInspector = Component.extend({
  classNames: ['usm-policies-inspector-edr']
});

export default connect(stateToComputed, dispatchToActions)(UsmPoliciesEdrInspector);