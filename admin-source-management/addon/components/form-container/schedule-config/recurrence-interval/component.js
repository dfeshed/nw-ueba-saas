import Component from '@ember/component';
import { connect } from 'ember-redux';
import { updatePolicyProperty } from 'admin-source-management/actions/data-creators/policy';
import {
  scheduleOptions,
  runIntervalConfig,
  weekOptions,
  isWeeklyInterval,
  radioButtonConfig
} from 'admin-source-management/reducers/policy/selector';

const stateToComputed = (state) => ({
  schedule: scheduleOptions(state),
  radioButtonConfig: radioButtonConfig(),
  runIntervalConfig: runIntervalConfig(state),
  weekOptions: weekOptions(state),
  isWeeklyInterval: isWeeklyInterval(state)
});

const dispatchToActions = {
  updatePolicyProperty
};

const RecInterval = Component.extend({
  tagName: 'box',

  classNames: 'recurrence-interval',

  schedule: null,

  actions: {
    selectWeek(index) {
      this.send('updatePolicyProperty', 'runOnDaysOfWeek', [index]);
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(RecInterval);
