import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';

import { policy } from 'admin-source-management/reducers/usm/policy-wizard-selectors';
import { descriptionsForDisplay } from 'admin-source-management/reducers/usm/util/selector-helpers';


const stateToComputed = (state) => ({
  policy: policy(state)
});

const dispatchToActions = {
};

const PolicyWizardTitlebar = Component.extend({
  tagName: 'vbox',
  classNames: ['policy-wizard-titlebar'],

  // step object required to be passed in
  step: null,

  @computed('policy.description')
  descriptions(description) {
    return descriptionsForDisplay(description);
  }

});

export default connect(stateToComputed, dispatchToActions)(PolicyWizardTitlebar);
