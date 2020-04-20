import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getIncidentPositionAndNextIncidentId } from 'entity-details/reducers/indicators/selectors';
import { initializeIndicator } from 'entity-details/actions/indicator-details';

const stateToComputed = (state) => ({
  incidentNavDetails: getIncidentPositionAndNextIncidentId(state)
});

const dispatchToActions = {
  initializeIndicator
};

const IndicatorNavigatorComponent = Component.extend({
  classNames: ['entity-details-container-body-indicator-navigator']
});

export default connect(stateToComputed, dispatchToActions)(IndicatorNavigatorComponent);