import Component from '@ember/component';
import { connect } from 'ember-redux';

const dispatchToActions = () => {
};

const UsmGroupsToolbar = Component.extend({
  classNames: ['usm-groups-toolbar']
});

export default connect(undefined, dispatchToActions)(UsmGroupsToolbar);