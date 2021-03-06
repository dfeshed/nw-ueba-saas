import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  startTime
} from 'admin-source-management/reducers/usm/policy-wizard/edrPolicy/edr-selectors';
import {
  updatePolicyProperty
} from 'admin-source-management/actions/creators/policy-wizard-creators';

import { isEmpty } from '@ember/utils';

const stateToComputed = (state) => ({
  startTime: startTime(state)
});

const dispatchToActions = {
  updatePolicyProperty
};

const StartTime = Component.extend({
  tagName: 'box',

  classNames: 'start-time',

  classNameBindings: ['selectedSettingId'],

  isDefaultPolicy: null,

  actions: {
    onTimeChange(selectedDates, dateString, flatpikr) {
      const selectedTime = flatpikr.element.value;
      if (!isEmpty(selectedTime)) {
        this.send('updatePolicyProperty', 'scanStartTime', selectedTime);
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(StartTime);
