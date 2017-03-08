import Ember from 'ember';
import * as ACTION_TYPES from 'respond/actions/types';
import { incidentDetails, storyline } from '../server/data';

const { run, RSVP } = Ember;

// Dispatches a given redux action, wrapping it in Ember.run.
function _dispatchAction(redux, action) {
  run(() => {
    redux.dispatch(action);
  });
}

// Dispatches a given redux action type and simulates an async response with the given payload.
// Both the action dispatch and the promise resolve will be wrapped in Ember.run. Just to be safe!
function _dispatchActionWithPromisePayload(redux, type, payload) {
  const promise = new RSVP.Promise(function(resolve) {
    run(() => {
      resolve(payload);
    });
  });

  _dispatchAction(redux, { type, promise });
}

class DataHelper {
  constructor(redux) {
    this.redux = redux;
  }
  initializeIncident(incidentId) {
    _dispatchAction(this.redux, {
      type: ACTION_TYPES.INITIALIZE_INCIDENT,
      incidentId
    });
  }
  fetchIncidentDetails(data = incidentDetails) {
    _dispatchActionWithPromisePayload(
      this.redux,
      ACTION_TYPES.FETCH_INCIDENT_DETAILS,
      { code: 0, data }
    );
  }
  fetchIncidentStoryline(data = storyline) {
    _dispatchActionWithPromisePayload(
      this.redux,
      ACTION_TYPES.FETCH_INCIDENT_STORYLINE,
      { code: 0, data }
    );
  }
  toggleIncidentJournalPanel() {
    _dispatchAction(this.redux, {
      type: ACTION_TYPES.TOGGLE_JOURNAL_PANEL
    });
  }
}

export default DataHelper;
