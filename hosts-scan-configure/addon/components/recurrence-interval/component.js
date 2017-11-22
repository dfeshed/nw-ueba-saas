import Component from 'ember-component';
import layout from './template';
import { connect } from 'ember-redux';
import { updateScheduleProperty } from 'hosts-scan-configure/actions/data-creators';
import { scheduleData, runIntervalConfig, weekOptions, isWeeklyInterval } from 'hosts-scan-configure/reducers/schedule/selectors';

const RADIO_BUTTONS_CONFIG = {
  name: 'recurrence',
  label: 'hostsScanConfigure.recurrenceInterval.title',
  type: 'radioGroup',
  items: [
    {
      name: 'DAYS',
      label: 'hostsScanConfigure.recurrenceInterval.options.daily'
    },
    {
      name: 'WEEKS',
      label: 'hostsScanConfigure.recurrenceInterval.options.weekly'
    }
  ]
};

const stateToComputed = (state) => ({
  schedule: scheduleData(state),
  runIntervalConfig: runIntervalConfig(state),
  weekOptions: weekOptions(state),
  isWeeklyInterval: isWeeklyInterval(state)
});

const dispatchToActions = {
  updateScheduleProperty
};

const RecInterval = Component.extend({
  layout,

  tagName: 'box',

  classNames: 'recurrence-interval',

  radioButtonConfig: RADIO_BUTTONS_CONFIG,

  schedule: null,

  actions: {
    selectWeek(index) {
      this.send('updateScheduleProperty', 'runOnDays', [index]);
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(RecInterval);
