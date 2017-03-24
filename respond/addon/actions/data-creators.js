import Ember from 'ember';
import {
  dictionaries,
  Incidents,
  Journal,
  Users } from './api';
import * as ACTION_TYPES from './types';

const {
  Logger
} = Ember;

// NOOP function to replace Ember.K
const NOOP = () => {};

/**
 * Action creator that dispatches a set of actions for fetching incidents (with or without filters) and sorted by one field.
 * @method getIncidents
 * @public
 * @returns {function(*, *)}
 */
const getIncidents = () => {
  return (dispatch, getState) => {
    const { incidentsFilters: filters, incidentsSort: sort, stopIncidentsStream } = getState().respond.incidents;

    // Fetch the total incident count for the current query
    dispatch({
      type: ACTION_TYPES.FETCH_INCIDENTS_TOTAL_COUNT,
      promise: Incidents.getIncidentsCount(filters, sort),
      meta: {
        onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_INCIDENTS_TOTAL_COUNT, response),
        onFailure: (response) => _handleContentRetrievalError(response, 'incidents count')
      }
    });

    dispatch({ type: ACTION_TYPES.FETCH_INCIDENTS_STARTED });
    // If we already have an incidents stream running, stop it. This prevents a previously started stream
    // from continuing to deliver results at the same time as the new stream.
    if (stopIncidentsStream) {
      stopIncidentsStream();
    }

    Incidents.getIncidents(
      filters,
      sort,
      {
        onInit: (stopStreamFn) => {
          dispatch({ type: ACTION_TYPES.FETCH_INCIDENTS_STREAM_INITIALIZED, payload: stopStreamFn });
        },
        onCompleted: () => dispatch({ type: ACTION_TYPES.FETCH_INCIDENTS_COMPLETED }),
        onResponse: (payload) => dispatch({ type: ACTION_TYPES.FETCH_INCIDENTS_RETRIEVE_BATCH, payload }),
        onError: (response) => {
          dispatch({ type: ACTION_TYPES.FETCH_INCIDENTS_ERROR });
          _handleContentRetrievalError(response, 'incidents');
        }
      }
    );
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
    const state = getState();
    const wasIncidentId = state.respond.incident.id;
    if (wasIncidentId !== incidentId) {
      dispatch({
        type: ACTION_TYPES.INITIALIZE_INCIDENT,
        payload: incidentId
      });
      if (incidentId) {
        dispatch(getIncident(incidentId));
        dispatch(getStoryline(incidentId));
      }

      // If we haven't already fetched users (say, from incidents route), fetch now
      if (!state.respond.users.usersStatus) {
        dispatch(getAllUsers());
      }
      if (!state.respond.dictionaries.priorityTypes.length) {
        dispatch(getAllPriorityTypes());
      }
      if (!state.respond.dictionaries.statusTypes.length) {
        dispatch(getAllStatusTypes());
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
 * @param newValue {String} One of { LOW, MEDIUM, HIGH, CRITICAL }
 * @param {object.onSuccess} onSuccess callback for successful completion of action
 * @param {object.onFailure} onFailure callback for failed completion of action
 * @returns {object}
 */
const changeIncidentPriority = (incidentId, newValue, { onSuccess = NOOP, onFailure = NOOP }) => {
  return {
    type: ACTION_TYPES.UPDATE_INCIDENT,
    promise: Incidents.changeIncidentPriority(incidentId, newValue),
    meta: {
      onSuccess: (response) => {
        Logger.debug(ACTION_TYPES.UPDATE_INCIDENT, response);
        onSuccess(response);
      },
      onFailure: (response) => {
        _handleContentUpdateError(response, `${incidentId} priority to ${newValue}`);
        onFailure(response);
      }
    }
  };
};

/**
 * Action creator for changing the priority on multiple incidents simultaneously
 * @method bulkChangeIncidentPriority
 * @public
 * @param incidentIds
 * @param newValue
 * @param {object.onSuccess} onSuccess callback for successful completion of action
 * @param {object.onFailure} onFailure callback for failed completion of action
 * @returns {Object}
 */
const bulkChangeIncidentPriority = (incidentIds, newValue, { onSuccess = NOOP, onFailure = NOOP }) => {
  return {
    type: ACTION_TYPES.UPDATE_INCIDENT,
    promise: Incidents.bulkChangeIncidentPriority(incidentIds, newValue),
    meta: {
      onSuccess: (response) => {
        Logger.debug(ACTION_TYPES.UPDATE_INCIDENT, response);
        onSuccess(response);
      },
      onFailure: (response) => {
        _handleContentUpdateError(response, `incidents priority to ${newValue}`);
        onFailure(response);
      }
    }
  };
};

/**
 * Action creator for changing the status for a single incident
 * @method changeIncidentStatus
 * @public
 * @param incidentId The ID of the incident to update
 * @param newValue {String}
 * @param {object.onSuccess} onSuccess callback for successful completion of action
 * @param {object.onFailure} onFailure callback for failed completion of action
 * @returns {Object}
 */
const changeIncidentStatus = (incidentId, newValue, { onSuccess = NOOP, onFailure = NOOP }) => {
  return {
    type: ACTION_TYPES.UPDATE_INCIDENT,
    promise: Incidents.changeIncidentStatus(incidentId, newValue),
    meta: {
      onSuccess: (response) => {
        Logger.debug(ACTION_TYPES.UPDATE_INCIDENT, response);
        onSuccess(response);
      },
      onFailure: (response) => {
        _handleContentUpdateError(response, `${incidentId} status to ${newValue}`);
        onFailure(response);
      }
    }
  };
};

/**
 * Action creator for changing the status on multiple incidents simultaneously
 * @method bulkChangeIncidentStatus
 * @public
 * @param incidentIds
 * @param newValue {String}
 * @param {object.onSuccess} onSuccess callback for successful completion of action
 * @param {object.onFailure} onFailure callback for failed completion of action
 * @returns {Object}
 */
const bulkChangeIncidentStatus = (incidentIds, newValue, { onSuccess = NOOP, onFailure = NOOP }) => {
  return {
    type: ACTION_TYPES.UPDATE_INCIDENT,
    promise: Incidents.bulkChangeIncidentStatus(incidentIds, newValue),
    meta: {
      onSuccess: (response) => {
        Logger.debug(ACTION_TYPES.UPDATE_INCIDENT, response);
        onSuccess(response);
      },
      onFailure: (response) => {
        _handleContentUpdateError(response, `incidents priority to ${newValue}`);
        onFailure(response);
      }
    }
  };
};

/**
 * Action creator for changing the assignee for a single incident
 * @method changeIncidentAssignee
 * @public
 * @param incidentId The ID of the incident to update
 * @param newValue {Object}
 * @param {object.onSuccess} onSuccess callback for successful completion of action
 * @param {object.onFailure} onFailure callback for failed completion of action
 * @returns {Object}
 */
const changeIncidentAssignee = (incidentId, newValue, { onSuccess = NOOP, onFailure = NOOP }) => {
  return {
    type: ACTION_TYPES.UPDATE_INCIDENT,
    promise: Incidents.changeIncidentAssignee(incidentId, newValue),
    meta: {
      onSuccess: (response) => {
        Logger.debug(ACTION_TYPES.UPDATE_INCIDENT, response);
        onSuccess(response);
      },
      onFailure: (response) => {
        _handleContentUpdateError(response, `${incidentId} assignee to ${newValue.lastName}`);
        onFailure(response);
      }
    }
  };
};

/**
 * Action creator for changing the assignee on multiple incidents simultaneously
 * @method bulkChangeIncidentAssignee
 * @public
 * @param incidentIds
 * @param newValue {String}
 * @param {object.onSuccess} onSuccess callback for successful completion of action
 * @param {object.onFailure} onFailure callback for failed completion of action
 * @returns {Object}
 */
const bulkChangeIncidentAssignee = (incidentIds, newValue, { onSuccess = NOOP, onFailure = NOOP }) => {
  return {
    type: ACTION_TYPES.UPDATE_INCIDENT,
    promise: Incidents.bulkChangeIncidentAssignee(incidentIds, newValue),
    meta: {
      onSuccess: (response) => {
        Logger.debug(ACTION_TYPES.UPDATE_INCIDENT, response);
        onSuccess(response);
      },
      onFailure: (response) => {
        _handleContentUpdateError(response, `incidents assignee to ${newValue.name}`);
        onFailure(response);
      }
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
 * Action creator for fetching all known priority types
 * @method getAllPriorityTypes
 * @public
 * @returns {Object}
 */
const getAllPriorityTypes = () => {
  return {
    type: ACTION_TYPES.FETCH_PRIORITY_TYPES,
    promise: dictionaries.getAllPriorityTypes(),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_PRIORITY_TYPES, response),
      onFailure: (response) => _handleContentRetrievalError(response, 'priority types')
    }
  };
};


/**
 * Action creator for fetching all known status types
 * @method getAllStatusTypes
 * @public
 * @returns {Object}
 */
const getAllStatusTypes = () => {
  return {
    type: ACTION_TYPES.FETCH_STATUS_TYPES,
    promise: dictionaries.getAllStatusTypes(),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_STATUS_TYPES, response),
      onFailure: (response) => _handleContentRetrievalError(response, 'status types')
    }
  };
};

/**
 * Action creator for creating a journal entry on an incident
 * @method createJournalEntry
 * @public
 * @param journalEntry An {Object} containing the incidentId, author, notes and other properties for creating an entry
 * @param {object.onSuccess} onSuccess callback for successful completion of action
 * @param {object.onFailure} onFailure callback for failed completion of action
 * @returns {Object}
 */
const createJournalEntry = (journalEntry, { onSuccess = NOOP, onFailure = NOOP }) => {
  return {
    type: ACTION_TYPES.CREATE_JOURNAL_ENTRY,
    promise: Journal.createEntry(journalEntry),
    meta: {
      onSuccess: (response) => {
        Logger.debug(ACTION_TYPES.CREATE_JOURNAL_ENTRY, response);
        onSuccess(response);
      },
      onFailure: (response) => {
        _handleContentRetrievalError(response, 'journal');
        onFailure(response);
      }
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
  initializeIncident,
  getAllPriorityTypes,
  getAllStatusTypes
};