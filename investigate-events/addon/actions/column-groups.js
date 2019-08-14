import { apiCreateColumnGroup } from './fetch/column-groups';
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
