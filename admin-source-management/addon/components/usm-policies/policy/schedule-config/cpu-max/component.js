import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  updatePolicyProperty,
  removeFromSelectedSettings
} from 'admin-source-management/actions/creators/policy-wizard-creators';
import { cpuMaximum } from 'admin-source-management/reducers/usm/policy-wizard-selectors';

const stateToComputed = (state) => ({
  cpuMaximum: cpuMaximum(state)
});

const dispatchToActions = {
  updatePolicyProperty,
  removeFromSelectedSettings
};


const CpuMax = Component.extend({
  tagName: 'box',
  classNames: 'cpu-max',
  format: {
    to(value) {
      return Math.round(value);
    },
    from(value) {
      return value;
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(CpuMax);

