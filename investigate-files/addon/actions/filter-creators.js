import * as ACTION_TYPES from 'investigate-shared/actions/types';
import File from './fetch/file';
import { warn } from '@ember/debug';

const callbacksDefault = { onSuccess() {}, onFailure() {} };

const _handleError = (response, type) => {
  const warnResponse = JSON.stringify(response);
  warn(`_handleError ${type} ${warnResponse}`, { id: 'investigate-files.actions.data-creators' });
};


/**
 * An action creator for getting the saved filter information
 * @returns {function(*)}
 * @public
 */
const getFilter = (callback) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.GET_FILTER,
      promise: File.getSavedFilters(),
      meta: {
        name: 'FILE',
        onSuccess: () => {
          dispatch(callback());
        }
      }
    });
  };
};

const deleteFilter = (id, callbacks = callbacksDefault) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.DELETE_FILTER,
      promise: File.deleteFilter(id),
      meta: {
        name: 'FILE',
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


const applyFilters = (reload, expressions) => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.APPLY_FILTER, payload: expressions, meta: { name: 'FILE' } });
    dispatch(reload());
  };
};

const applySavedFilters = (filter, reload) => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.SET_SAVED_FILTER, payload: filter, meta: { name: 'FILE' } });
    dispatch(applyFilters(reload, filter.criteria.expressionList));
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
      promise: File.createCustomSearch(filter, schemas, filterType),
      meta: {
        name: 'FILE',
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
  createCustomSearch
};
