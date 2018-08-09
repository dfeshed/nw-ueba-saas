import Component from '@ember/component';
import { connect } from 'ember-redux';

// placeholder for future actions
const dispatchToActions = () => {
};

const UsmPoliciesToolbar = Component.extend({
  classNames: ['usm-policies-toolbar']
});

export default connect(undefined, dispatchToActions)(UsmPoliciesToolbar);