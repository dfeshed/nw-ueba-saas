import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getAlertsSeverity } from 'investigate-users/reducers/alerts/selectors';

const stateToComputed = (state) => ({
  alertsSeverity: getAlertsSeverity(state)
});

const AlertTabBodyComponent = Component.extend({
  classNames: 'alerts-tab_body'
});

export default connect(stateToComputed)(AlertTabBodyComponent);
