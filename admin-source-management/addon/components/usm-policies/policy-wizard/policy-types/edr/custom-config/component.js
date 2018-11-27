import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  updatePolicyProperty,
  removeFromSelectedSettings
} from 'admin-source-management/actions/creators/policy-wizard-creators';
import {
  customConfig,
  customConfigValidator
} from 'admin-source-management/reducers/usm/policy-wizard/edrPolicy/edr-selectors';

function stateToComputed(state) {
  const [, { selectedSettingId }] = arguments;
  return {
    customConfig: customConfig(state, selectedSettingId),
    customConfigValidator: customConfigValidator(state, selectedSettingId)
  };
}

const dispatchToActions = {
  updatePolicyProperty,
  removeFromSelectedSettings
};
const CustomConfig = Component.extend({
  tagName: 'box',
  classNames: 'custom-config',
  classNameBindings: ['selectedSettingId'],

  actions: {
    handleCustomSettingChange(value) {
      this.send('updatePolicyProperty', 'customConfig', value.trim());
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(CustomConfig);