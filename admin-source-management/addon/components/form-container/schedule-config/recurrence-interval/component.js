import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import { updatePolicyProperty } from 'admin-source-management/actions/data-creators/policy';
import {
  scheduleOptions,
  runIntervalConfig,
  weekOptions,
  isWeeklyInterval
} from 'admin-source-management/reducers/policy/selector';

const RADIO_BUTTONS_CONFIG = {
  name: 'recurrence',
  label: 'adminUsm.policy.scheduleConfiguration.recurrenceInterval.title',
  type: 'radioGroup',
  items: [
    {
      name: 'DAYS',
      label: 'adminUsm.policy.scheduleConfiguration.recurrenceInterval.options.daily'
    },
    {
      name: 'WEEKS',
      label: 'adminUsm.policy.scheduleConfiguration.recurrenceInterval.options.weekly'
    }
  ]
};

const stateToComputed = (state) => ({
  schedule: scheduleOptions(state),
  runIntervalConfig: runIntervalConfig(state),
  weekOptions: weekOptions(state),
  isWeeklyInterval: isWeeklyInterval(state)
});

const dispatchToActions = {
  updatePolicyProperty
};

const RecInterval = Component.extend({
  layout,

  tagName: 'box',

  classNames: 'recurrence-interval',

  radioButtonConfig: RADIO_BUTTONS_CONFIG,

  schedule: null,

  actions: {
    selectWeek(index) {
      this.send('updatePolicyProperty', 'runOnDaysOfWeek', [index]);
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(RecInterval);
