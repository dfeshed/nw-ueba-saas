import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getAlertsSeverity, hasAlerts, alertListError } from 'investigate-users/reducers/alerts/selectors';
import { exportAlerts } from 'investigate-users/actions/alert-details';

const stateToComputed = (state) => ({
  alertsSeverity: getAlertsSeverity(state),
  hasAlerts: hasAlerts(state),
  alertListError: alertListError(state)
});

const dispatchToActions = {
  exportAlerts
};

const AlertTabBodyComponent = Component.extend({
  classNames: 'alerts-tab_body'
});

export default connect(stateToComputed, dispatchToActions)(AlertTabBodyComponent);