import { getProcessDetails } from '../api/process-properties';
import * as ACTION_TYPES from '../types';
import { debug } from '@ember/debug';

/**
 * Action creator for fetching process information.
 * @method fetchProcessDetails
 * @public
 * @returns {Object}
 */
const fetchProcessDetails = (data, serverId) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.FETCH_PROCESS_PROPERTIES,
      promise: getProcessDetails(data, serverId),
      meta: {
        onSuccess: (response) => {
          return debug(`${ACTION_TYPES.FETCH_PROCESS_PROPERTIES} ${JSON.stringify(response)}`);
        },
        onFailure: (response) => _handleError(response)
      }
    });
  };
};

/**
 * Generic handler for errors
 * @private
 */
const _handleError = (response) => {
  return {
    type: ACTION_TYPES.RETRIEVE_FAILURE,
    payload: response.code
  };
};

export {
  fetchProcessDetails
};