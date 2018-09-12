import Component from '@ember/component';
import { connect } from 'ember-redux';

import {
  groupCriteria
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

const stateToComputed = (state) => ({
  groupCriteria: groupCriteria(state)
});

const dispatchToActions = {
  // selectParserRule
};

const DefineGroupStep = Component.extend({
  tagName: 'vbox',
  classNames: ['define-group-step', 'scroll-box'],

  actions: {
  }
});
export default connect(stateToComputed, dispatchToActions)(DefineGroupStep);
