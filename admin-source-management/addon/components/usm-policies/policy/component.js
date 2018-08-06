import Component from '@ember/component';
import { connect } from 'ember-redux';
import Notifications from 'component-lib/mixins/notifications';

import {
  isPolicyLoading,
  hasMissingRequiredData,
  currentPolicy
} from 'admin-source-management/reducers/usm/policy-selectors';

import {
  savePolicy,
  editPolicy
} from 'admin-source-management/actions/creators/policy-creators';


const stateToComputed = (state) => ({
  policy: currentPolicy(state),
  isPolicyLoading: isPolicyLoading(state),
  hasMissingRequiredData: hasMissingRequiredData(state)
});

const dispatchToActions = (dispatch) => ({
  // edit the policy using fully qualified field name (e.g., 'policy.name')
  edit(field, value) {
    if (field && value !== undefined) {
      dispatch(editPolicy(field, value));
    }
  },
  // save changes to the policy
  save() {
    const callBackOptions = {
      // if the save is succesful, redirect the user to the policies list route
      onSuccess: () => {
        this.send('success', 'adminUsm.policy.saveSuccess');
        const transitionToPolicies = this.get('transitionToPolicies');
        transitionToPolicies();
      },
      onFailure: () => {
        this.send('failure', 'adminUsm.policy.saveFailure');
      }
    };
    dispatch(savePolicy(this.get('policy'), callBackOptions));
  },
  // cancel changes to the policy
  cancel() {
    const transitionToPolicies = this.get('transitionToPolicies');
    transitionToPolicies();
  }
});

const UsmPolicy = Component.extend(Notifications, {
  tagName: 'hbox',

  classNames: ['usm-policy', 'scroll-box'],

  actions: {
    handleNameChange(value) {
      this.send('edit', 'policy.name', value);
    },
    handleDescriptionChange(value) {
      this.send('edit', 'policy.description', value);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(UsmPolicy);