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
import { _handleError } from '../creator-utils';

const { Logger } = Ember;

/**
 * Action creator for adding the expression to the expression list.
 * @param expression
 * @returns {function(*)}
 * @public
 */
const addFilter = (expression) => ({ type: ACTION_TYPES.ADD_HOST_FILTER, payload: expression });

// NOOP function to replace Ember.K
const NOOP = () => {};

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
const createCustomSearch = (filter, schemas, filterType, { onSuccess = NOOP, onFailure = NOOP }) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.UPDATE_FILTER_LIST,
      promise: Machines.createCustomSearch(filter, schemas, filterType),
      meta: {
        onSuccess: (response) => {
          Logger.debug(ACTION_TYPES.UPDATE_FILTER_LIST, response);
          onSuccess(response);
        },
        onFailure: (response) => {
          _handleError(ACTION_TYPES.UPDATE_FILTER_LIST, response);
          onFailure(response);
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
    dispatch({ type: ACTION_TYPES.ADD_SYSTEM_FILTER, payload: expression });
    dispatch(getPageOfMachines());
  };
};

export {
    setActiveFilter,
    updateFilter,
    addSystemFilter,
    addFilter,
    removeFilter,
    resetFilters,
    createCustomSearch
};