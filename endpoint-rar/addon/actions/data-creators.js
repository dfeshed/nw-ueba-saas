import api from './api';
import ACTION_TYPES from './types';
/**
 * Action creator for fetching RAR installer ID.
 * @method getRARDownloadID
 * @public
 * @returns {Object}
 */
const getRARDownloadID = (data, callback) => ({
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
      callback.onFailure(response.meta.message);
    }
  }
});

const setServerId = (serverId) => ({
  type: ACTION_TYPES.SET_SERVER_ID,
  payload: serverId
});

const getRARConfig = (callback) => ({
  type: ACTION_TYPES.GET_AND_SAVE_RAR_CONFIG,
  promise: api.getRARConfig(),
  meta: {
    onFailure: (response) => {
      callback.onFailure(response.meta.message);
    }
  }
});

const _formatRARConfigData = (data) => {
  const { esh, httpsPort, httpsBeaconIntervalInSeconds, address } = data;
  return {
    esh,
    servers: [
      {
        httpsPort,
        httpsBeaconIntervalInSeconds: httpsBeaconIntervalInSeconds * 60,
        address
      }
    ]
  };
};

const saveRARConfig = (data, callback) => ({
  type: ACTION_TYPES.GET_AND_SAVE_RAR_CONFIG,
  promise: api.saveRARConfig(_formatRARConfigData({ ...data })),
  meta: {
    onSuccess: () => {
      callback.onSuccess();
    },
    onFailure: (response) => {
      callback.onFailure(response.meta.message);
    }
  }
});

const saveUIState = (fieldValues) => ({
  type: ACTION_TYPES.UPDATE_UI_STATE,
  payload: fieldValues
});

const resetRARConfig = () => ({
  type: ACTION_TYPES.RESET_RAR_CONFIG
});

export {
  getRARDownloadID,
  setServerId,
  getRARConfig,
  saveRARConfig,
  resetRARConfig,
  saveUIState
};