import Component from '@ember/component';
import { connect } from 'ember-redux';

import {
  policy,
  sourceTypes,
  selectedSourceType,
  nameValidator
} from 'admin-source-management/reducers/usm/policy-wizard-selectors';

import {
  editPolicy
} from 'admin-source-management/actions/creators/policy-wizard-creators';

const stateToComputed = (state) => ({
  policy: policy(state),
  sourceTypes: sourceTypes(state),
  selectedSourceType: selectedSourceType(state),
  nameValidator: nameValidator(state)
});

const dispatchToActions = {
  editPolicy
};

const IdentifyPolicyStep = Component.extend({
  tagName: 'vbox',
  classNames: ['identify-policy-step', 'scroll-box', 'rsa-wizard-step'],

  // edit the policy using fully qualified field name (e.g., 'policy.name')
  edit(field, value) {
    if (field && value !== undefined) {
      this.send('editPolicy', field, value);
    }
  },

  actions: {
    handleSourceTypeChange(value) {
      // power-select passes the whole object, we only want the type
      this.edit('policy.type', value.type);
    },
    handleNameChange(value) {
      this.edit('policy.name', value.trim());
    },
    handleDescriptionChange(value) {
      this.edit('policy.description', value.trim());
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(IdentifyPolicyStep);
