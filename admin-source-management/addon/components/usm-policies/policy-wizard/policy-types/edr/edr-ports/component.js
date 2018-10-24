import Component from '@ember/component';
import { connect } from 'ember-redux';
import { updatePolicyProperty, removeFromSelectedSettings } from 'admin-source-management/actions/creators/policy-wizard-creators';
import {
  portValue,
  isPortValid
} from 'admin-source-management/reducers/usm/policy-wizard/edrPolicy/edr-selectors';

const stateToComputed = function stateToComputed(state) {
  const [, { selectedSettingId }] = arguments;
  return {
    portValue: portValue(state, selectedSettingId),
    isPortValid: isPortValid(state, selectedSettingId)
  };
};

const dispatchToActions = {
  updatePolicyProperty,
  removeFromSelectedSettings
};

const UsmPorts = Component.extend({
  tagName: 'box',

  classNames: 'usm-ports',

  classNameBindings: ['selectedSettingId'],

  selectedSettingId: null,
  actions: {
    handlePortValueChange(value) {
      const field = this.get('selectedSettingId');
      value = isNaN(parseInt(value, 10)) ? '' : parseInt(value, 10);
      this.send('updatePolicyProperty', field, value);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(UsmPorts);