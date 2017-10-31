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

import Ember from 'ember';


import * as ACTION_TYPES from './types';
import {
  getPackagerConfig,
  setPackagerConfig,
  createPackagerLogConfig
} from './fetch';

const { Logger } = Ember;

const downloadURL = '/rsa/nwe/management/packager/download';
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
        onSuccess: (response) => Logger.debug(ACTION_TYPES.GET_INFO, response),
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
const setConfig = (configData) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.SAVE_INFO,
      promise: setPackagerConfig(configData),
      meta: {
        onSuccess: (response) => {
          Logger.debug(ACTION_TYPES.GET_INFO, response);
          if ('OK' === response.data.statusCode || 'Success' === response.data) {
            const url = response.data.link || downloadURL;
            dispatch({ type: ACTION_TYPES.DOWNLOAD_PACKAGE, payload: url });
          }
        },
        onFailure: (response) => dispatch(_handleFilesError(response))
      }
    });
  };
};


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
 * Action creator setting the windows log collection config information and getting the download link.
 * @method createLogConfig
 * @public
 * @returns {Object}
 */
const createLogConfig = (configData) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.CREATE_LOG,
      promise: createPackagerLogConfig(configData),
      meta: {
        onSuccess: (response) => {
          Logger.debug(ACTION_TYPES.GET_INFO, response);
          if ('OK' === response.data.statusCode || 'Success' === response.data) {
            const url = response.data.link || downloadURL;
            dispatch({ type: ACTION_TYPES.DOWNLOAD_PACKAGE, payload: url });
          }
        },
        onFailure: (response) => dispatch(_handleFilesError(response))
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
  createLogConfig,
  resetForm
};
