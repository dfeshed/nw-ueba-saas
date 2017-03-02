import Ember from 'ember';
import * as ACTION_TYPES from 'respond/actions/types';
import { incidentDetails } from '../server/data';

const { run, RSVP } = Ember;

function _dispatchAction(redux, action) {
  run(() => {
    redux.dispatch(action);
  });
}

function _dispatchFetchIncidentDetails(redux, payload) {
  const promise = new RSVP.Promise(function(resolve) {
    resolve(payload);
  });
  _dispatchAction(redux, { type: ACTION_TYPES.FETCH_INCIDENT_DETAILS, promise });
}


class DataHelper {
  constructor(redux) {
    this.redux = redux;
  }
  fetchIncidentDetails(payload = incidentDetails) {
    _dispatchFetchIncidentDetails(this.redux, payload);
  }
  initializeIncident(incidentId) {
    _dispatchAction(this.redux, {
      type: ACTION_TYPES.INITIALIZE_INCIDENT,
      incidentId
    });
  }
  toggleIncidentEntitiesPanel() {
    _dispatchAction(this.redux, {
      type: ACTION_TYPES.TOGGLE_ENTITIES_PANEL
    });
  }
  toggleIncidentEventsPanel() {
    _dispatchAction(this.redux, {
      type: ACTION_TYPES.TOGGLE_EVENTS_PANEL
    });
  }
  toggleIncidentJournalPanel() {
    _dispatchAction(this.redux, {
      type: ACTION_TYPES.TOGGLE_JOURNAL_PANEL
    });
  }
}

export default DataHelper;
