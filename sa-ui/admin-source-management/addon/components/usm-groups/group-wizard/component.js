import Component from '@ember/component';
import { connect } from 'ember-redux';

import {
  group,
  steps,
  isGroupLoading,
  isGroupFetchError
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

// import {
//   editPolicy
// } from 'admin-source-management/actions/creators/policy-wizard-creators';

const stateToComputed = (state) => ({
  group: group(state),
  steps: steps(state),
  isGroupLoading: isGroupLoading(state),
  isGroupFetchError: isGroupFetchError(state)
});

const dispatchToActions = (/* dispatch */) => ({
});

const UsmGroupWizard = Component.extend({
  tagName: 'hbox',
  classNames: ['usm-group-wizard'],

  // closure action expected to be passed in
  transitionToGroups: null,

  actions: {
  }
});

export default connect(stateToComputed, dispatchToActions)(UsmGroupWizard);