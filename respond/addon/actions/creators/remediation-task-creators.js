import Ember from 'ember';
import { RemediationTasks } from '../api';
import * as ACTION_TYPES from '../types';
import * as ErrorHandlers from '../util/error-handlers';

const {
  Logger
} = Ember;

/**
 * Action creator that dispatches a set of actions for fetching remediation tasks (with or without filters) and sorted by one field.
 * @method getItems
 * @public
 * @returns {function(*, *)}
 */
const getItems = () => {
  return (dispatch, getState) => {
    const { itemsFilters, sortField, isSortDescending } = getState().respond.remediationTasks;

    // Fetch the total remediation task count for the current filter criteria
    dispatch({
      type: ACTION_TYPES.FETCH_REMEDIATION_TASKS_TOTAL_COUNT,
      promise: RemediationTasks.getRemediationTaskCount(itemsFilters, { sortField, isSortDescending }),
      meta: {
        onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_REMEDIATION_TASKS_TOTAL_COUNT, response),
        onFailure: (response) => ErrorHandlers.handleContentRetrievalError(response, 'remediation tasks count')
      }
    });
    // Fetch all of the remediation tasks that meet the current filter criteria
    dispatch({
      type: ACTION_TYPES.FETCH_REMEDIATION_TASKS,
      promise: RemediationTasks.getRemediationTasks(itemsFilters, { sortField, isSortDescending }),
      meta: {
        onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_REMEDIATION_TASKS, response),
        onFailure: (response) => ErrorHandlers.handleContentRetrievalError(response, 'remediation tasks')
      }
    });
  };
};

/**
 * An action creator that updates a field on a remediation task
 * @method updateItem
 * @public
 * @param entityId {string} - The ID of the remediation task to update
 * @param field {string} - The name of the field on the record (e.g., 'priority' or 'status') to update
 * @param updatedValue {*} - The value to be set/updated on the record's field
 * @param callbacks
 * @param callbacks.onSuccess {function} - The callback to be executed when the operation is successful (e.g., showing a flash notification)
 * @param callbacks.onFailure {function} - The callback to be executed when the operation fails
 * @returns {Promise}
 */
const updateItem = (entityId, field, updatedValue, callbacks) => {
  return {
    type: ACTION_TYPES.UPDATE_REMEDIATION_TASK,
    promise: RemediationTasks.updateRemediationTask(entityId, field, updatedValue),
    meta: {
      onSuccess: (response) => {
        Logger.debug(ACTION_TYPES.UPDATE_REMEDIATION_TASK, response);
        callbacks.onSuccess(response);
      },
      onFailure: (response) => {
        ErrorHandlers.handleContentUpdateError(response, `${entityId} ${field} to ${updatedValue}`);
        callbacks.onFailure(response);
      }
    }
  };
};

/**
 * An action creator that dispatches a delete action for one or more remediation tasks
 * @public
 * @param entityId
 * @param callbacks
 * @returns {{type, promise: *, meta: {onSuccess: (function(*=)), onFailure: (function(*=))}}}
 */
const deleteItem = (entityId, callbacks) => {
  return {
    type: ACTION_TYPES.DELETE_REMEDIATION_TASK,
    promise: RemediationTasks.deleteRemediationTask(entityId),
    meta: {
      onSuccess: (response) => {
        Logger.debug(ACTION_TYPES.DELETE_REMEDIATION_TASK, response);
        callbacks.onSuccess(response);
      },
      onFailure: (response) => {
        ErrorHandlers.handleContentDeletionError(response, 'remediation task');
        callbacks.onFailure(response);
      }
    }
  };
};

/**
 * An action creator that dispatches a set of actions for updating remediation tasks filter criteria and re-running fetch of the
 * tasks using that new criteria
 * @public
 * @method updateFilter
 * @param filters An object representing the filters to be applied
 * @returns {function(*)}
 */
const updateFilter = (filters) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.UPDATE_REMEDIATION_TASK_FILTERS,
      payload: filters
    });

    dispatch(getItems());
  };
};

/**
 * An action creator that creates a remediation task for an incident
 * @method createRemediationTask
 * @param task
 * @public
 * @returns {{type, promise: *, meta: {onSuccess: (function(*=): *), onFailure: (function(*=))}}}
 */
const createRemediationTask = (task) => {
  return {
    type: ACTION_TYPES.CREATE_REMEDIATION_TASK,
    promise: RemediationTasks.createRemediationTask(task),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.CREATE_REMEDIATION_TASK, response),
      onFailure: (response) => ErrorHandlers.handleContentCreationError(response, 'create remediation task')
    }
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
    dispatch({ type: ACTION_TYPES.TOGGLE_REMEDIATION_TASKS_CUSTOM_DATE_RESTRICTION });
    dispatch(getItems());
  };
};

const toggleItemSelected = (item) => ({ type: ACTION_TYPES.TOGGLE_REMEDIATION_TASK_SELECTED, payload: item });
const toggleFocusItem = (item) => ({ type: ACTION_TYPES.TOGGLE_FOCUS_REMEDIATION_TASK, payload: item });
const clearFocusItem = () => ({ type: ACTION_TYPES.CLEAR_FOCUS_REMEDIATION_TASK });
const toggleSelectAll = () => ({ type: ACTION_TYPES.TOGGLE_SELECT_ALL_REMEDIATION_TASKS });

const resetFilters = () => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.RESET_REMEDIATION_TASK_FILTERS
    });

    dispatch(getItems());
  };
};

/**
 * An action creator for updating the sort-by information used in fetching remediation tasks
 * @public
 * @method sortBy Object { id: [field name (string) to sort by], isDescending: [boolean] }
 * @param sortField
 * @param isSortDescending
 * @returns {function(*)}
 */
const sortBy = (sortField, isSortDescending) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.REMEDIATION_TASK_SORT_BY,
      payload: {
        sortField,
        isSortDescending
      }
    });

    dispatch(getItems());
  };
};

const toggleFilterPanel = () => ({ type: ACTION_TYPES.TOGGLE_FILTER_PANEL_REMEDIATION_TASKS });

export {
  getItems,
  updateItem,
  deleteItem,
  updateFilter,
  createRemediationTask,
  sortBy,
  toggleCustomDateRestriction,
  resetFilters,
  toggleFilterPanel,
  toggleItemSelected,
  toggleFocusItem,
  clearFocusItem,
  toggleSelectAll
};