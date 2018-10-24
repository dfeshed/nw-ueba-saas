import Component from '@ember/component';
import { connect } from 'ember-redux';
import { updatePolicyProperty, removeFromSelectedSettings } from 'admin-source-management/actions/creators/policy-wizard-creators';
import {
  beaconIntervalValue,
  beaconIntervalUnits,
  selectedBeaconIntervalUnit,
  beaconIntervalValueValidator
} from 'admin-source-management/reducers/usm/policy-wizard/edrPolicy/edr-selectors';

const stateToComputed = function stateToComputed(state) {
  const [, { selectedSettingId }] = arguments;
  return {
    beaconIntervalValue: beaconIntervalValue(state, selectedSettingId),
    beaconIntervalUnits: beaconIntervalUnits(selectedSettingId),
    selectedBeaconIntervalUnit: selectedBeaconIntervalUnit(state, selectedSettingId),
    beaconIntervalValueValidator: beaconIntervalValueValidator(state, selectedSettingId)
  };
};

const dispatchToActions = {
  updatePolicyProperty,
  removeFromSelectedSettings
};

const UsmBeacons = Component.extend({
  tagName: 'box',

  classNames: 'usm-beacons',

  classNameBindings: ['selectedSettingId'],

  selectedSettingId: null,

  actions: {
    handleBeaconIntervalValueChange(value) {
      const field = this.get('selectedSettingId');
      value = isNaN(parseInt(value, 10)) ? '' : parseInt(value, 10);
      this.send('updatePolicyProperty', field, value);
    },
    handleBeaconIntervalUnitChange(value) {
      // combine the selectedSettingId + 'Unit' to update the correct unit prop
      // (ex. 'httpBeaconInterval' + 'Unit' = 'httpBeaconIntervalUnit')
      const field = `${this.get('selectedSettingId')}Unit`;
      // power-select passes the whole object, we only want the unit
      this.send('updatePolicyProperty', field, value.unit);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(UsmBeacons);