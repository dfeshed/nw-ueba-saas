import Component from '@ember/component';
import { connect } from 'ember-redux';
import { computed } from '@ember/object';
import {
  focusedPolicy
} from 'admin-source-management/reducers/usm/policies-selectors';

// placeholder for future actions
const dispatchToActions = () => {
};

const stateToComputed = (state) => ({
  focusedPolicy: focusedPolicy(state)
});

const UsmPoliciesInspector = Component.extend({
  classNames: ['usm-policies-inspector'],

  hasSourceError: computed('focusedPolicy', function() {
    return this.focusedPolicy.sources ? this.focusedPolicy.sources.filter((source) => source?.errorState?.state) : '';
  })
});

export default connect(stateToComputed, dispatchToActions)(UsmPoliciesInspector);