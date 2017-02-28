import Ember from 'ember';
import {
  Incidents,
  Journal,
  Users } from './api';
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
      promise: Incidents.getIncidents(filters, sort),
      meta: {
        onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_INCIDENTS, response),
        onFailure: (response) => _handleContentRetrievalError(response, 'incidents')
      }
    });
  };
};

/**
 * Action creator for fetching an incident profile.
 * @method getIncident
 * @public
 * @returns {Object}
 */
const getIncident = (incidentId) => {
  return {
    type: ACTION_TYPES.FETCH_INCIDENT_DETAILS,
    promise: Incidents.getIncidentDetails(incidentId),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_INCIDENT_DETAILS, response),
      onFailure: (response) => _handleContentRetrievalError(response, `incident ${incidentId} profile`)
    }
  };
};

/**
 * Action creator for resetting the `respond.incident` state to a given incident id.
 * If the given id matches the id currently in state, exits. Otherwise, reinitializes that state
 * and kicks off the fetching of its info + storyline.
 *
 * @param {string} incidentId
 * @returns {function(*, *)}
 * @public
 */
const initializeIncident = (incidentId) => {
  return (dispatch, getState) => {
    const wasIncidentId = getState().respond.incident.id;
    if (wasIncidentId !== incidentId) {
      dispatch({
        type: ACTION_TYPES.INITIALIZE_INCIDENT,
        payload: incidentId
      });
      if (incidentId) {
        dispatch(getIncident(incidentId));
        dispatch(getStoryline(incidentId));
      }
    }
  };
};

/**
 * Action creator for deleting an incident
 * @public
 * @method deleteIncident
 * @param incidentId
 * @returns {Object}
 */
const deleteIncident = (incidentId) => {
  return {
    type: ACTION_TYPES.DELETE_INCIDENT,
    promise: Incidents.deleteIncident(incidentId),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.DELETE_INCIDENT, response),
      onFailure: (response) => _handleContentDeletionError(response, `incident ${incidentId}`)
    }
  };
};

/**
 * Action creator for bulk deleting multiple incidents
 * @public
 * @method bulkDeleteIncidents
 * @param incidentIds An array of incident ids
 * @returns {Object}
 */
const bulkDeleteIncidents = (incidentIds) => {
  return {
    type: ACTION_TYPES.DELETE_INCIDENTS_BULK,
    promise: Incidents.bulkDeleteIncidents(incidentIds),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.DELETE_INCIDENTS_BULK, response),
      onFailure: (response) => _handleContentDeletionError(response, 'incidents')
    }
  };
};

/**
 * Action creator for changing the priority for a single incident
 * @method changeIncidentPriority
 * @public
 * @param incidentId The ID of the incident to update
 * @param newPriorityValue {String} One of { LOW, MEDIUM, HIGH, CRITICAL }
 * @returns {Object}
 */
const changeIncidentPriority = (incidentId, newPriorityValue) => {
  return {
    type: ACTION_TYPES.UPDATE_INCIDENT_PRIORITY,
    promise: Incidents.changeIncidentPriority(incidentId, newPriorityValue),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.UPDATE_INCIDENT_PRIORITY, response),
      onFailure: (response) => _handleContentUpdateError(response, `${incidentId} priority to ${newPriorityValue}`)
    }
  };
};

/**
 * Action creator for changing the priority on multiple incidents simultaneously
 * @method bulkChangeIncidentPriority
 * @public
 * @param incidentIds
 * @param newPriorityValue
 * @returns {Object}
 */
const bulkChangeIncidentPriority = (incidentIds, newPriorityValue) => {
  return {
    type: ACTION_TYPES.UPDATE_INCIDENT_PRIORITY_BULK,
    promise: Incidents.bulkChangeIncidentPriority(incidentIds, newPriorityValue),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.UPDATE_INCIDENT_PRIORITY_BULK, response),
      onFailure: (response) => _handleContentUpdateError(response, `incidents priority to ${newPriorityValue}`)
    }
  };
};

/**
 * Action creator for changing the status for a single incident
 * @method changeIncidentStatus
 * @public
 * @param incidentId The ID of the incident to update
 * @param newValue {String}
 * @returns {Object}
 */
const changeIncidentStatus = (incidentId, newValue) => {
  return {
    type: ACTION_TYPES.UPDATE_INCIDENT_STATUS,
    promise: Incidents.changeIncidentStatus(incidentId, newValue),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.UPDATE_INCIDENT_STATUS, response),
      onFailure: (response) => _handleContentUpdateError(response, `${incidentId} status to ${newValue}`)
    }
  };
};

/**
 * Action creator for changing the status on multiple incidents simultaneously
 * @method bulkChangeIncidentStatus
 * @public
 * @param incidentIds
 * @param newValue {String}
 * @returns {Object}
 */
