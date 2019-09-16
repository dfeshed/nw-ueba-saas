import { apiCreateColumnGroup,
  apiUpdateColumnGroup,
  apiDeleteColumnGroup } from './fetch/column-group';
import * as ACTION_TYPES from './types';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';

/**
 *
 * @param {{ string, object[] }} Object
 */
export const createColumnGroup = ({ name, columns }) => ({
  type: ACTION_TYPES.COLUMNS_CREATE,
  promise: apiCreateColumnGroup(name, columns),
  meta: {
    onFailure(response) {
      handleInvestigateErrorCode(response, 'POST_COLUMN_GROUP');
    }
  }
});

/**
 *
 * @param {string, object[], string} Object
 */
export const updateColumnGroup = ({ name, columns, id }) => ({
  type: ACTION_TYPES.COLUMNS_UPDATE,
  promise: apiUpdateColumnGroup(name, columns, id),
  meta: {
    onFailure(response) {
      handleInvestigateErrorCode(response, 'PUT_COLUMN_GROUP');
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
