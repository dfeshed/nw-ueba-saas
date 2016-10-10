/**
 * @file Recon Data Action Creators
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
import { RECON_VIEW_TYPES_BY_NAME } from '../utils/reconstruction-types';
import {
  fetchReconSummary,
  fetchMeta,
  fetchReconFiles,
  fetchPacketData,
  fetchLanguage,
  fetchAliases
} from './fetch';

const { Logger } = Ember;

/**
 * Will fetch and dispatch event meta
 *
 * @private
 */
const _dispatchMeta = (dispatch, dataState) => {
  dispatch({ type: ACTION_TYPES.META_RETRIEVE_STARTED });

  fetchMeta(dataState)
    .then(({ data }) => {
      dispatch({
        type: ACTION_TYPES.META_RETRIEVE_SUCCESS,
        payload: data[0].metas
      });
    }).catch((response) => {
      Logger.error('Could not retrieve event meta', response);
      dispatch({ type: ACTION_TYPES.META_RETRIEVE_FAILURE });
    });
};

/**
 * Generic handler for errors fetching reconstruction-type data
 *
 * @private
 */
const _handleContentError = (dispatch, response, type) => {
  if (response.code !== 2) {
    Logger.error(`Could not retrieve ${type} recon data`, response);
  } else {
    dispatch({
      type: ACTION_TYPES.RECON_CONTENT_RETRIEVE_FAILURE,
      payload: response.code
    });
  }
};

/**
 * Will fetch data and dispatch actions for each recon view type
 *
 * @private
 */
const _dispatchReconViewData = (dispatch, { code }, dataState) => {

  dispatch({ type: ACTION_TYPES.RECON_CONTENT_RETRIEVE_STARTED });

  // if is file recon, time to kick of request
  // for file recon data
  switch (code) {
    case RECON_VIEW_TYPES_BY_NAME.FILE.code:
      fetchReconFiles(dataState)
        .then(({ data }) => {
          dispatch({
            type: ACTION_TYPES.RECON_FILES_RETRIEVE_SUCCESS,
            payload: data
          });
        }).catch((response) => {
          _handleContentError(dispatch, response, 'file');
        });
      break;
    case RECON_VIEW_TYPES_BY_NAME.PACKET.code:
      fetchPacketData(
        dataState,
        (payload) => dispatch({ type: ACTION_TYPES.RECON_PACKETS_RETRIEVE_PAGE, payload }),
        (response) => _handleContentError(dispatch, response, 'packet')
      );
      break;
  }
};

/**
 * An Action Creator thunk creator for changing a recon view.
 *
 * Dispatches action to update visual indicators, then, if
 * data not already available, will fetch the data for the
 * recon view
 *
 * @param {object} newView an object from the reconstruction-types.js list
 * @returns {function} redux-thunk
 * @public
 */
const setNewReconView = (newView) => {
  return (dispatch, getState) => {

    // first dispatch the new view
    dispatch({
      type: ACTION_TYPES.CHANGE_RECON_VIEW,
      payload: {
        newView
      }
    });

    // No need to fetch/dispatch recon view data if it already exists
    // in state. Means this recon view, for this event, has already had
    // its data fetched. On INITIALIZE the recon view data is wiped out
    const dataState = getState().data;
    if (!dataState[newView.dataKey]) {
      _dispatchReconViewData(dispatch, newView, dataState);
    }
  };
};

/**
 * An Action Creator thunk that builds/sends action for initializing recon.
 *
 * If the incoming event is the same as the existing event, no dispatch occurs
 *
 * Otherwise...
 * 1) the inputs are dispatched
 * 2) If language is not an input, it is fetched/dispatched
 * 3) If aliases is not an input, it is fetched/dispatched
 * 4) The summary data for the event is fetched/dispatched
 * 5) If meta is not provided, and the meta panel is open, meta is fetched/dispatched
 *
 * @param {object} reconInputs the hash of inputs provided to recon
 * @returns {function} redux-thunk
 * @public
 */
const initializeRecon = (reconInputs) => {
  return (dispatch, getState) => {
    const dataState = getState().data;

    // If its the same eventId, there is nothing to do
    // as previous state will be intact
    if (dataState.eventId !== reconInputs.eventId) {

      // first, dispatch the data provided to recon as input
      dispatch({
        type: ACTION_TYPES.INITIALIZE,
        payload: reconInputs
      });

      // language is optional parameter for recon
      // may be passed in, if not, fetch
      if (!reconInputs.language) {
        fetchLanguage(reconInputs)
          .then(({ data }) => {
            dispatch({
              type: ACTION_TYPES.LANGUAGE_RETRIEVE_SUCCESS,
              payload: data
            });
          }).catch((response) => {
            // failure to get language is no good, but
            // is not critical error no need to dispatch
            Logger.error('Could not retrieve language', response);
          });
      }

      // aliases is optional parameter for recon
      // may be passed in, if not, fetch
      if (!reconInputs.aliases) {
        fetchAliases(reconInputs)
          .then(({ data }) => {
            dispatch({
              type: ACTION_TYPES.ALIASES_RETRIEVE_SUCCESS,
              payload: data
            });
          }).catch((response) => {
            // failure to get aliases is no good, but
            // is not critical error no need to dispatch
            Logger.error('Could not retrieve aliases', response);
          });
      }

      dispatch({ type: ACTION_TYPES.SUMMARY_RETRIEVE_STARTED });

      fetchReconSummary(reconInputs)
        .then(([headerItems, packetFields]) => {
          dispatch({
            type: ACTION_TYPES.SUMMARY_RETRIEVE_SUCCESS,
            payload: {
              headerItems,
              packetFields
            }
          });
        }).catch((response) => {
          Logger.error('Could not retrieve recon event summary', response);
          dispatch({ type: ACTION_TYPES.SUMMARY_RETRIEVE_FAILURE });
        });

      // if meta not passed in, and meta is shown, then need to fetch
      // meta data or panel will be empty
      if (getState().visuals.isMetaShown === true && !reconInputs.meta) {
        _dispatchMeta(dispatch, dataState);
      }

      _dispatchReconViewData(dispatch, dataState.currentReconView, reconInputs);
    }
  };
};

/**
 * This Action Creator thunk can possibly dispatch two actions:
 * 1) It will always dispatch the TOGGLE_META action which
 *   is responsible for switching meta display state
 * 2) If no meta is available (wasn't passed to recon)
 *   then this action will retrieve the meta for the event
 *   and dispatch a META_RETRIEVED action
 *
 * @param {boolean} [setTo] a means to force the 'toggle' to
 *   set meta one way or another
 * @returns {function} redux-thunk
 * @public
 */
const toggleMetaData = (setTo) => {
  return (dispatch, getState) => {
    const { visuals, data } = getState();

    // if 1) currently meta not shown
    // 2) not purposefully setting to closed
    // 3) Meta not already fetched
    // then meta is about to open and needs retrieving
    if (visuals.isMetaShown === false && setTo !== false && !data.meta) {
      _dispatchMeta(dispatch, data);
    }

    // Handle setting of visual flag to
    // open/close meta
    let returnVal = {
      type: ACTION_TYPES.TOGGLE_META
    };

    if (setTo !== undefined) {
      returnVal.payload = {
        setTo
      };
    }

    dispatch(returnVal);
  };
};

export {
  setNewReconView,
  initializeRecon,
  toggleMetaData
};
