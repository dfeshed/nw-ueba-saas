import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  updatePolicyProperty,
  removeFromSelectedSettings
} from 'admin-source-management/actions/creators/policy-wizard-creators';
import {
  endpointServersList,
  selectedEndpointSever
} from 'admin-source-management/reducers/usm/policy-wizard-selectors';

const stateToComputed = (state) => ({
  endpointsList: endpointServersList(state),
  selectedEndpointSever: selectedEndpointSever(state)
});

const dispatchToActions = {
  updatePolicyProperty,
  removeFromSelectedSettings
};

const PrimaryAddress = Component.extend({
  tagName: 'box',
  classNames: 'primary-address'
});

export default connect(stateToComputed, dispatchToActions)(PrimaryAddress);

