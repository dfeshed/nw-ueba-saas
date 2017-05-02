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
import { getStoredState } from 'redux-persist';

import * as ACTION_TYPES from './types';
import { eventTypeFromMetaArray } from 'recon/reducers/meta/selectors';
import { RECON_VIEW_TYPES_BY_NAME } from '../utils/reconstruction-types';
import {
  fetchAliases,
  fetchLanguage,
  fetchMeta,
  fetchNotifications,
  fetchPacketData,
  fetchReconFiles,
  fetchReconSummary,
  fetchTextData
} from './fetch';

const { Logger } = Ember;

/**
 * Will fetch and dispatch event meta
 *
 * @private
 */
const _dispatchMeta = (dispatch, dataState, view) => {
  dispatch({
    type: ACTION_TYPES.META_RETRIEVE,
    promise: fetchMeta(dataState),
    meta: {
      onSuccess(data) {
        // have new meta, now need to possibly set to new recon view
        // and fetch data for that view
        _dispatchEvent(dispatch, view, data);
      },
      onFailure(response) {
        Logger.error('Could not retrieve event meta', response);
      }
    }
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
  }
  dispatch({
    type: ACTION_TYPES.CONTENT_RETRIEVE_FAILURE,
    payload: response.code
  });
};

const _getTextAndPacketInputs = ({ recon: { data, packets, text } }) => ({
  endpointId: data.endpointId,
  eventId: data.eventId,
  packetsPageSize: packets.packetsPageSize,
  decode: text.decode
});

/**
 * We need to set the index and total for the event footer, after we receive them from investigate
 * @param index The event index in the list
 * @param total The total number of events
 * @returns {function(*)}
 * @public
 */
const setIndexAndTotal = (index, total) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.SET_INDEX_AND_TOTAL,
      payload: { index, total }
    });
  };
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
    const reconState = getState().recon;
    if (!newView.doesStateHaveViewData(reconState)) {
      dispatch({ type: ACTION_TYPES.CONTENT_RETRIEVE_STARTED });

      // if is file recon, time to kick of request
      // for file recon data
      switch (newView.code) {
        case RECON_VIEW_TYPES_BY_NAME.FILE.code:
          fetchReconFiles(reconState.data)
            .then(({ data }) => {
              dispatch({
                type: ACTION_TYPES.FILES_RETRIEVE_SUCCESS,
                payload: data
              });
            })
            .catch((response) => {
              _handleContentError(dispatch, response, 'file');
            });
          break;
        case RECON_VIEW_TYPES_BY_NAME.PACKET.code:
          fetchPacketData(
            _getTextAndPacketInputs(getState()),
            (payload) => dispatch({ type: ACTION_TYPES.PACKETS_RETRIEVE_PAGE, payload }),
            (response) => _handleContentError(dispatch, response, 'packet')
          );
          break;
        case RECON_VIEW_TYPES_BY_NAME.TEXT.code:
          fetchTextData(
            _getTextAndPacketInputs(getState()),
            (payload) => dispatch({ type: ACTION_TYPES.TEXT_DECODE_PAGE, payload }),
            (response) => _handleContentError(dispatch, response, 'text')
          );
          break;
      }
    }
  };
};

/*
 * The first time recon is opened, need to check and see if currentReconView is in the storedState
 * and update that here. This allows the reconView to be retained in localStorage between
 * sessions
 */
