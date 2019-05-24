import Incidents from '../api/add-to-incident';
import * as ACTION_TYPES from '../types';

/**
 * Action creator for searching incidents. The search terms must have a character length greater than 3 characters,
 * otherwise no search is performed and any previous results are cleared out
 * @public
 * @returns function
 */
const searchIncidents = () => {
  return (dispatch, getState) => {
    const { incidentSearchText, incidentSearchSortBy, incidentSearchSortIsDescending } = getState().respondShared.incidentSearchParams;

    if (incidentSearchText && incidentSearchText.length >= 3) { // only search if there are three or more characters in the search text
      dispatch({ type: ACTION_TYPES.SEARCH_INCIDENTS_STARTED });
      Incidents.search(incidentSearchText, incidentSearchSortBy, incidentSearchSortIsDescending, {
        onInit: (stopStreamFn) => {
          dispatch({ type: ACTION_TYPES.SEARCH_INCIDENTS_STREAM_INITIALIZED, payload: stopStreamFn });
        },
        onCompleted: () => {
          return dispatch({ type: ACTION_TYPES.SEARCH_INCIDENTS_COMPLETED });
        },
        onResponse: (payload) => {
          return dispatch({ type: ACTION_TYPES.SEARCH_INCIDENTS_RETRIEVE_BATCH, payload });
        },
        onError: () => {
          dispatch({ type: ACTION_TYPES.SEARCH_INCIDENTS_ERROR });
        }
      });
    } else { // otherwise, clear out the results
      dispatch(clearSearchIncidentsResults());
    }
  };
};

const updateSearchIncidentsText = (searchText) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.UPDATE_SEARCH_INCIDENTS_TEXT,
      payload: searchText
    });
    dispatch(searchIncidents());
  };
};

const updateSearchIncidentsSortBy = (sortField, isSortDescending) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.UPDATE_SEARCH_INCIDENTS_SORTBY,
      payload: { sortField, isSortDescending }
    });
    dispatch(searchIncidents());
  };
};

const selectIncident = (incident) => ({
  type: ACTION_TYPES.SEARCH_INCIDENTS_SELECT,
  payload: incident
});

const clearSearchIncidentsResults = () => ({
  type: ACTION_TYPES.CLEAR_SEARCH_INCIDENTS_RESULTS
});


/**
 * Adds a given list of indicators (alerts) to an incident with a given ID.
 * @param data - all the request params to br sent to Investgate Server
 * @param callbacks { onSuccess, onFailure }
 * @public
 */
const addEventsToIncident = (data, callbacks) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.EVENTS_ADD_TO_INCIDENT,
      promise: Incidents.addEventsToIncident(data),
      meta: {
        onSuccess: (response) => {
          callbacks.onSuccess(response);
        },
        onFailure: (response) => {
          callbacks.onFailure(response);
        }
      }
    });
  };
};

/**
 * Adds a given list of indicators (alerts) to an incident with a given ID.
 * @param indicators array of alert IDs
 * @param incidentId ID for incident to associate alerts with
 * @param callbacks { onSuccess, onFailure }
 * @public
 */
const addAlertsToIncident = (indicators, incidentId, callbacks) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.ALERTS_ADD_TO_INCIDENT,
      promise: Incidents.addAlertsToIncident(indicators, incidentId),
      meta: {
        onSuccess: (response) => {
          callbacks.onSuccess(response);
          dispatch({ type: ACTION_TYPES.UPDATE_INCIDENT_ON_ADD, payload: response });
        },
        onFailure: (response) => {
          callbacks.onFailure(response);
        }
      }
    });
  };
};

export {
  clearSearchIncidentsResults,
  searchIncidents,
  selectIncident,
  addEventsToIncident,
  updateSearchIncidentsText,
  updateSearchIncidentsSortBy,
  addAlertsToIncident
};