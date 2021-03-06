import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  updatePolicyProperty
} from 'admin-source-management/actions/creators/policy-wizard-creators';
import { cpuMax } from 'admin-source-management/reducers/usm/policy-wizard/edrPolicy/edr-selectors';

const stateToComputed = (state) => ({
  cpuMax: cpuMax(state)
});

const dispatchToActions = {
  updatePolicyProperty
};


const CpuMax = Component.extend({
  tagName: 'box',
  classNames: 'cpu-max',
  classNameBindings: ['selectedSettingId'],
  isDefaultPolicy: null,
  format: null,

  init() {
    this._super(...arguments);
    this.format = {
      to(value) {
        return Math.round(value);
      },
      from(value) {
        return value;
      }
    };
  }
});

export default connect(stateToComputed, dispatchToActions)(CpuMax);

