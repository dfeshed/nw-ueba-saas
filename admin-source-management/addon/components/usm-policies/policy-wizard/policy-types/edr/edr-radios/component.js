import Component from '@ember/component';
import { connect } from 'ember-redux';
import { updatePolicyProperty, removeFromSelectedSettings } from 'admin-source-management/actions/creators/policy-wizard-creators';
import {
  radioButtonValue,
  radioButtonOption
} from 'admin-source-management/reducers/usm/policy-wizard/edrPolicy/edr-selectors';

const stateToComputed = function stateToComputed(state) {
  const [, { selectedSettingId }] = arguments;
  return {
    radioButtonValue: radioButtonValue(state, selectedSettingId),
    radioButtonOption: radioButtonOption(selectedSettingId)
  };
};

const dispatchToActions = {
  updatePolicyProperty,
  removeFromSelectedSettings
};

const UsmRadios = Component.extend({
  tagName: 'box',

  classNames: 'usm-radios',

  classNameBindings: ['selectedSettingId'],

  selectedSettingId: null

});

export default connect(stateToComputed, dispatchToActions)(UsmRadios);