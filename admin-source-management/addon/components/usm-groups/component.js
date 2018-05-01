import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  groups,
  isGroupsLoading
} from 'admin-source-management/selectors/groups-selectors';
// import groupsCreators from 'admin-source-management/actions/creators/groups-creators';
import columns from './columns';
import layout from './template';

const stateToComputed = (state) => ({
  groups: groups(state),
  isGroupsLoading: isGroupsLoading(state)
});

// const dispatchToActions = (dispatch) => {
const dispatchToActions = () => {
  return {
    // someAction() {
    //   dispatch(groupsCreators.someAction());
    // }
  };
};

const UsmGroups = Component.extend({
  tagName: 'vbox',
  classNames: ['usm-groups', 'flexi-fit'],
  columns,
  layout
});

export default connect(stateToComputed, dispatchToActions)(UsmGroups);
