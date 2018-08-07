import Component from '@ember/component';
import { connect } from 'ember-redux';

const dispatchToActions = () => {
};

const UsmGroupsInspector = Component.extend({
  classNames: ['usm-groups-inspector']
});

export default connect(undefined, dispatchToActions)(UsmGroupsInspector);