import Component from '@ember/component';
import { connect } from 'ember-redux';
import { updatePolicyProperty, removeFromSelectedSettings } from 'admin-source-management/actions/creators/policy-wizard-creators';
import {
  scheduleConfig,
  scanScheduleConfig
} from 'admin-source-management/reducers/usm/policy-wizard-selectors';

const stateToComputed = (state) => ({
  scheduleConfig: scheduleConfig(state),
  scanScheduleConfig: scanScheduleConfig()
});

const dispatchToActions = {
  updatePolicyProperty,
  removeFromSelectedSettings
};

const ScanSchedule = Component.extend({
  tagName: 'box',

  classNames: 'scan-schedule'

});

export default connect(stateToComputed, dispatchToActions)(ScanSchedule);
