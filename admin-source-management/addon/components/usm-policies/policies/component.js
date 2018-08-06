import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  policies,
  isPolicyListLoading
} from 'admin-source-management/reducers/usm/policies-selectors';

import columns from './columns';

const stateToComputed = (state) => ({
  policies: policies(state),
  // policies: state.usm.policies.items,
  isTableLoading: isPolicyListLoading(state)
});

const UsmGroups = Component.extend({
  tagName: 'vbox',
  classNames: ['usm-policies', 'flexi-fit'],
  columns
});

export default connect(stateToComputed)(UsmGroups);