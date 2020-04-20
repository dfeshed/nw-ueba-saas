import * as ACTION_TYPES from 'investigate-shared/actions/types';
import api from 'investigate-shared/actions/api/filters/filter-api';
import { warn } from '@ember/debug';

const callbacksDefault = { onSuccess() {}, onFailure() {} };

const _handleError = (response, type) => {
  const warnResponse = JSON.stringify(response);
  warn(`_handleError ${type} ${warnResponse}`, { id: 'investigate-CERTIFICATEs.actions.data-creators' });
};


/**
 * An action creator for getting the saved filter information
 * @returns {function(*)}
 * @public
 */
const getFilter = (callback, belongsTo) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.GET_FILTER,
      promise: api.getSavedFilters(),
      meta: {
        belongsTo,
        onSuccess: () => {
          dispatch(callback());
        }
      }
    });
  };
};

const deleteFilter = (belongsTo, id, callbacks = callbacksDefault) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.DELETE_FILTER,
      promise: api.deleteFilter(id),
      meta: {
        belongsTo,
        onSuccess: (response) => {
          callbacks.onSuccess(response);
        },
        onFailure: (response) => {
          _handleError(ACTION_TYPES.DELETE_FILTER, response);
          callbacks.onFailure(response);
        }
      }
    });
  };
};


const applyFilters = (reload, expressions, belongsTo) => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.APPLY_FILTER, payload: expressions, meta: { belongsTo } });
    dispatch(reload());
  };
};

const resetFilters = (belongsTo) => ({ type: ACTION_TYPES.RESET_FILTER, meta: { belongsTo } });

const applySavedFilters = (reload, belongsTo, filter) => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.SET_SAVED_FILTER, payload: filter, meta: { belongsTo } });
    dispatch(applyFilters(reload, filter.criteria.expressionList, belongsTo));
  };
};


/**
 * Action for creating custom search
 * @method createCustomSearch
 * @public
 */
const createCustomSearch = (filter, schemas, filterType, callbacks = callbacksDefault) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.SAVE_FILTER,
      promise: api.createCustomSearch(filter, schemas, filterType),
      meta: {
        belongsTo: filterType,
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

export {
  getFilter,
  deleteFilter,
  applyFilters,
  applySavedFilters,
  createCustomSearch,
  resetFilters
};
