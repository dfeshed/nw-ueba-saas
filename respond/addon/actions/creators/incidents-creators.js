import Ember from 'ember';
import { Incidents } from '../api';
import * as ACTION_TYPES from '../types';
import * as ErrorHandlers from '../util/error-handlers';
import * as DictionaryCreators from './dictionary-creators';

const {
  Logger
} = Ember;

/**
 * Action creator that dispatches a set of actions for fetching incidents (with or without filters) and sorted by one field.
 * @method getItems
 * @public
 * @returns {function(*, *)}
 */
const getItems = () => {
  return (dispatch, getState) => {
    const { itemsFilters, sortField, isSortDescending, stopItemsStream } = getState().respond.incidents;

    // Fetch the total incident count for the current query
    dispatch({
      type: ACTION_TYPES.FETCH_INCIDENTS_TOTAL_COUNT,
      promise: Incidents.getIncidentsCount(itemsFilters, { sortField, isSortDescending }),
      meta: {
        onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_INCIDENTS_TOTAL_COUNT, response),
        onFailure: (response) => ErrorHandlers.handleContentRetrievalError(response, 'incidents count')
      }
    });

    dispatch({ type: ACTION_TYPES.FETCH_INCIDENTS_STARTED });
    // If we already have an incidents stream running, stop it. This prevents a previously started stream
    // from continuing to deliver results at the same time as the new stream.
    if (stopItemsStream) {
      stopItemsStream();
    }

    Incidents.getIncidents(
      itemsFilters,
      { sortField, isSortDescending },
      {
        onInit: (stopStreamFn) => {
          dispatch({ type: ACTION_TYPES.FETCH_INCIDENTS_STREAM_INITIALIZED, payload: stopStreamFn });
        },
        onCompleted: () => dispatch({ type: ACTION_TYPES.FETCH_INCIDENTS_COMPLETED }),
        onResponse: (payload) => dispatch({ type: ACTION_TYPES.FETCH_INCIDENTS_RETRIEVE_BATCH, payload }),
        onError: (response) => {
          dispatch({ type: ACTION_TYPES.FETCH_INCIDENTS_ERROR });
          ErrorHandlers.handleContentRetrievalError(response, 'incidents');
        }
      }
    );
  };
};

/**
 * An action creator that updates a field on an incident
 * @method updateItem
 * @public
 * @param entityId {string} - The ID of the incident to update
 * @param field {string} - The name of the field on the record (e.g., 'priority' or 'status') to update
 * @param updatedValue {*} - The value to be set/updated on the record's field
 * @param callbacks
 * @param callbacks.onSuccess {function} - The callback to be executed when the operation is successful (e.g., showing a flash notification)
 * @param callbacks.onFailure {function} - The callback to be executed when the operation fails
 * @returns {Promise}
 */
const updateItem = (entityId, field, updatedValue, callbacks) => {
  return {
    type: ACTION_TYPES.UPDATE_INCIDENT,
    promise: Incidents.updateIncident(entityId, field, updatedValue),
    meta: {
      onSuccess: (response) => {
        Logger.debug(ACTION_TYPES.UPDATE_INCIDENT, response);
        callbacks.onSuccess(response);
      },
      onFailure: (response) => {
        ErrorHandlers.handleContentUpdateError(response, `${entityId} ${field} to ${updatedValue}`);
        callbacks.onFailure(response);
      }
    }
  };
};

const deleteItem = (entityId, callbacks) => {
  return {
    type: ACTION_TYPES.DELETE_INCIDENT,
    promise: Incidents.delete(entityId),
    meta: {
      onSuccess: (response) => {
        Logger.debug(ACTION_TYPES.DELETE_INCIDENT, response);
        callbacks.onSuccess(response);
      },
      onFailure: (response) => {
        ErrorHandlers.handleContentDeletionError(response, 'incident');
        callbacks.onFailure(response);
      }
    }
  };
};

const resetFilters = () => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.RESET_INCIDENT_FILTERS
    });

    dispatch(getItems());
  };
};

/**
 * An action creator for dispatches a set of actions for updating incidents filter criteria and re-running fetch of the
 * incidents using that new criteria
 * @public
 * @method updateFilter
 * @param filters An object representing the filters to be applied
 * @returns {function(*)}
 */
const updateFilter = (filters) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.UPDATE_INCIDENT_FILTERS,
      payload: filters
    });

    dispatch(getItems());
  };
};

/**
 * An action creator for updating the sort-by information used in fetching incidents
 * @public
 * @method sortBy Object { id: [field name (string) to sort by], isDescending: [boolean] }
 * @param sort
 * @returns {function(*)}
 */
const sortBy = (sortField, isSortDescending) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.SORT_BY,
      payload: {
        sortField,
        isSortDescending
      }
    });

    dispatch(getItems());
  };
};

