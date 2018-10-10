import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getTopAlerts } from 'investigate-users/reducers/alerts/selectors';

const stateToComputed = (state) => ({
  topAlerts: getTopAlerts(state)
});

const OverviewAlertComponent = Component.extend({
});

export default connect(stateToComputed)(OverviewAlertComponent);