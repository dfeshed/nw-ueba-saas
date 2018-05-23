import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  isPolicyListLoading
} from 'admin-source-management/reducers/policy/selector';

import columns from './columns-config';

const stateToComputed = (state) => ({
  policyList: state.policy.policyList,
  isTableLoading: isPolicyListLoading(state)
});

const UsmGroups = Component.extend({
  tagName: 'vbox',
  classNames: ['usm-policies', 'flexi-fit'],
  columns
});

export default connect(stateToComputed)(UsmGroups);