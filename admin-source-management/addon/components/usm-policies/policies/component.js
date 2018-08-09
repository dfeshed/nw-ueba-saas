import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  policies,
  isPolicyListLoading
} from 'admin-source-management/reducers/usm/policies-selectors';
import creators from 'admin-source-management/actions/creators/policies-creators';
import columns from './columns';

const stateToComputed = (state) => ({
  policies: policies(state),
  isTableLoading: isPolicyListLoading(state)
});

const UsmPolicies = Component.extend({
  tagName: 'vbox',
  classNames: ['usm-policies', 'flexi-fit'],
  columns,
  creators
});

export default connect(stateToComputed)(UsmPolicies);