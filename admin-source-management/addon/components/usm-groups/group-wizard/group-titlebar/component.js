import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';

import { group } from 'admin-source-management/reducers/usm/group-wizard-selectors';
import { descriptionsForDisplay } from 'admin-source-management/reducers/usm/util/selector-helpers';

const stateToComputed = (state) => ({
  group: group(state)
});

const dispatchToActions = {
};

const GroupWizardTitlebar = Component.extend({
  tagName: 'vbox',
  classNames: ['group-wizard-titlebar'],

  // step object required to be passed in
  step: null,

  @computed('group.description')
  descriptions(description) {
    return descriptionsForDisplay(description);
  }

});

export default connect(stateToComputed, dispatchToActions)(GroupWizardTitlebar);
