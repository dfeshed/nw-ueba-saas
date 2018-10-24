import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';

import {
  policy,
  sourceTypes,
  selectedSourceType,
  nameValidator,
  descriptionValidator
} from 'admin-source-management/reducers/usm/policy-wizard/policy-wizard-selectors';

import {
  updatePolicyType,
  updatePolicyProperty
} from 'admin-source-management/actions/creators/policy-wizard-creators';

const stateToComputed = (state) => ({
  sourceTypes: sourceTypes(state),
  selectedSourceType: selectedSourceType(state),
  policyName: policy(state).name,
  policyDescription: policy(state).description,
  nameValidator: nameValidator(state),
  descriptionValidator: descriptionValidator(state)
});

const dispatchToActions = {
  updatePolicyType,
  updatePolicyProperty
};

const IdentifyPolicyStep = Component.extend({
  tagName: 'vbox',
  classNames: ['identify-policy-step', 'scroll-box', 'rsa-wizard-step'],

  // Computed properties for name and description
  // This handles the issue with focus and loss of first key input
  // Couldn't find any other binding type that worked correctly
  @computed('policyName')
  name(policyName) {
    if (policyName) {
      return policyName;
    }
  },

  @computed('policyDescription')
  description(policyDescription) {
    if (policyDescription) {
      return policyDescription;
    }
  },

  // step object required to be passed in
  // step: null, // the wizard passes this in but we're not using it (yet anyway) - uncomment if/when needed

  actions: {
    handleSourceTypeChange(value) {
      // power-select passes the whole object, we only want the policy type
      this.send('updatePolicyType', value.policyType);
    },
    handleNameChange(value) {
      this.send('updatePolicyProperty', 'name', value.trim());
    },
    handleDescriptionChange(value) {
      this.send('updatePolicyProperty', 'description', value.trim());
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(IdentifyPolicyStep);
