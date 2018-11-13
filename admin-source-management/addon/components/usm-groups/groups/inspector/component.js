import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  focusedGroup,
  focusedGroupCriteria
} from 'admin-source-management/reducers/usm/group-details/group-selectors';

// placeholder for future actions
const dispatchToActions = () => {
};

const stateToComputed = (state) => ({
  focusedGroup: focusedGroup(state),
  focusedGroupCriteria: focusedGroupCriteria(state)
});

const UsmGroupsInspector = Component.extend({
  classNames: ['usm-groups-inspector']
});

export default connect(stateToComputed, dispatchToActions)(UsmGroupsInspector);