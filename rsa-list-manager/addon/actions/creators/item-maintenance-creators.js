import { apiCreateOrUpdateItem, apiDeleteItem } from '../api/api-interactions';
import * as ACTION_TYPES from '../types';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';
import { modelName, editItemId } from 'rsa-list-manager/selectors/list-manager/selectors';

/**
 * @param {*}
 * 1. itemPayload of item to delete,
 * 2. list's stateLocation
 */
export const createItem = (payload, stateLocation) => {
  return (dispatch, getState) => {
    const apiModelName = modelName(getState(), stateLocation);
    dispatch({
      type: ACTION_TYPES.ITEM_CREATE,
      promise: apiCreateOrUpdateItem(payload, apiModelName),
      meta: {
        belongsTo: stateLocation,
        onFailure(response) {
          handleInvestigateErrorCode(response, `POST_${apiModelName.toUpperCase()}_ITEM`);
        }
      }
    });
  };
};

/**
 * @param {*}
 * 1. itemPayload of item to delete,
 * 2. list's stateLocation
 */
export const updateItem = (payload, stateLocation) => {
  return (dispatch, getState) => {
    const apiModelName = modelName(getState(), stateLocation);
    dispatch({
      type: ACTION_TYPES.ITEM_UPDATE,
      promise: apiCreateOrUpdateItem(payload, apiModelName),
      meta: {
        belongsTo: stateLocation,
        onFailure(response) {
          handleInvestigateErrorCode(response, `PUT_${apiModelName.toUpperCase()}_ITEM`);
        }
      }
    });
  };
};

/**
 * @param {*}
 * list's stateLocation
 */
export const deleteItem = (stateLocation) => {
  return (dispatch, getState) => {
    const id = editItemId(getState(), stateLocation);
    const apiModelName = modelName(getState(), stateLocation);
    dispatch({
      type: ACTION_TYPES.ITEM_DELETE,
      promise: apiDeleteItem(id, apiModelName),
      meta: {
        belongsTo: stateLocation,
        onFailure(response) {
          handleInvestigateErrorCode(response, `DELETE_${apiModelName.toUpperCase()}_ITEM`);
        }
      }
    });
  };
};
