import Component from '@ember/component';
import { connect } from 'ember-redux';
import { updatePolicyProperty } from 'admin-source-management/actions/creators/policy-wizard-creators';

import {
  selectedDropdownOption,
  listOfDropdownValues
} from 'admin-source-management/reducers/usm/policy-wizard/edrPolicy/edr-selectors';

const stateToComputed = function stateToComputed(state) {
  const [, { selectedSettingId }] = arguments;
  return {
    selectedOption: selectedDropdownOption(state, selectedSettingId),
    listOfValues: listOfDropdownValues(state, selectedSettingId)
  };
};

const dispatchToActions = {
  updatePolicyProperty
};

const SignatureDropdown = Component.extend({
  tagName: 'box',
  classNames: ['signature-dropdown'],
  classNameBindings: ['selectedSettingId'],
  selectedSettingId: null,

  actions: {
    updateDropdownSelection(selectedSettingId, { value }) {
      this.send('updatePolicyProperty', selectedSettingId, value);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(SignatureDropdown);
