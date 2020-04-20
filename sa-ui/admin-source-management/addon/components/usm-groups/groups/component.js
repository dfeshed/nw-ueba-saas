import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  isGroupsLoading
} from 'admin-source-management/reducers/usm/groups-selectors';
import creators from 'admin-source-management/actions/creators/groups-creators';
import columns from './columns';

const stateToComputed = (state) => ({
  isGroupsLoading: isGroupsLoading(state)
});

const UsmGroups = Component.extend({
  classNames: ['usm-groups'],
  columns,
  creators
});

export default connect(stateToComputed)(UsmGroups);
