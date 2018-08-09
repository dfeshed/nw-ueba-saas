import Component from '@ember/component';
import { connect } from 'ember-redux';

// placeholder for future actions
const dispatchToActions = () => {
};

const UsmPoliciesInspector = Component.extend({
  classNames: ['usm-policies-inspector']
});

export default connect(undefined, dispatchToActions)(UsmPoliciesInspector);