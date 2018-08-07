import Component from '@ember/component';
import { connect } from 'ember-redux';

const dispatchToActions = () => {
};

const UsmGroupsFilter = Component.extend({
  classNames: ['usm-groups-filter']
});

export default connect(undefined, dispatchToActions)(UsmGroupsFilter);