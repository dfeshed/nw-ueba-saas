import Component from '@ember/component';
import { connect } from 'ember-redux';

import {
  group,
  steps
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

// import {
//   editPolicy
// } from 'admin-source-management/actions/creators/policy-wizard-creators';

const stateToComputed = (state) => ({
  group: group(state),
  steps: steps(state)
});

const dispatchToActions = (/* dispatch */) => ({
});

const UsmGroupWizard = Component.extend({
  tagName: 'hbox',
  classNames: ['usm-group-wizard'],

  actions: {
  }
});

export default connect(stateToComputed, dispatchToActions)(UsmGroupWizard);