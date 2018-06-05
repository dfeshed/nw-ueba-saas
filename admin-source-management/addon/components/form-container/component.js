import Component from '@ember/component';
import { connect } from 'ember-redux';
import Notifications from 'component-lib/mixins/notifications';

import {
  isPolicyLoading,
  hasMissingRequiredData,
  currentPolicy
} from 'admin-source-management/reducers/policy/selector';

import {
  savePolicy,
  editPolicy
} from 'admin-source-management/actions/data-creators/policy';


const stateToComputed = (state) => ({
  policy: currentPolicy(state),
  isPolicyLoading: isPolicyLoading(state),
  hasMissingRequiredData: hasMissingRequiredData(state)
});

const dispatchToActions = (dispatch) => ({
  edit(field, value) {
    if (field && value !== undefined) {
      dispatch(editPolicy(field, value));
    }
  },
  save() {
    const callBackOptions = {
      onSuccess: () => {
        this.send('success', 'adminUsm.policy.scheduleConfiguration.saveSuccess');
        const transitionToPolicies = this.get('transitionToPolicies');
        transitionToPolicies();
      },
      onFailure: () => {
        this.send('failure', 'adminUsm.policy.scheduleConfiguration.error.generic');
      }
    };
    dispatch(savePolicy(this.get('policy'), callBackOptions));
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