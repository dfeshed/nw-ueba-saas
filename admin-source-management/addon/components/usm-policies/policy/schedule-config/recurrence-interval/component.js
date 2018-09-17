import Component from '@ember/component';
import { connect } from 'ember-redux';
import { updatePolicyProperty, removeFromSelectedSettings } from 'admin-source-management/actions/creators/policy-wizard-creators';
import {
  interval,
  intervalType,
  runIntervalConfig,
  weekOptions,
  isWeeklyInterval,
  radioButtonConfig
} from 'admin-source-management/reducers/usm/policy-wizard-selectors';

const stateToComputed = (state) => ({
  interval: interval(state),
  intervalType: intervalType(state),
  radioButtonConfig: radioButtonConfig(),
  runIntervalConfig: runIntervalConfig(state),
  weekOptions: weekOptions(state),
  isWeeklyInterval: isWeeklyInterval(state)
});

const dispatchToActions = {
  updatePolicyProperty,
  removeFromSelectedSettings
};

const RecInterval = Component.extend({
  tagName: 'box',

  classNames: 'recurrence-interval',

  actions: {
    selectWeek(index) {
      this.send('updatePolicyProperty', 'runOnDaysOfWeek', [index]);
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(RecInterval);
