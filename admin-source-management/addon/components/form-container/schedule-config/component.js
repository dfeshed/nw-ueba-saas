import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import {
  isEnabled,
  startDate,
  startTime
} from 'admin-source-management/reducers/policy/selector';

import {
  updatePolicyProperty,
  saveScheduleConfig
} from 'admin-source-management/actions/data-creators/policy';

import { isEmpty } from '@ember/utils';

const stateToComputed = (state) => ({
  enabled: isEnabled(state),
  startDate: startDate(state),
  startTime: startTime(state),
  config: state.policy.scheduleConfig
});

const dispatchToActions = {
  updatePolicyProperty,
  saveScheduleConfig
};

const Form = Component.extend({
  layout,

  tagName: 'hbox',

  classNames: 'schedule-config',

  isDirty: false,

  errorMessage: 'adminUsm.policy.scheduleConfiguration.error.generic',

  actions: {
    toggleEnable() {
      this.send('updatePolicyProperty', 'enabledScheduledScan', !this.get('enabled'));
    },

    onDateChange(selectedDates, dateString) {
      if (!isEmpty(dateString)) {
        this.send('updatePolicyProperty', 'scanStartDate', new Date(dateString).getTime());
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
