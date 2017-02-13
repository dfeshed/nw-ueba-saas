import Ember from 'ember';
import {
  fetchIncidents,
  fetchIncidentDetails,
  fetchStoryline } from './fetch';
import * as ACTION_TYPES from './types';

const {
  Logger
} = Ember;

/**
 * Action creator that dispatches a set of actions for fetching incidents (with or without filters) and sorted by one field.
 * @method getIncidents
 * @public
 * @returns {function(*, *)}
 */
const getIncidents = () => {
  return (dispatch, getState) => {
    const { incidentsFilters: filters, incidentsSort: sort } = getState().respond.incidents;
    dispatch({
      type: ACTION_TYPES.FETCH_INCIDENTS,
      promise: fetchIncidents(filters, sort),
      meta: {
        onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_INCIDENTS, response),
        onFailure: (response) => _handleContentError(response, 'incidents')
      }
    });
  };
};

/**
 * Action creator that dispatches an action for fetching an incident profile.
 * @method getIncident
 * @public
 * @returns {function(*)}
 */
const getIncident = (incidentId) => {
  return {
    type: ACTION_TYPES.FETCH_INCIDENT_DETAILS,
    promise: fetchIncidentDetails(incidentId),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_INCIDENT_DETAILS, response),
      onFailure: (response) => _handleContentError(response, `incident ${incidentId} profile`)
    }
  };
};

/**
 * Action creator that dispatches an action for fetching an incident's storyline data.
 * @method getStoryline
 * @public
 * @returns {function(*)}
 */
const getStoryline = (incidentId) => {
  return {
    type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE,
    promise: fetchStoryline(incidentId),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_INCIDENT_STORYLINE, response),
      onFailure: (response) => _handleContentError(response, `incident ${incidentId} storyline`)
    }
  };
};

/**
 * An action creator for dispatches a set of actions for updating incidents filter criteria and re-running fetch of the
 * incidents using that new criteria
 * @public
 * @method updateIncidentFilters
 * @param filters An object representing the filters to be applied
 * @returns {function(*)}
 */
const updateIncidentFilters = (filters) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.UPDATE_INCIDENT_FILTERS,
      payload: filters
    });

    dispatch(getIncidents());
  };
};

/**
 * An action creator for updating the sort-by information used in fetching incidents
 * @public
 * @method sortBy Object { id: [field name (string) to sort by], isDescending: [boolean] }
 * @param sort
 * @returns {function(*)}
 */
const sortBy = (sort) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.SORT_BY,
      payload: sort
    });

    dispatch(getIncidents());
  };
};

/**
 * Error helper for logging fetch errors to the console.
 * @param response
 * @param type
 * @private
 */
const _handleContentError = (response, type) => {
  if (response.code !== 2) {
    Logger.error(`Could not retrieve ${type} data`, response);
  }
};

export {
  getIncidents,
  getIncident,
  getStoryline,
  updateIncidentFilters,
  sortBy
};