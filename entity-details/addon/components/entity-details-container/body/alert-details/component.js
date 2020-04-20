import Component from '@ember/component';
import { connect } from 'ember-redux';
import { hasAlerts, alertError } from 'entity-details/reducers/alerts/selectors';

const stateToComputed = (state) => ({
  hasAlerts: hasAlerts(state),
  alertError: alertError(state)
});
const AlertDetailsComponent = Component.extend({
  tagName: ''
});

export default connect(stateToComputed)(AlertDetailsComponent);