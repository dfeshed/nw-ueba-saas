import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  updatePolicyProperty,
  removeFromSelectedSettings
} from 'admin-source-management/actions/creators/policy-wizard-creators';
import {
  primaryLogServersList,
  secondaryLogServersList,
  selectedPrimaryLogServer,
  selectedSecondaryLogServer,
  windowsLogDestinationValidator
} from 'admin-source-management/reducers/usm/policy-wizard/windowsLogPolicy/windowsLog-selectors';

const stateToComputed = function stateToComputed(state) {
  const [, { selectedSettingId }] = arguments;
  if (selectedSettingId === 'primaryDestination') {
    return {
      logServersList: primaryLogServersList(state),
      selectedLogServer: selectedPrimaryLogServer(state),
      windowsLogDestinationValidator: windowsLogDestinationValidator(state, selectedSettingId)
    };
  } else {
    return {
      logServersList: secondaryLogServersList(state),
      selectedLogServer: selectedSecondaryLogServer(state),
      windowsLogDestinationValidator: windowsLogDestinationValidator(state, selectedSettingId)
    };
  }
};

const dispatchToActions = {
  updatePolicyProperty,
  removeFromSelectedSettings
};

const WindowsLogDestinations = Component.extend({
  tagName: 'box',
  classNames: 'windows-log-destinations',
  classNameBindings: ['selectedSettingId'],
  selectedSettingId: null
});

export default connect(stateToComputed, dispatchToActions)(WindowsLogDestinations);

