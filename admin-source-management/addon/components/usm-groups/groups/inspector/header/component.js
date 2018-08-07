import Component from '@ember/component';
import { connect } from 'ember-redux';

const dispatchToActions = () => {
};

const UsmGroupsInspectorHeader = Component.extend({
  classNames: ['usm-groups-inspector-header']
});

export default connect(undefined, dispatchToActions)(UsmGroupsInspectorHeader);