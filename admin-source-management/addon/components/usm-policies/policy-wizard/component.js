import Component from '@ember/component';
import { connect } from 'ember-redux';

import {
  policy,
  steps
} from 'admin-source-management/reducers/usm/policy-wizard-selectors';

// import {
//   editPolicy
// } from 'admin-source-management/actions/creators/policy-wizard-creators';

const stateToComputed = (state) => ({
  policy: policy(state),
  steps: steps(state)
});

const dispatchToActions = (/* dispatch */) => ({
});

const UsmPolicyWizard = Component.extend({
  tagName: 'hbox',
  classNames: ['usm-policy-wizard'],

  actions: {
  }
});

export default connect(stateToComputed, dispatchToActions)(UsmPolicyWizard);