const initializeRecon = (reconInputs) => {
  return (dispatch, getState) => {
    // is there not an eventId in state? then is first time in recon.
    // check to see if should use localStorage reconView to keep user
    // in same place. Can't rely on rehydration into state because
    // rehydration is an action like any other any may not complete
    // before decisions are made based on what view we are starting at.
    if (!getState().recon.data.eventId) {
      getStoredState({}, (err, storedState) => {
        let starterView;
        if (!err && storedState && storedState.recon && storedState.recon.visuals.currentReconView) {
          starterView = RECON_VIEW_TYPES_BY_NAME[storedState.recon.visuals.currentReconView.name];
        }
        dispatch(_initializeRecon(reconInputs, starterView));
      });
    } else {
      dispatch(_initializeRecon(reconInputs));
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
 * 5) If meta is not provided, meta is fetched/dispatched
 * 6) If meta is provided, the event data is fetched
 *
 * @param {object} reconInputs the hash of inputs provided to recon
 * @returns {function} redux-thunk
 * @public
 */
const _initializeRecon = (reconInputs, starterView) => {

  return (dispatch, getState) => {
    const dataState = getState().recon.data;

    dispatch({
      type: ACTION_TYPES.OPEN_RECON
    });

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
        dispatch({
          type: ACTION_TYPES.LANGUAGE_RETRIEVE,
          promise: fetchLanguage(reconInputs),
          meta: {
            onFailure(response) {
              // failure to get language is no good, but
              // is not critical error no need to dispatch
              Logger.error('Could not retrieve language', response);
            }
          }
        });
      }

      // aliases is optional parameter for recon
      // may be passed in, if not, fetch
      if (!reconInputs.aliases) {
        dispatch({
          type: ACTION_TYPES.ALIASES_RETRIEVE,
          promise: fetchAliases(reconInputs),
          meta: {
            onFailure(response) {
              Logger.error('Could not retrieve aliases', response);
            }
          }
        });
      }

      dispatch({
        type: ACTION_TYPES.SUMMARY_RETRIEVE,
        promise: fetchReconSummary(reconInputs),
        meta: {
          onFailure(response) {
            Logger.error('Could not retrieve event summary', response);
          }
        }
      });

      // currentReconView is either provided when Recon first boots (from local storage)
      // or we will use whatever is in currentReconView (current redux state).
      // This allows for initializing with one view, and then using whatever view the user
      // changes to after that.
      const currentReconView = starterView || getState().recon.visuals.currentReconView;

      // if meta not passed in then need to fetch it now
      // (even if its not being displayed) as we need to
      // use meta to determine which data to fetch and
      // which recon view to display
      if (!reconInputs.meta) {
        _dispatchMeta(dispatch, reconInputs, currentReconView);
      } else {
        _dispatchEvent(dispatch, currentReconView, reconInputs.meta);
      }
    }
  };
};

/*
 * Function reused whenever meta is determined, either when it is passed in
 * or when it is retrieved
 */
const _dispatchEvent = (dispatch, reconView, meta) => {
  // If we need to force a specific view based on eventType, do so
  const newReconView = eventTypeFromMetaArray(meta).forcedView || reconView;

  // Taking advantage of existing action creator that handles
  // changing the recon view AND fetching the appropriate data
  dispatch(setNewReconView(newReconView));
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
    const { recon: { visuals, meta } } = getState();

    // if 1) currently meta not shown
    // 2) not purposefully setting to closed
    // 3) Meta not already fetched
    // then meta is about to open and needs retrieving
    if (visuals.isMetaShown === false && setTo !== false && !meta.meta) {
      _dispatchMeta(dispatch, meta, visuals.currentReconView);
    }

    // Handle setting of visual flag to
    // open/close meta
    const returnVal = {
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

/**
 * Subscribe to notifications. Notifications tell us when any file
 * downloads are finished/failed. Eventually we will have a standalone
 * notifications UI outside of recon, but for now it's all handled
 * internally in recon.
 *
 * @public
 */
const initializeNotifications = () => {
  return (dispatch, getState) => {
    fetchNotifications(
      // on successful init, will received a function for
      // stopping all notification callbacks
      (response) => {
        dispatch({
          type: ACTION_TYPES.NOTIFICATION_INIT_SUCCESS,
          payload: { cancelFn: response }
        });
      },
      // some job has finished and is ready for download...
      ({ data }) => {

        // ...but is it the right job?
        //
        // Verify that a file extraction is actually taking place.
        // If multiple browsers are open to the file tab of recon,
        // then all those open sockets will get the notification
        // that the download is ready, but we do not want to download
        // from every browser, just the browser where the download originated.
        const extractStatus = getState().recon.files.fileExtractStatus;
        if (['init', 'wait'].includes(extractStatus)) {
          dispatch({
            type: ACTION_TYPES.FILE_EXTRACT_JOB_SUCCESS,
            payload: data
          });
        }
      },
      // some job failed
      (err) => {
        Logger.error('Error in file extract job', err);
      }
    );
  };
};

/**
 * Action Creator to retrieve decoded text data (i.e. HTTP traffic).
 * @return {function} redux-thunk
 * @public
 */
const decodeText = () => {
  return (dispatch, getState) => {
    dispatch({ type: ACTION_TYPES.CONTENT_RETRIEVE_STARTED });
    dispatch({ type: ACTION_TYPES.TOGGLE_TEXT_DECODE });
    fetchTextData(
      _getTextAndPacketInputs(getState()),
      (payload) => dispatch({ type: ACTION_TYPES.TEXT_DECODE_PAGE, payload }),
      (response) => _handleContentError(dispatch, response, 'decode')
    );
  };
};

const teardownNotifications = () => ({ type: ACTION_TYPES.NOTIFICATION_TEARDOWN_SUCCESS });

export {
  decodeText,
  initializeNotifications,
  initializeRecon,
  setIndexAndTotal,
  setNewReconView,
  teardownNotifications,
  toggleMetaData
};
