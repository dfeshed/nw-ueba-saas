import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
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
  @computed('focusedPolicy')
  hasSourceError(focusedPolicy) {
    return focusedPolicy.sources ? focusedPolicy.sources.filter((source) => source?.errorState?.state) : '';
  }
});

export default connect(stateToComputed, dispatchToActions)(UsmPoliciesInspector);