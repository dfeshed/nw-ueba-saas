import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  updatePolicyProperty,
  removeFromSelectedSettings
} from 'admin-source-management/actions/creators/policy-creators';
import { scanOptions } from 'admin-source-management/reducers/usm/policy-selectors';

const stateToComputed = (state) => ({
  scanOptions: scanOptions(state)
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

