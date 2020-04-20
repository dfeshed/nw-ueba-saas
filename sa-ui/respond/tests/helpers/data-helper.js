import { run } from '@ember/runloop';
import RSVP from 'rsvp';
import * as ACTION_TYPES from 'respond/actions/types';
import { incidentDetails, storyline, events } from '../data/data';

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
  initializeIncidentSelection(payload) {
    _dispatchAction(this.redux, {
      type: ACTION_TYPES.SET_INCIDENT_SELECTION,
      payload
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
    _dispatchAction(
      this.redux,
      { type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_STARTED }
    );
    _dispatchAction(
      this.redux,
      {
        type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_RETRIEVE_BATCH,
        payload: {
          code: 0,
          data,
          meta: { complete: true }
        }
      }
    );
    _dispatchAction(
      this.redux,
      { type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_EVENTS_STREAM_INITIALIZED }
    );
    _dispatchAction(
      this.redux,
      { type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_EVENTS_REQUEST_BATCH }
    );
    const [ firstIndicator ] = data;
    _dispatchAction(
      this.redux,
      {
        type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_EVENTS_RETRIEVE_BATCH,
        payload: {
          indicatorId: firstIndicator.id,
          events
        }
      }
    );
    _dispatchAction(
      this.redux,
      { type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_EVENTS_COMPLETED }
    );
    _dispatchAction(
      this.redux,
      { type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_COMPLETED }
    );
  }
  toggleIncidentJournalPanel() {
    _dispatchAction(this.redux, {
      type: ACTION_TYPES.TOGGLE_JOURNAL_PANEL
    });
  }
}

export default DataHelper;
