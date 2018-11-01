import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  updatePolicyProperty,
  removeFromSelectedSettings
} from 'admin-source-management/actions/creators/policy-wizard-creators';
import {
  protocolsList,
  selectedProtocol
} from 'admin-source-management/reducers/usm/policy-wizard/windowsLogPolicy/windowsLog-selectors';

const stateToComputed = (state) => ({
  protocolsList,
  selectedProtocol: selectedProtocol(state)
});

const dispatchToActions = {
  updatePolicyProperty,
  removeFromSelectedSettings
};

const WindowsLogProtocol = Component.extend({
  tagName: 'box',
  classNames: 'windows-log-protocol'
});

export default connect(stateToComputed, dispatchToActions)(WindowsLogProtocol);

