import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  updatePolicyProperty,
  removeFromSelectedSettings
} from 'admin-source-management/actions/creators/policy-wizard-creators';
import {
  logServersList,
  selectedLogServer,
  primaryDestinationValidator
} from 'admin-source-management/reducers/usm/policy-wizard/windowsLogPolicy/windowsLog-selectors';

const stateToComputed = function stateToComputed(state) {
  const [, { selectedSettingId }] = arguments;
  return {
    logServersList: logServersList(state),
    selectedLogServer: selectedLogServer(state),
    primaryDestinationValidator: primaryDestinationValidator(state, selectedSettingId)
  };
};

const dispatchToActions = {
  updatePolicyProperty,
  removeFromSelectedSettings
};

const PrimaryDestination = Component.extend({
  tagName: 'box',
  classNames: 'primary-destination',
  classNameBindings: ['selectedSettingId'],
  selectedSettingId: null
});

export default connect(stateToComputed, dispatchToActions)(PrimaryDestination);

