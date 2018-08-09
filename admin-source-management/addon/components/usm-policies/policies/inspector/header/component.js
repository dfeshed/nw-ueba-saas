import Component from '@ember/component';
import { connect } from 'ember-redux';

// placeholder for future actions
const dispatchToActions = () => {
};

const UsmPoliciesInspectorHeader = Component.extend({
  classNames: ['usm-policies-inspector-header']
});

export default connect(undefined, dispatchToActions)(UsmPoliciesInspectorHeader);