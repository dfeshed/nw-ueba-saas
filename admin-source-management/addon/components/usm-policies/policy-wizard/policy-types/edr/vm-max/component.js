import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  updatePolicyProperty,
  removeFromSelectedSettings
} from 'admin-source-management/actions/creators/policy-wizard-creators';
import { cpuMaxVm } from 'admin-source-management/reducers/usm/policy-wizard/edrPolicy/edr-selectors';

const stateToComputed = (state) => ({
  cpuMaxVm: cpuMaxVm(state)
});

const dispatchToActions = {
  updatePolicyProperty,
  removeFromSelectedSettings
};


const VmMax = Component.extend({
  tagName: 'box',
  classNames: 'vm-max',
  format: {
    to(value) {
      return Math.round(value);
    },
    from(value) {
      return value;
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(VmMax);

