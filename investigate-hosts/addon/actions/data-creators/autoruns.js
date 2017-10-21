import { HostDetails } from '../api';
import * as ACTION_TYPES from '../types';
import Ember from 'ember';
import { handleError } from '../creator-utils';

const { Logger } = Ember;

/**
 * Action creator for fetching all autoruns given host id and scan time
 * @method getFileContextAutoruns
 * @public
 * @returns {Object}
 */
const getFileContextAutoruns = () => {
  return (dispatch, getState) => {
    // Get selected agentId and scan time from the state
    const { endpoint: { detailsInput: { agentId, scanTime } } } = getState();
    const data = {
      agentId,
      scanTime,
      categories: ['AUTORUNS', 'SERVICES', 'TASKS']
    };
    dispatch({
      type: ACTION_TYPES.FETCH_FILE_CONTEXT_AUTORUNS,
      promise: HostDetails.getFileContextData(data),
      meta: {
        onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_FILE_CONTEXT_AUTORUNS, response),
        onFailure: (response) => handleError(ACTION_TYPES.FETCH_FILE_CONTEXT_AUTORUNS, response)
      }
    });
  };
};

/**
 * An Action Creator for changing the autoruns view.
 *
 * @param {object}
 * @returns {function} redux-thunk
 * @public
 */
const setAutorunsTabView = (tabName) => ({ type: ACTION_TYPES.CHANGE_AUTORUNS_TAB, payload: { tabName } });

const setSelectedRow = ({ id }) => ({ type: ACTION_TYPES.SET_AUTORUN_SELECTED_ROW, payload: { id } });

export {
  getFileContextAutoruns,
  setSelectedRow,
  setAutorunsTabView
};