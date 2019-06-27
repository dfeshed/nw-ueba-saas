import Component from '@ember/component';
import { connect } from 'ember-redux';
import { selectedEdrPolicy } from 'investigate-hosts/reducers/details/policy-details/edr-policy/edr-selectors';

// placeholder for future actions
const dispatchToActions = () => {
};

const stateToComputed = (state) => ({
  selectedEdrPolicy: selectedEdrPolicy(state)
});

const EdrPolicy = Component.extend({
  tagName: 'vbox',

  classNames: ['edr-policy']
});

export default connect(stateToComputed, dispatchToActions)(EdrPolicy);