/**
 * Endpoint Data Action Creators
 * Action creators for data retrieval,
 * or for actions that have data side effects
 *
 * Building actions according to FSA spec:
 * https://github.com/acdlite/flux-standard-action
 *
 * @public
 */

import Ember from 'ember';

import * as ACTION_TYPES from '../types';
import { getPageOfMachines } from './host';
import { Machines } from '../api';
import { handleError } from '../creator-utils';

const { Logger } = Ember;

/**
 * Action creator for adding the expression to the expression list.
 * @param expression
 * @returns {function(*)}
 * @public
 */
const addFilter = (expression) => ({ type: ACTION_TYPES.ADD_HOST_FILTER, payload: expression });

const callbacksDefault = { onSuccess() {}, onFailure() {} };

/**
 * An action creator for removing the expression from the list of expression and also dispatch the action to re-fresh the host machine list
 * @param name
 * @returns {function(*)}
 * @public
 */
const removeFilter = (name) => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.REMOVE_HOST_FILTER, payload: name });
    dispatch(getPageOfMachines());
  };
};

/**
 * An action creator for resting the host machine filters.
 * @public
 * @returns {function(*)}
 */
const resetFilters = () => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.RESET_HOST_FILTERS });
    dispatch(getPageOfMachines());
  };
};

/**
* Action creator for setting the currently active filter.
* @param filter
* @returns {function(*)}
* @public
*/
const setActiveFilter = (filter) => ({ type: ACTION_TYPES.SET_ACTIVE_FILTER, payload: filter });

/**
 * Action for creating custom search
 * @method createCustomSearch
 * @public
 */
const createCustomSearch = (filter, schemas, filterType, callbacks = callbacksDefault) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.UPDATE_FILTER_LIST,
      promise: Machines.createCustomSearch(filter, schemas, filterType),
      meta: {
        onSuccess: (response) => {
          Logger.debug(ACTION_TYPES.UPDATE_FILTER_LIST, response);
          callbacks.onSuccess(response);
        },
        onFailure: (response) => {
          handleError(ACTION_TYPES.UPDATE_FILTER_LIST, response);
          callbacks.onFailure(response);
        }
      }
    });
  };
};

/**
 * An action creator for dispatches a set of actions for updating machine filter criteria and re-running fetch of the
 * machines using that new criteria
 * @public
 * @method updateFilter
 * @param filters list of expressions
 * @returns {function(*)}
 */
const updateFilter = (expression) => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.UPDATE_HOST_FILTER, payload: expression });
    dispatch(getPageOfMachines());
  };
};

/**
 * Action creator for adding the system filters (mac, linux and windows)
 * @param expression
 * @returns {function(*)}
 * @public
 */
const addSystemFilter = (expression) => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.ADD_SYSTEM_FILTER, payload: [expression] });
    dispatch(getPageOfMachines());
  };
};


const addExternalFilter = (expression) => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.ADD_SYSTEM_FILTER, payload: expression });
  };
};


/**
 * Action creator for deleting the saved search
 * @returns {function(*, *)}
 * @public
 */
const deleteSavedSearch = (id, callbacks = callbacksDefault) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.DELETE_SAVED_SEARCH,
      promise: Machines.deleteSearch(id),
      meta: {
        onSuccess: (response) => {
          Logger.debug(ACTION_TYPES.DELETE_SAVED_SEARCH, response);
          callbacks.onSuccess(response);
        },
        onFailure: (response) => {
          handleError(ACTION_TYPES.DELETE_SAVED_SEARCH, response);
          callbacks.onFailure(response);
        }
      }
    });
  };
};


export {
  setActiveFilter,
  updateFilter,
  addSystemFilter,
  addFilter,
  removeFilter,
  resetFilters,
  createCustomSearch,
  deleteSavedSearch,
  addExternalFilter
};
