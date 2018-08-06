import Component from '@ember/component';
import { connect } from 'ember-redux';
import { updatePolicyProperty } from 'admin-source-management/actions/creators/policy-creators';
import { scanOptions } from 'admin-source-management/reducers/usm/policy-selectors';

const stateToComputed = (state) => ({
  scanOptions: scanOptions(state)
});

const dispatchToActions = {
  updatePolicyProperty
};


const ScanOptions = Component.extend({
  tagName: 'box',

  classNames: 'scan-options',
  format: {
    to(value) {
      return Math.round(value);
    },
    from(value) {
      return value;
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(ScanOptions);

