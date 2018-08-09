import Component from '@ember/component';
import { connect } from 'ember-redux';

// placeholder for future actions
const dispatchToActions = () => {
};

const UsmPoliciesFilter = Component.extend({
  classNames: ['usm-policies-filter']
});

export default connect(undefined, dispatchToActions)(UsmPoliciesFilter);