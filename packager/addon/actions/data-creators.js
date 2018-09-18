/**
 * @file Packager Data Action Creators
 * Action creators for data retrieval,
 * or for actions that have data side effects
 *
 * Building actions according to FSA spec:
 * https://github.com/acdlite/flux-standard-action
 *
 * @public
 */

import { debug } from '@ember/debug';

import * as ACTION_TYPES from './types';
import {
  getPackagerConfig,
  setPackagerConfig,
  getListOfDevices
} from './fetch';

/**
 * Action creator for fetching packager config information.
 * @method getConfig
 * @public
 * @returns {Object}
 */
const getConfig = () => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.GET_INFO,
      promise: getPackagerConfig(),
      meta: {
        onSuccess: (response) => debug(`${ACTION_TYPES.GET_INFO} ${JSON.stringify(response)}`),
        onFailure: (response) => _handleFilesError(response)
      }
    });
  };
};

/**
 * Action creator setting the packager config information and getting the download link.
 * @method setConfig
 * @public
 * @returns {Object}
 */
const setConfig = (configData, configType, callback) => {
  return (dispatch, getState) => {
    dispatch({
      type: ACTION_TYPES.SAVE_INFO,
      promise: setPackagerConfig(configData),
      meta: {
        onSuccess: (response) => {
          debug(`${ACTION_TYPES.GET_INFO} ${JSON.stringify(response)}`);
          const { request: { data: { packageConfig } } } = response;
          const agentMode = packageConfig && packageConfig.fullAgent ? 'Full' : 'Insights';
          const { serverId } = getState().endpointQuery;
          let url = '';
          if (response.data.id) {
            if (serverId) {
              url = `/rsa/endpoint/${serverId}/packager/download?id=${response.data.id}&agentMode=${agentMode}`;
            } else {
              url = `/rsa/endpoint/packager/download?id=${response.data.id}&agentMode=${agentMode}`;
            }
            if (configType === 'LOG_CONFIG') {
              if (serverId) {
                url = `/rsa/endpoint/${serverId}/logconfig/download?id=${response.data.id}&filename=${configData.logCollectionConfig.configName}`;
              } else {
                url = `/rsa/endpoint/logconfig/download?id=${response.data.id}&filename=${configData.logCollectionConfig.configName}`;
              }
            }
            dispatch({ type: ACTION_TYPES.DOWNLOAD_PACKAGE, payload: url });
          }
          callback.onSuccess();
        },
        onFailure: (response) => {
          callback.onFailure(response);
        }
      }
    });
  };
};

const saveUIState = (fieldValues) => ({
  type: ACTION_TYPES.UPDATE_FIELDS,
  payload: fieldValues
});

/**
 * Action creator for fetching list of devices available.
 * @method getServices
 * @public
 * @returns {Object}
 */
const getDevices = () => ({
  type: ACTION_TYPES.GET_DEVICES,
  promise: getListOfDevices(),
  meta: {
    onSuccess: (response) => debug(`${ACTION_TYPES.GET_DEVICES} ${JSON.stringify(response)}`),
    onFailure: (response) => _handleFilesError(response)
  }
});

/**
 * Generic handler for errors
 * @private
 */
const _handleFilesError = (response) => {
  return {
    type: ACTION_TYPES.RETRIEVE_FAILURE,
    payload: response.code
  };
};

/**
 * Action for resetting form back to previous saved state
 * @public
 */
const resetForm = () => ({ type: ACTION_TYPES.RESET_FORM });

export {
  setConfig,
  getConfig,
  getDevices,
  resetForm,
  saveUIState
};
