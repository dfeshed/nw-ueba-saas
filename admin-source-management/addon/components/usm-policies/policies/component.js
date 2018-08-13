import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  isPoliciesLoading
} from 'admin-source-management/reducers/usm/policies-selectors';
import creators from 'admin-source-management/actions/creators/policies-creators';
import columns from './columns';

const stateToComputed = (state) => ({
  isPoliciesLoading: isPoliciesLoading(state)
});

const UsmPolicies = Component.extend({
  classNames: ['usm-policies'],
  columns,
  creators
});

export default connect(stateToComputed)(UsmPolicies);