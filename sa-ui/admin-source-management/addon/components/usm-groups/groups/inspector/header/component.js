import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  focusedGroup
} from 'admin-source-management/reducers/usm/groups-selectors';

// placeholder for future actions
const dispatchToActions = () => {
};

const stateToComputed = (state) => ({
  focusedGroup: focusedGroup(state)
});

const UsmGroupsInspectorHeader = Component.extend({
  classNames: ['usm-groups-inspector-header']
});

export default connect(stateToComputed, dispatchToActions)(UsmGroupsInspectorHeader);