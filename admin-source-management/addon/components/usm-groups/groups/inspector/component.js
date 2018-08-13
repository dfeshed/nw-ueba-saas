import Component from '@ember/component';
import { connect } from 'ember-redux';

// placeholder for future actions
const dispatchToActions = () => {
};

const UsmGroupsInspector = Component.extend({
  classNames: ['usm-groups-inspector']
});

export default connect(undefined, dispatchToActions)(UsmGroupsInspector);