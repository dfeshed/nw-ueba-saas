import Component from 'ember-component';
import layout from './template';
import connect from 'ember-redux/components/connect';
import * as ACTION_TYPES from 'respond/actions/types';

import {
  setDefaultSearchTimeFrameName,
  setDefaultSearchEntityType,
  startSearchRelatedIndicators,
  stopSearchRelatedIndicators,
  addRelatedIndicatorsToIncident,
  getStorylineEvents
} from 'respond/actions/creators/incidents-creators';

const stateToComputed = ({
  respond: {
    incident: {
      id: incidentId,
      defaultSearchTimeFrameName,
      defaultSearchEntityType,
      searchEntity,
      searchTimeFrameName,
      searchDevices,
      searchStatus,
      searchResults
    }
  }
}) => ({
  incidentId,
  defaultSearchTimeFrameName,
  defaultSearchEntityType,
  searchEntity,
  searchTimeFrameName,
  searchDevices,
  searchStatus,
  searchResults
});

const dispatchToActions = (dispatch) => ({
  onChangeTimeFrame(timeFrame) {
    dispatch(setDefaultSearchTimeFrameName(timeFrame && timeFrame.name));
  },
  onChangeEntityType(entityType) {
    dispatch(setDefaultSearchEntityType(entityType && entityType.name));
  },
  onSubmit() {
    dispatch(startSearchRelatedIndicators(...arguments));
  },
  onCancel() {
    dispatch(stopSearchRelatedIndicators());
  },
  addToIncident(indicator) {
    const incidentId = this.get('incidentId');
    dispatch(
      addRelatedIndicatorsToIncident(
        [ indicator ],
        incidentId,
        {
          onSuccess: () => {
            // Kick off the loading of the events for these newly added indicators.
            dispatch({ type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_EVENTS_STREAM_INITIALIZED });
            dispatch(getStorylineEvents(incidentId));
          },
          onFailure: () => {
            // TODO Show error message in UI
          }
        }
      )
    );
  }
});

const IncidentAlertsSearch = Component.extend({
  tagName: '',
  layout
});

export default connect(stateToComputed, dispatchToActions)(IncidentAlertsSearch);