const bulkChangeIncidentStatus = (incidentIds, newValue) => {
  return {
    type: ACTION_TYPES.UPDATE_INCIDENT_STATUS_BULK,
    promise: Incidents.bulkChangeIncidentStatus(incidentIds, newValue),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.UPDATE_INCIDENT_STATUS_BULK, response),
      onFailure: (response) => _handleContentUpdateError(response, `incidents priority to ${newValue}`)
    }
  };
};

/**
 * Action creator for changing the assignee for a single incident
 * @method changeIncidentAssignee
 * @public
 * @param incidentId The ID of the incident to update
 * @param newValue {Object}
 * @returns {Object}
 */
const changeIncidentAssignee = (incidentId, newValue) => {
  return {
    type: ACTION_TYPES.UPDATE_INCIDENT_ASSIGNEE,
    promise: Incidents.changeIncidentAssignee(incidentId, newValue),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.UPDATE_INCIDENT_ASSIGNEE, response),
      onFailure: (response) => _handleContentUpdateError(response, `${incidentId} assignee to ${newValue.lastName}`)
    }
  };
};

/**
 * Action creator for changing the assignee on multiple incidents simultaneously
 * @method bulkChangeIncidentAssignee
 * @public
 * @param incidentIds
 * @param newValue {String}
 * @returns {Object}
 */
const bulkChangeIncidentAssignee = (incidentIds, newValue) => {
  return {
    type: ACTION_TYPES.UPDATE_INCIDENT_ASSIGNEE_BULK,
    promise: Incidents.bulkChangeIncidentAssignee(incidentIds, newValue),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.UPDATE_INCIDENT_ASSIGNEE_BULK, response),
      onFailure: (response) => _handleContentUpdateError(response, `incidents assignee to ${newValue.lastName}`)
    }
  };
};

/**
 * Action creator for fetching an incident's storyline data.
 * @method getStoryline
 * @public
 * @returns {Object}
 */
const getStoryline = (incidentId) => {
  return {
    type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE,
    promise: Incidents.getStoryline(incidentId),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_INCIDENT_STORYLINE, response),
      onFailure: (response) => _handleContentRetrievalError(response, `incident ${incidentId} storyline`)
    }
  };
};

/**
 * Action creator for fetching all known Users
 * @method getAllUsers
 * @public
 * @returns {Object}
 */
const getAllUsers = () => {
  return {
    type: ACTION_TYPES.FETCH_ALL_USERS,
    promise: Users.getAllUsers(),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_ALL_USERS, response),
      onFailure: (response) => _handleContentRetrievalError(response, 'users')
    }
  };
};

/**
 * Action creator for creating a journal entry on an incident
 * @method createJournalEntry
 * @public
 * @param journalEntry An {Object} containing the incidentId, author, notes and other properties for creating an entry
 * @returns {Object}
 */
const createJournalEntry = (journalEntry) => {
  return {
    type: ACTION_TYPES.CREATE_JOURNAL_ENTRY,
    promise: Journal.createEntry(journalEntry),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.CREATE_JOURNAL_ENTRY, response),
      onFailure: (response) => _handleContentRetrievalError(response, 'journal')
    }
  };
};

/**
 * Action creator for deleting a journal entry attached to an incident
 * @method deleteJournalEntry
 * @public
 * @param incidentId
 * @param journalId
 * @returns {Object}
 */
const deleteJournalEntry = (incidentId, journalId) => {
  return {
    type: ACTION_TYPES.DELETE_JOURNAL_ENTRY,
    promise: Journal.deleteEntry(incidentId, journalId),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.DELETE_JOURNAL_ENTRY, response),
      onFailure: (response) => _handleContentRetrievalError(response, 'journal')
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
const _handleContentRetrievalError = (response, type) => {
  if (response.code !== 2) {
    Logger.error(`Could not retrieve ${type} data`, response);
  }
};

/**
 * Error helper for logging data delete errors to the console.
 * @param response
 * @param type
 * @private
 */
const _handleContentDeletionError = (response, type) => {
  if (response.code !== 2) {
    Logger.error(`Could not delete ${type} data`, response);
  }
};

/**
 * Error helper for logging data update errors to the console.
 * @param response
 * @param type
 * @private
 */
const _handleContentUpdateError = (response, type) => {
  if (response.code !== 2) {
    Logger.error(`Could not update ${type}`, response);
  }
};

export {
  getIncidents,
  getIncident,
  deleteIncident,
  changeIncidentPriority,
  changeIncidentAssignee,
  changeIncidentStatus,
  bulkDeleteIncidents,
  bulkChangeIncidentPriority,
  bulkChangeIncidentAssignee,
  bulkChangeIncidentStatus,
  getStoryline,
  getAllUsers,
  createJournalEntry,
  deleteJournalEntry,
  updateIncidentFilters,
  sortBy,
  initializeIncident
};