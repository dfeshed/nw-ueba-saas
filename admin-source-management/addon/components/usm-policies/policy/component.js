import Component from '@ember/component';
import { connect } from 'ember-redux';
import Notifications from 'component-lib/mixins/notifications';

import {
  isPolicyLoading,
  hasMissingRequiredData,
  currentPolicy,
  enabledAvailableSettings,
  sortedSelectedSettings
} from 'admin-source-management/reducers/usm/policy-selectors';

import {
  savePolicy,
  editPolicy,
  addToSelectedSettings
} from 'admin-source-management/actions/creators/policy-creators';


const stateToComputed = (state) => ({
  policy: currentPolicy(state),
  isPolicyLoading: isPolicyLoading(state),
  hasMissingRequiredData: hasMissingRequiredData(state),
  enabledAvailableSettings: enabledAvailableSettings(state),
  sortedSelectedSettings: sortedSelectedSettings(state)
});

const dispatchToActions = {
  addToSelectedSettings,
  editPolicy,
  savePolicy
};

const UsmPolicy = Component.extend(Notifications, {
  tagName: 'hbox',

  classNames: ['usm-policy', 'scroll-box'],

  edit(field, value) {
    if (field && value !== undefined) {
      this.send('editPolicy', field, value);
    }
  },

  actions: {
    handleNameChange(value) {
      this.edit('policy.name', value);
    },
    handleDescriptionChange(value) {
      this.edit('policy.description', value);
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
      this.send('savePolicy', this.get('policy'), callBackOptions);
    },
    // cancel changes to the policy
    cancel() {
      const transitionToPolicies = this.get('transitionToPolicies');
      transitionToPolicies();
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(UsmPolicy);
