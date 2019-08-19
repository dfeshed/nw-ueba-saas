import { apiCreateColumnGroup, apiDeleteColumnGroup } from './fetch/column-group';
import * as ACTION_TYPES from './types';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';

/**
 *
 * @param {{ string, object[] }} Object
 */
export const createColumnGroup = ({ name, fields }) => ({
  type: ACTION_TYPES.COLUMNS_CREATE,
  promise: apiCreateColumnGroup(name, fields),
  meta: {
    onFailure(response) {
      handleInvestigateErrorCode(response, 'POST_COLUMN_GROUP');
    }
  }
});

/**
 *
 * @param {*} id id of col group to delete
 */
export const deleteColumnGroup = (id) => ({
  type: ACTION_TYPES.COLUMNS_DELETE,
  promise: apiDeleteColumnGroup(id),
  meta: {
    onFailure(response) {
      handleInvestigateErrorCode(response, 'DELETE_COLUMN_GROUP');
    }
  }
});
