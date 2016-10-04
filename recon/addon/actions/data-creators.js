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

import * as TYPES from './types';
import { TYPES_BY_NAME as RECON_VIEW_TYPES } from '../utils/reconstruction-types';
import {
  fetchReconSummary,
  fetchMeta,
  fetchReconFiles,
  fetchPacketData,
  fetchLanguage,
  fetchAliases
} from './fetch';


/**
 * Will fetch and dispatch event meta
 *
 * @private
 */
const _dispatchMeta = (dispatch, dataState) => {
  fetchMeta(dataState)
    .then(({ data }) => {
      dispatch({
        type: TYPES.META_RETRIEVE_SUCCESS,
        payload: data[0].metas
      });
    }).catch((/* response */) => {
    // TODO, dispatch error
    });
};

/**
 * Will fetch and dispatch data required for each recon view type
 *
 * @private
 */
const _dispatchReconViewData = (dispatch, { code }, dataState) => {
  // if is file recon, time to kick of request
  // for file recon data
  switch (code) {
    case RECON_VIEW_TYPES.FILE.code:
      fetchReconFiles(dataState)
        .then(({ data }) => {
          dispatch({
            type: TYPES.RECON_FILES_RETRIEVE_SUCCESS,
            payload: data
          });
        }).catch((/* response */) => {
          dispatch({ type: TYPES.RECON_CONTENT_RETRIEVE_FAILURE });
        });
      break;
    case RECON_VIEW_TYPES.PACKET.code:
      fetchPacketData(dataState)
        .then(([packetFields, packets]) => {
          dispatch({
            type: TYPES.RECON_PACKETS_RETRIEVE_SUCCESS,
            payload: {
              packetFields,
              packets
            }
          });
        }).catch((/* response */) => {
          dispatch({ type: TYPES.RECON_CONTENT_RETRIEVE_FAILURE });
        });
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
 * @public
 */
const setNewReconView = (newView) => {
  return (dispatch, getState) => {

    // first dispatch the new view
    dispatch({
      type: TYPES.CHANGE_RECON_VIEW,
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
 * @public
 */
const initializeRecon = (reconInputs) => {
  return (dispatch, getState) => {
    const dataState = getState().data;

    // If its the same eventId, there is nothing to do
    // as previous state will be intact
    if (dataState.eventId !== reconInputs.eventId) {

      // first, dispatch the inputs data
      dispatch({
        type: TYPES.INITIALIZE,
        payload: reconInputs
      });

      // language is optional parameter for recon
      // may be passed in, if not, fetch
      if (!reconInputs.language) {
        fetchLanguage(reconInputs)
          .then(({ data }) => {
            dispatch({
              type: TYPES.LANGUAGE_RETRIEVE_SUCCESS,
              payload: data
            });
          });
      }

      // aliases is optional parameter for recon
      // may be passed in, if not, fetch
      if (!reconInputs.aliases) {
        fetchAliases(reconInputs)
          .then(({ data }) => {
            dispatch({
              type: TYPES.ALIASES_RETRIEVE_SUCCESS,
              payload: data
            });
          });
      }

      fetchReconSummary(reconInputs)
        .then((headerItems) => {
          dispatch({
            type: TYPES.SUMMARY_RETRIEVE_SUCCESS,
            payload: headerItems
          });
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
      type: TYPES.TOGGLE_META
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