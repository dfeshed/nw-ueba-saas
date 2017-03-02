import Ember from 'ember';
import * as ACTION_TYPES from 'respond/actions/types';
import { incidentDetails } from '../server/data';

const { run, RSVP } = Ember;

function _dispatchFetchIncidentDetails(redux, payload) {
  const promise = new RSVP.Promise(function(resolve) {
    resolve(payload);
  });
  run(() => {
    redux.dispatch({ type: ACTION_TYPES.FETCH_INCIDENT_DETAILS, promise });
  });
}

class DataHelper {
  constructor(redux) {
    this.redux = redux;
  }
  fetchIncidentDetails(payload = incidentDetails) {
    _dispatchFetchIncidentDetails(this.redux, payload);
  }
  initializeIncident(incidentId) {
    this.redux.dispatch({
      type: ACTION_TYPES.INITIALIZE_INCIDENT,
      incidentId
    });
  }
  toggleIncidentEntitiesPanel() {
    this.redux.dispatch({
      type: ACTION_TYPES.TOGGLE_ENTITIES_PANEL
    });
  }
  toggleIncidentEventsPanel() {
    this.redux.dispatch({
      type: ACTION_TYPES.TOGGLE_EVENTS_PANEL
    });
  }
  toggleIncidentJournalPanel() {
    this.redux.dispatch({
      type: ACTION_TYPES.TOGGLE_JOURNAL_PANEL
    });
  }
}

export default DataHelper;
