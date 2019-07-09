import Component from '@ember/component';
import { connect } from 'ember-redux';
import { selectedWindowsLogPolicy } from 'investigate-hosts/reducers/details/policy-details/windows-log-policy/windows-log-selectors';

// placeholder for future actions
const dispatchToActions = () => {
};

const stateToComputed = (state) => ({
  selectedWindowsLogPolicy: selectedWindowsLogPolicy(state)
});

const WindowsLogPolicy = Component.extend({
  tagName: 'vbox',
  classNames: ['windows-log-policy']
});

export default connect(stateToComputed, dispatchToActions)(WindowsLogPolicy);