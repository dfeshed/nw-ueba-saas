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

// const dispatchToActions = (dispatch) => {
const dispatchToActions = () => {
  return {
    // someAction() {
    //   dispatch(groupsCreators.someAction());
    // }
  };
};

const UsmGroupsExplorer = Component.extend({
  classNames: ['usm-groups'],
  columns,
  creators
});

export default connect(stateToComputed, dispatchToActions)(UsmGroupsExplorer);
