import Component from '@ember/component';
import { connect } from 'ember-redux';

import {
  enabledAvailableSettings,
  sortedSelectedSettings
} from 'admin-source-management/reducers/usm/policy-wizard-selectors';

import {
  addToSelectedSettings
} from 'admin-source-management/actions/creators/policy-wizard-creators';


const stateToComputed = (state) => ({
  enabledAvailableSettings: enabledAvailableSettings(state),
  sortedSelectedSettings: sortedSelectedSettings(state)
});

const dispatchToActions = {
  addToSelectedSettings
};

const DefinePolicyStep = Component.extend({
  tagName: 'vbox',
  classNames: ['define-policy-step', 'scroll-box', 'rsa-wizard-step'],

  // step object required to be passed in
  // step: null, // the wizard passes this in but we're not using it (yet anyway) - uncomment if/when needed

  actions: {
  }
});

export default connect(stateToComputed, dispatchToActions)(DefinePolicyStep);
