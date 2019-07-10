import Component from '@ember/component';
import { connect } from 'ember-redux';
import { alertSources, userScoreContribution, getSelectedAlertData } from 'entity-details/reducers/alerts/selectors';

const stateToComputed = (state) => ({
  alertSources: alertSources(state),
  alertDetails: getSelectedAlertData(state),
  userScoreContribution: userScoreContribution(state)
});

const AlertDetailsDetailsComponent = Component.extend({
  classNames: ['entity-details-container-body-alert-details_details']
});

export default connect(stateToComputed)(AlertDetailsDetailsComponent);