/**
 * Toggles between standardized date/time ranges and custom range selection, initiating a search when the change is
 * completed
 * @public
 * @returns {function(*)}
 */
const toggleCustomDateRestriction = () => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.TOGGLE_CUSTOM_DATE_RESTRICTION });
    dispatch(getItems());
  };
};

const toggleFilterPanel = () => ({ type: ACTION_TYPES.TOGGLE_FILTER_PANEL });
const toggleItemSelected = (item) => ({ type: ACTION_TYPES.TOGGLE_INCIDENT_SELECTED, payload: item });
const toggleFocusItem = (item) => ({ type: ACTION_TYPES.TOGGLE_FOCUS_INCIDENT, payload: item });
const clearFocusItem = () => ({ type: ACTION_TYPES.CLEAR_FOCUS_INCIDENTS });
const toggleSelectAll = () => ({ type: ACTION_TYPES.TOGGLE_SELECT_ALL_INCIDENTS });

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
      onFailure: (response) => ErrorHandlers.handleContentRetrievalError(response, `incident ${incidentId} profile`)
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
  return (dispatch, getState) => {

    // If we already have a storyline stream running, stop it. This prevents a previously started stream
    // from continuing to deliver results at the same time as the new stream.
    const { stopStorylineStream } = getState().respond.incident;
    if (stopStorylineStream) {
      stopStorylineStream();
    }

    dispatch({ type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_STARTED });

    Incidents.getAlertsForIncident(
      incidentId,
      {
        onInit: (stopStreamFn) => {
          dispatch({ type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_STREAM_INITIALIZED, payload: stopStreamFn });
        },
        onCompleted: () => dispatch({ type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_COMPLETED }),
        onResponse: (payload) => dispatch({ type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_RETRIEVE_BATCH, payload }),
        onError: (response) => {
          dispatch({ type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_ERROR });
          ErrorHandlers.handleContentRetrievalError(response, `incident ${incidentId} storyline`);
        }
      }
    );
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
        dispatch(DictionaryCreators.getAllEnabledUsers());
      }
      if (!state.respond.dictionaries.priorityTypes.length) {
        dispatch(DictionaryCreators.getAllPriorityTypes());
      }
      if (!state.respond.dictionaries.statusTypes.length) {
        dispatch(DictionaryCreators.getAllStatusTypes());
      }
    }
  };
};

// UI STATE CREATORS - INCIDENT

const toggleJournalPanel = () => ({ type: ACTION_TYPES.TOGGLE_JOURNAL_PANEL });
const setHideViz = (hideViz) => ({ type: ACTION_TYPES.SET_HIDE_VIZ, payload: hideViz });
const setViewMode = (viewMode) => ({ type: ACTION_TYPES.SET_VIEW_MODE, payload: viewMode });
const resizeIncidentInspector = (width) => ({ type: ACTION_TYPES.RESIZE_INCIDENT_INSPECTOR, payload: width });
const singleSelectStoryPoint = (id) => ({ type: ACTION_TYPES.SET_INCIDENT_SELECTION, payload: { type: 'storyPoint', id } });
const toggleSelectStoryPoint = (id) => ({ type: ACTION_TYPES.TOGGLE_INCIDENT_SELECTION, payload: { type: 'storyPoint', id } });
const singleSelectEvent = (id) => ({ type: ACTION_TYPES.SET_INCIDENT_SELECTION, payload: { type: 'event', id } });
const toggleSelectEvent = (id) => ({ type: ACTION_TYPES.TOGGLE_INCIDENT_SELECTION, payload: { type: 'event', id } });
const singleSelectNode = (id) => ({ type: ACTION_TYPES.SET_INCIDENT_SELECTION, payload: { type: 'node', id } });
const toggleSelectNode = (id) => ({ type: ACTION_TYPES.TOGGLE_INCIDENT_SELECTION, payload: { type: 'node', id } });
const singleSelectLink = (id) => ({ type: ACTION_TYPES.SET_INCIDENT_SELECTION, payload: { type: 'link', id } });
const toggleSelectLink = (id) => ({ type: ACTION_TYPES.TOGGLE_INCIDENT_SELECTION, payload: { type: 'link', id } });

export {
  getItems,
  updateItem,
  deleteItem,
  updateFilter,
  sortBy,
  toggleCustomDateRestriction,
  resetFilters,
  toggleFilterPanel,
  toggleItemSelected,
  toggleFocusItem,
  clearFocusItem,
  toggleSelectAll,
  getIncident,
  getStoryline,
  initializeIncident,
  toggleJournalPanel,
  setHideViz,
  setViewMode,
  resizeIncidentInspector,
  singleSelectStoryPoint,
  toggleSelectStoryPoint,
  singleSelectEvent,
  toggleSelectEvent,
  singleSelectLink,
  toggleSelectLink,
  singleSelectNode,
  toggleSelectNode
};