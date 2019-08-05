import Component from '@ember/component';
import { connect } from 'ember-redux';
import { scheduleOnce } from '@ember/runloop';

import {
  enabledAvailableSettings,
  sortedSelectedSettings,
  policy
} from 'admin-source-management/reducers/usm/policy-wizard/policy-wizard-selectors';

import {
  addToSelectedSettings,
  removeFromSelectedSettings
} from 'admin-source-management/actions/creators/policy-wizard-creators';


const stateToComputed = (state) => ({
  enabledAvailableSettings: enabledAvailableSettings(state),
  sortedSelectedSettings: sortedSelectedSettings(state),
  policyType: policy(state).policyType,
  defaultPolicy: policy(state).defaultPolicy
});

const dispatchToActions = {
  addToSelectedSettings,
  removeFromSelectedSettings
};

const DefinePolicyStep = Component.extend({
  tagName: 'vbox',
  classNames: ['define-policy-step', 'rsa-wizard-step'],
  selectedSettingId: null,

  _scrollToSelectedSetting() {
    const id = this.get('scrollToSelectedSetting');
    this.get('element').querySelector(`.${id}`).scrollIntoView(false);
  },
  // step object required to be passed in
  // step: null, // the wizard passes this in but we're not using it (yet anyway) - uncomment if/when needed

  actions: {
    handleAddToSelectedSettings(id) {
      // action will work on click, Enter or Space
      if (event.type === 'click' || event.key === 'Enter' || event.key === ' ') {
        this.set('scrollToSelectedSetting', id);
        this.send('addToSelectedSettings', id);
        scheduleOnce('afterRender', this, '_scrollToSelectedSetting');
      }
    },
    handleRemoveFromSelectedSettings(id) {
      // action will work on click, Enter or Space
      if (event.type === 'click' || event.key === 'Enter' || event.key === ' ') {
        this.get('scrollToSelectedSetting', id);
        this.send('removeFromSelectedSettings', id);
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(DefinePolicyStep);
