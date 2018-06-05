import Component from '@ember/component';
import { connect } from 'ember-redux';
import { updatePolicyProperty } from 'admin-source-management/actions/data-creators/policy';
import { scanOptions } from 'admin-source-management/reducers/policy/selector';

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

