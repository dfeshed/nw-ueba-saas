import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getSelectedAlertData } from 'entity-details/reducers/alerts/selectors';
import { getIncidentData } from 'entity-details/reducers/indicators/selectors';
import { entityDisplayName } from 'entity-details/reducers/entity/selectors';

const stateToComputed = (state) => ({
  entityDisplayName: entityDisplayName(state),
  alertDetails: getSelectedAlertData(state),
  incidentDetails: getIncidentData(state)
});

const IndicatorDetailsDetailsComponent = Component.extend({
  classNames: ['entity-details-container-body-indicator-details_details']
});

export default connect(stateToComputed)(IndicatorDetailsDetailsComponent);