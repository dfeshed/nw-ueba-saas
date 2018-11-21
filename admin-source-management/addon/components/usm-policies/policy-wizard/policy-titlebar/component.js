import Component from '@ember/component';
import { connect } from 'ember-redux';
import { policy } from 'admin-source-management/reducers/usm/policy-wizard/policy-wizard-selectors';

const stateToComputed = (state) => ({
  policy: policy(state)
});

const dispatchToActions = {
};

const PolicyWizardTitlebar = Component.extend({
  tagName: 'vbox',
  classNames: ['policy-wizard-titlebar'],

  // step object required to be passed in
  step: null
});

export default connect(stateToComputed, dispatchToActions)(PolicyWizardTitlebar);
