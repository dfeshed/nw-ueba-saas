import Component from '@ember/component';
import { connect } from 'ember-redux';

import { group } from 'admin-source-management/reducers/usm/group-wizard-selectors';

const stateToComputed = (state) => ({
  group: group(state)
});

const dispatchToActions = {
};

const GroupWizardTitlebar = Component.extend({
  tagName: 'vbox',
  classNames: ['group-wizard-titlebar'],

  // step object required to be passed in
  step: null

});

export default connect(stateToComputed, dispatchToActions)(GroupWizardTitlebar);