import Component from '@ember/component';
import { connect } from 'ember-redux';
import { updatePolicyProperty } from 'admin-source-management/actions/creators/policy-wizard-creators';
import {
  beaconIntervalValue,
  beaconIntervalUnits,
  selectedBeaconIntervalUnit,
  maxFileDownloadSizeValidator
} from 'admin-source-management/reducers/usm/policy-wizard/edrPolicy/edr-selectors';

const stateToComputed = function stateToComputed(state) {
  const [, { selectedSettingId }] = arguments;
  return {
    fileSizeValue: beaconIntervalValue(state, selectedSettingId),
    fileSizeUnits: beaconIntervalUnits(selectedSettingId),
    selectedFileSizeUnit: selectedBeaconIntervalUnit(state, selectedSettingId),
    fileSizeValueValidator: maxFileDownloadSizeValidator(state, selectedSettingId)
  };
};

const dispatchToActions = {
  updatePolicyProperty
};

const FileSizeSelection = Component.extend({
  tagName: 'box',

  classNames: 'file-size-selection',

  classNameBindings: ['selectedSettingId'],

  selectedSettingId: null,

  actions: {
    handleBeaconIntervalValueChange(value) {
      const field = this.get('selectedSettingId');
      value = isNaN(value) ? value : (+value).toFixed(2);
      this.send('updatePolicyProperty', field, value);
    },
    handleBeaconIntervalUnitChange(value) {
      const field = `${this.get('selectedSettingId')}Unit`;
      this.send('updatePolicyProperty', field, value.unit);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(FileSizeSelection);