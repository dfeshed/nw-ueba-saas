import api from './api';
import ACTION_TYPES from './types';
/**
 * Action creator for fetching RAR installer ID.
 * @method getRARDownloadID
 * @public
 * @returns {Object}
 */
const getRARDownloadID = (data, callback) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.GET_RAR_INSTALLER_ID,
      promise: api.getRARDownloadID(data),
      meta: {
        onSuccess: (response) => {
          const { id } = response.data;
          if (id) {
            callback.onSuccess();
          }
        },
        onFailure: (response) => {
          callback.onFailure(response);
        }
      }
    });
  };
};

const setServerId = (serverId) => ({
  type: ACTION_TYPES.SET_SERVER_ID,
  payload: serverId
});

export {
  getRARDownloadID,
  setServerId
};