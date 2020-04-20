import * as ACTION_TYPES from 'investigate-shared/actions/types';
import api from 'investigate-shared/actions/api/file/file-status';

/**
 * Action for creating custom search
 * @method createCustomSearch
 * @public
 */
const getRestrictedFileList = (belongsTo) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.SET_RESTRICTED_FILE_LIST,
      promise: api.getRestrictedFileList(),
      meta: {
        belongsTo
      }
    });
  };
};

export {
  getRestrictedFileList
};
