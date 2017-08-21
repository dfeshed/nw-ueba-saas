import { alerts } from '../api';
import * as ACTION_TYPES from '../types';
import * as errorHandlers from '../util/error-handlers';
import { next } from 'ember-runloop';
import Ember from 'ember';

const { Logger } = Ember;
const callbacksDefault = { onSuccess() {}, onFailure() {} };

/**
 * Action creator that dispatches a set of actions for fetching alerts (with or without filters) and sorted by one field.
 * @method getAlerts
 * @public
 */
const getItems = () => {
  return (dispatch, getState) => {
    const { itemsFilters, sortField, isSortDescending, stopItemsStream } = getState().respond.alerts;

    // Fetch the total incident count for the current query
    dispatch({
      type: ACTION_TYPES.FETCH_ALERTS_TOTAL_COUNT,
      promise: alerts.getAlertsCount(itemsFilters, { sortField, isSortDescending }),
      meta: {
        onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_ALERTS_TOTAL_COUNT, response),
        onFailure: (response) => errorHandlers.handleContentRetrievalError(response, 'incidents count')
      }
    });

    dispatch({ type: ACTION_TYPES.FETCH_ALERTS_STARTED });
    // If we already have an incidents stream running, stop it. This prevents a previously started stream
    // from continuing to deliver results at the same time as the new stream.
    if (stopItemsStream) {
      stopItemsStream();
    }

    alerts.getAlerts(
      itemsFilters,
      { sortField, isSortDescending },
      {
        onInit: (stopStreamFn) => {
          dispatch({ type: ACTION_TYPES.FETCH_ALERTS_STREAM_INITIALIZED, payload: stopStreamFn });
        },
        onCompleted: () => dispatch({ type: ACTION_TYPES.FETCH_ALERTS_COMPLETED }),
        onResponse: (payload) => dispatch({ type: ACTION_TYPES.FETCH_ALERTS_RETRIEVE_BATCH, payload }),
        onError: (response) => {
          dispatch({ type: ACTION_TYPES.FETCH_ALERTS_ERROR });
          errorHandlers.handleContentRetrievalError(response, 'alerts');
        }
      }
    );
  };
};

/**
 * Action creator for fetching the original (raw) alert details for the given alert/entity ID
 * @public
 * @param entityId
 */
const getOriginalAlert = (entityId) => ({
  type: ACTION_TYPES.FETCH_ORIGINAL_ALERT,
  promise: alerts.getOriginalAlert(entityId),
  meta: {
    onSuccess: (response) => (Logger.debug(ACTION_TYPES.FETCH_ORIGINAL_ALERT, response)),
    onFailure: (response) => (errorHandlers.handleContentRetrievalError(response, 'original alert'))
  }
});

/**
 * Action creator for deleting one or more alerts
 * @public
 * @param entityId
 * @param callbacks
 * @returns {{type, promise, meta: {onSuccess: (function(*=)), onFailure: (function(*=))}}}
 */
const deleteItem = (entityId, callbacks = callbacksDefault) => {
  return (dispatch) => {
    const deleteAlerts = alerts.delete(entityId); // call delete and get back promise
    const reloadItems = entityId.length >= 500;

    dispatch({
      type: ACTION_TYPES.DELETE_ALERT,
      promise: deleteAlerts,
      meta: {
        onSuccess: (response) => {
          Logger.debug(ACTION_TYPES.DELETE_ALERT, response);
          callbacks.onSuccess(response);
          if (reloadItems) {
            dispatch(getItems());
          }
        },
        onFailure: (response) => {
          errorHandlers.handleContentDeletionError(response, 'alert');
          callbacks.onFailure(response);
        }
      }
    });
  };
};


const resetFilters = () => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.RESET_ALERT_FILTERS
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
      type: ACTION_TYPES.UPDATE_ALERT_FILTERS,
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
      type: ACTION_TYPES.ALERT_SORT_BY,
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
    dispatch({ type: ACTION_TYPES.TOGGLE_ALERTS_CUSTOM_DATE_RESTRICTION });
    dispatch(getItems());
  };
};

const toggleFilterPanel = () => ({ type: ACTION_TYPES.TOGGLE_FILTER_PANEL_ALERTS });
const toggleItemSelected = (item) => ({ type: ACTION_TYPES.TOGGLE_ALERT_SELECTED, payload: item });
const toggleFocusItem = (item) => ((dispatch) => {
  dispatch({ type: ACTION_TYPES.TOGGLE_FOCUS_ALERT, payload: item });
  dispatch(getOriginalAlert(item.id));
});
const clearFocusItem = () => ({ type: ACTION_TYPES.CLEAR_FOCUS_ALERT });
const toggleSelectAll = () => ({ type: ACTION_TYPES.TOGGLE_SELECT_ALL_ALERTS });

/**
 * Action creator for resetting the `respond.alert` state to a given alert id.
 * Kicks off the fetching of the alert details info and the alert events.
 * @param {string} alertId
 * @public
 */
const initializeAlert = (alertId) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.INITIALIZE_ALERT,
      payload: alertId
    });
    if (alertId) {
      next(() => {
        dispatch(getAlert(alertId));
        dispatch(getAlertEvents(alertId));
        dispatch(getOriginalAlert(alertId));
      });
    }
  };
};

/**
 * Action creator for fetching an alert profile.
 * @param {string} alertId
 * @public
 */
const getAlert = (alertId) => {
  return (dispatch, getState) => {
    const { stopInfoStream } = getState().respond.alerts;

    // If we already have an alert info stream running, stop it. This prevents a previously started stream
    // from continuing to deliver results at the same time as the new stream.
    if (stopInfoStream) {
      stopInfoStream();
    }

    dispatch({ type: ACTION_TYPES.FETCH_ALERT_DETAILS_STARTED });
    alerts.getAlerts(
      { _id: alertId },
      { sortField: '_id', isSortDescending: false },
      {
        onInit: (stopStreamFn) => {
          dispatch({ type: ACTION_TYPES.FETCH_ALERT_DETAILS_STREAM_INITIALIZED, payload: stopStreamFn });
        },
        onCompleted: () => dispatch({ type: ACTION_TYPES.FETCH_ALERT_DETAILS_COMPLETED }),
        onResponse: (payload) => dispatch({ type: ACTION_TYPES.FETCH_ALERT_DETAILS_RETRIEVE_BATCH, payload }),
        onError: (response) => {
          dispatch({ type: ACTION_TYPES.FETCH_ALERT_DETAILS_ERROR });
          errorHandlers.handleContentRetrievalError(response, `alert ${alertId} profile`);
        }
      }
    );
  };
};

/**
 * Action creator for fetching an alert's events list.
 * @param {string} alertId
 * @public
 */
const getAlertEvents = (alertId) => ({
  type: ACTION_TYPES.FETCH_ALERT_EVENTS,
  promise: alerts.getAlertEvents(alertId),
  meta: {
    onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_ALERT_EVENTS, response),
    onFailure: (response) => errorHandlers.handleContentRetrievalError(response, `alert ${alertId} events`)
  }
});

const resizeAlertInspector = (width) => ({ type: ACTION_TYPES.RESIZE_ALERT_INSPECTOR, payload: width });

export {
  getItems,
  getOriginalAlert,
  deleteItem,
  resetFilters,
  updateFilter,
  sortBy,
  toggleCustomDateRestriction,
  toggleFilterPanel,
  toggleItemSelected,
  toggleFocusItem,
  clearFocusItem,
  toggleSelectAll,
  initializeAlert,
  getAlert,
  getAlertEvents,
  resizeAlertInspector
};