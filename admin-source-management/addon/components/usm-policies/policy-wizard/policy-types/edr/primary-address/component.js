import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  updatePolicyProperty,
  removeFromSelectedSettings
} from 'admin-source-management/actions/creators/policy-wizard-creators';
import {
  endpointServersList,
  selectedEndpointSever,
  primaryAddressValidator
} from 'admin-source-management/reducers/usm/policy-wizard/edrPolicy/edr-selectors';

const stateToComputed = function stateToComputed(state) {
  const [, { selectedSettingId }] = arguments;
  return {
    endpointsList: endpointServersList(state),
    selectedEndpointSever: selectedEndpointSever(state),
    primaryAddressValidator: primaryAddressValidator(state, selectedSettingId)
  };
};

const dispatchToActions = {
  updatePolicyProperty,
  removeFromSelectedSettings
};

const PrimaryAddress = Component.extend({
  tagName: 'box',
  classNames: 'primary-address',
  classNameBindings: ['selectedSettingId'],
  selectedSettingId: null
});

export default connect(stateToComputed, dispatchToActions)(PrimaryAddress);

