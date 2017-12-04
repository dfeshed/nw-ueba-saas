import { Incidents } from '../api';
import * as ACTION_TYPES from '../types';

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
        },
        onFailure: (response) => {
          callbacks.onFailure(response);
        }
      }
    });
  };
};

/**
 * Action creator for searching incidents. The search terms must have a character length greater than 3 characters,
 * otherwise no search is performed and any previous results are cleared out
 * @public
 * @returns function
 */
const searchIncidents = () => {
  return (dispatch, getState) => {
    const { incidentSearchText, incidentSearchSortBy, incidentSearchSortIsDescending } = getState().respond.alertIncidentAssociation;

    if (incidentSearchText && incidentSearchText.length >= 3) { // only search if there are three or more characters in the search text
      dispatch({ type: ACTION_TYPES.ALERTS_SEARCH_INCIDENTS_STARTED });
      Incidents.search(incidentSearchText, incidentSearchSortBy, incidentSearchSortIsDescending, {
        onInit: (stopStreamFn) => {
          dispatch({ type: ACTION_TYPES.ALERTS_SEARCH_INCIDENTS_STREAM_INITIALIZED, payload: stopStreamFn });
        },
        onCompleted: () => dispatch({ type: ACTION_TYPES.ALERTS_SEARCH_INCIDENTS_COMPLETED }),
        onResponse: (payload) => dispatch({ type: ACTION_TYPES.ALERTS_SEARCH_INCIDENTS_RETRIEVE_BATCH, payload }),
        onError: () => {
          dispatch({ type: ACTION_TYPES.ALERTS_SEARCH_INCIDENTS_ERROR });
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
      type: ACTION_TYPES.ALERTS_UPDATE_SEARCH_INCIDENTS_TEXT,
      payload: searchText
    });
    dispatch(searchIncidents());
  };
};

const updateSearchIncidentsSortBy = (sortField, isSortDescending) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.ALERTS_UPDATE_SEARCH_INCIDENTS_SORTBY,
      payload: { sortField, isSortDescending }
    });
    dispatch(searchIncidents());
  };
};

const selectIncident = (incident) => ({
  type: ACTION_TYPES.ALERTS_SEARCH_INCIDENTS_SELECT,
  payload: incident
});

const clearSearchIncidentsResults = () => ({
  type: ACTION_TYPES.CLEAR_SEARCH_INCIDENTS_RESULTS_FOR_ALERTS
});

export {
  addAlertsToIncident,
  clearSearchIncidentsResults,
  searchIncidents,
  selectIncident,
  updateSearchIncidentsText,
  updateSearchIncidentsSortBy
};