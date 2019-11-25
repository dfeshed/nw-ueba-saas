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
  name: policy(state).name,
  description: policy(state).description,
  createdOn: policy(state).createdOn,
  defaultPolicy: policy(state).defaultPolicy,
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

  @computed('defaultPolicy', 'createdOn')
  cannotEditSourceType(defaultPolicy, createdOn) {
    return defaultPolicy || (createdOn > 0);
  },

  @computed('defaultPolicy')
  cannotEditPolicyIdentity(defaultPolicy) {
    if (defaultPolicy) {
      return defaultPolicy;
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
