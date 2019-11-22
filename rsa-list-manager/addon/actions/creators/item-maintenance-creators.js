import { apiCreateOrUpdateItem, apiDeleteItem } from '../api/api-interactions';
import * as ACTION_TYPES from '../types';
import _ from 'lodash';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';
import { modelName, editItemId, editItem } from 'rsa-list-manager/selectors/list-manager/selectors';

/**
 * for column groups only
 * @param {*} item column group object
 */
const _enrichColumnGroupWithPosition = (item) => {
  const result = _.cloneDeep(item);
  result.columns.forEach((col, i) => {
    if (!col.hasOwnProperty('position')) {
      col.position = i + 1;
    }
  });
  return result;
};

/**
 * @param {*}
 * 1. itemPayload of item to create,
 * 2. list's stateLocation
 */
export const createItem = (item, stateLocation, itemTransform) => {
  return (dispatch, getState) => {
    const apiModelName = modelName(getState(), stateLocation);
    const payload = {};
    payload[apiModelName] = item;

    // add 'position' to each element in columnGroup.columns
    if (apiModelName === 'columnGroup') {
      payload[apiModelName] = _enrichColumnGroupWithPosition(item);
    }
    dispatch({
      type: ACTION_TYPES.ITEM_CREATE,
      promise: apiCreateOrUpdateItem(payload, apiModelName),
      meta: {
        belongsTo: stateLocation,
        itemTransform,
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
export const updateItem = (item, stateLocation, itemTransform, itemUpdate) => {
  return (dispatch, getState) => {
    const apiModelName = modelName(getState(), stateLocation);
    const payload = {};
    payload[apiModelName] = item;
    // add 'position' to each element in columnGroup.columns
    if (apiModelName === 'columnGroup') {
      payload[apiModelName] = _enrichColumnGroupWithPosition(item);
    }
    dispatch({
      type: ACTION_TYPES.ITEM_UPDATE,
      promise: apiCreateOrUpdateItem(payload, apiModelName),
      meta: {
        belongsTo: stateLocation,
        itemTransform,
        onFailure(response) {
          handleInvestigateErrorCode(response, `PUT_${apiModelName.toUpperCase()}_ITEM`);
        },
        onSuccess() {
          const updatedItem = editItem(getState(), stateLocation);
          itemUpdate(updatedItem);
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

    const payload = {};
    payload[apiModelName] = { id };

    dispatch({
      type: ACTION_TYPES.ITEM_DELETE,
      promise: apiDeleteItem(payload, apiModelName),
      meta: {
        belongsTo: stateLocation,
        onFailure(response) {
          handleInvestigateErrorCode(response, `DELETE_${apiModelName.toUpperCase()}_ITEM`);
        }
      }
    });
  };
};
