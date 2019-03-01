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
import { lookup } from 'ember-dependency-lookup';
import * as ACTION_TYPES from './types';
import {
  getPackagerConfig,
  setPackagerConfig,
  fetchEndpointServers
} from './fetch';

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
const setConfig = (configData, callback, serverId) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.SAVE_INFO,
      promise: setPackagerConfig(configData),
      meta: {
        onSuccess: (response) => {
          debug(`${ACTION_TYPES.GET_INFO} ${JSON.stringify(response)}`);

          let url = '';
          if (response.data.id) {
            if (serverId) {
              url = `/rsa/endpoint/${serverId}/packager/download?id=${response.data.id}&agentMode=Full`;
            } else {
              url = `/rsa/endpoint/packager/download?id=${response.data.id}&agentMode=Full`;
            }
            dispatch({ type: ACTION_TYPES.DOWNLOAD_PACKAGE, payload: url });
          }
          callback.onSuccess();
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
 * action creator to fetch enpoint server list
 * @method getEndpointServerList
 * @public
 */
const getEndpointServerList = (serverId) => {
  return (dispatch) => {
    const request = lookup('service:request');
    request.registerPersistentStreamOptions({ 'socketUrlPostfix': 'any', 'requiredSocketUrl': 'endpoint/socket' });
    dispatch({
      type: ACTION_TYPES.GET_ENDPOINT_SERVERS,
      promise: fetchEndpointServers(),
      meta: {
        onSuccess: (response) => {
          request.registerPersistentStreamOptions({ 'socketUrlPostfix': serverId });
          const servers = [...response.data];
          const selectedServer = servers.find((server) => server.id === serverId);
          if (selectedServer) {
            dispatch({ type: ACTION_TYPES.SET_SELECTED_SERVER_IP, payload: selectedServer.host });
          }
          dispatch(getConfig());
          debug(`onSuccess: ${response}`);
        },
        onfailure: () => {
          request.clearPersistentStreamOptions(['socketUrlPostfix', 'requiredSocketUrl']);
        }
      }
    });
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
  resetForm,
  saveUIState,
  getEndpointServerList
};
