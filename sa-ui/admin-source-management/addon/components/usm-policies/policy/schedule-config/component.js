import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  isEnabled,
  startDate,
  startTime
} from 'admin-source-management/reducers/usm/policy-selectors';

import {
  updatePolicyProperty,
  saveScheduleConfig
} from 'admin-source-management/actions/creators/policy-creators';

import { isEmpty } from '@ember/utils';

const stateToComputed = (state) => ({
  isEnabled: isEnabled(state),
  startDate: startDate(state),
  startTime: startTime(state),
  config: state.usm.policy.scheduleConfig
});

const dispatchToActions = {
  updatePolicyProperty,
  saveScheduleConfig
};

const Form = Component.extend({
  tagName: 'hbox',

  classNames: 'schedule-config',

  actions: {
    toggleEnable() {
      this.send('updatePolicyProperty', 'enabledScheduledScan', !this.get('isEnabled'));
    },

    onDateChange(selectedDates) {
      if (!isEmpty(selectedDates[0])) {
        this.send('updatePolicyProperty', 'scanStartDate', selectedDates[0].getTime());
      }
    },
    onTimeChange(selectedDates, dateString, flatpikr) {
      const selectedTime = flatpikr.element.value;
      if (!isEmpty(selectedTime)) {
        this.send('updatePolicyProperty', 'scanStartTime', selectedTime);
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(Form);
