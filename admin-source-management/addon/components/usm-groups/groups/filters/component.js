import Component from '@ember/component';
import { connect } from 'ember-redux';

// placeholder for future actions
const dispatchToActions = () => {
};

const UsmGroupsFilter = Component.extend({
  classNames: ['usm-groups-filter']
});

export default connect(undefined, dispatchToActions)(UsmGroupsFilter);