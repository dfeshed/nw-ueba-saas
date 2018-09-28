import Component from '@ember/component';
import { connect } from 'ember-redux';
import _ from 'lodash';

import {
  groupCriteria
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

// cloneDeep is needed for OS Type power-selector-multiple as it is directly mutating the selected items
const stateToComputed = (state) => ({
  groupCriteria: _.cloneDeep(groupCriteria(state))
});

const DefineGroupStep = Component.extend({
  tagName: 'vbox',
  classNames: ['define-group-step', 'scroll-box', 'rsa-wizard-step'],

  actions: {
  }
});
export default connect(stateToComputed)(DefineGroupStep);
