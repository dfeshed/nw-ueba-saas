import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getTopAlerts, hasTopAlerts, topAlertsError } from 'investigate-users/reducers/alerts/selectors';

const stateToComputed = (state) => ({
  topAlerts: getTopAlerts(state),
  topAlertsError: topAlertsError(state),
  hasTopAlerts: hasTopAlerts(state)
});

const OverviewAlertComponent = Component.extend({
});

export default connect(stateToComputed)(OverviewAlertComponent);