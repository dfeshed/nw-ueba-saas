import api from './api';
import ACTION_TYPES from './types';

const DATABASE_EXCEPTION = 2;
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
    onFailure: () => {
      callback.onFailure('installerFailure');
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
      if (response && response.code === DATABASE_EXCEPTION) {
        callback.onFailure('dbFailureMessageForLoad');
      } else {
        callback.onFailure('failureMessageForLoad');
      }
    }
  }
});

const _formatRARConfigData = (data) => {
  const { esh, httpsPort, httpsBeaconIntervalInSeconds, address, enabled } = data;
  return {
    esh,
    enabled,
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
      if (response && response.code === DATABASE_EXCEPTION) {
        callback.onFailure('dbFailureMessage');
      } else {
        callback.onFailure('failureMessage');
      }
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

const testRARConfig = (data, callback) => ({
  type: ACTION_TYPES.TEST_RAR_CONFIG,
  promise: api.testRARConfig(_formatRARConfigData({ ...data })),
  meta: {
    onSuccess: () => {
      callback.onSuccess();
    },
    onFailure: () => {
      callback.onFailure();
    }
  }
});

const getRarStatus = (callback) => ({
  type: ACTION_TYPES.GET_AND_SAVE_ENABLE_STATUS,
  promise: api.getEnableStatus(),
  meta: {
    onSuccess: () => {
      callback.onSuccess();
    },
    onFailure: (response) => {
      if (response && response.code === DATABASE_EXCEPTION) {
        callback.onFailure('dbFailureMessageForLoad');
      } else {
        callback.onFailure('failureMessageForLoad');
      }
    }
  }
});

const _formatStatusData = (data) => {
  return {
    enabled: data
  };
};

const saveRarStatus = (data, callback) => ({
  type: ACTION_TYPES.GET_AND_SAVE_ENABLE_STATUS,
  promise: api.saveEnableStatus(_formatStatusData(data)),
  meta: {
    onSuccess: () => {
      callback.onSuccess();
    },
    onFailure: (response) => {
      if (response && response.code === DATABASE_EXCEPTION) {
        callback.onFailure('dbFailureMessageForStatus');
      } else {
        callback.onFailure('failureMessageForStatus');
      }
    }
  }
});


export {
  getRARDownloadID,
  setServerId,
  getRARConfig,
  saveRARConfig,
  resetRARConfig,
  saveUIState,
  testRARConfig,
  getRarStatus,
  saveRarStatus
};