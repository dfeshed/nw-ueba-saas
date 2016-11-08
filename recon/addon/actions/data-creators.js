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
import { determineEventType } from 'recon/utils/event-types';
import { RECON_VIEW_TYPES_BY_NAME } from '../utils/reconstruction-types';
import {
  fetchReconSummary,
  fetchMeta,
  fetchReconFiles,
  fetchPacketData,
  fetchLanguage,
  fetchAliases,
  fetchNotifications
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

      // have new meta, now need to possibly set to new recon view
      // and fetch data for that view
      _dispatchEvent(dispatch, dataState.currentReconView, data[0].metas);
    })
    .catch((response) => {
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
      type: ACTION_TYPES.CONTENT_RETRIEVE_FAILURE,
      payload: response.code
    });
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
    const dataState = getState().recon.data;
    if (!dataState[newView.dataKey]) {
      dispatch({ type: ACTION_TYPES.CONTENT_RETRIEVE_STARTED });

      // if is file recon, time to kick of request
      // for file recon data
      switch (newView.code) {
        case RECON_VIEW_TYPES_BY_NAME.FILE.code:
          fetchReconFiles(dataState)
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
        case RECON_VIEW_TYPES_BY_NAME.TEXT.code:
          fetchPacketData(
            dataState,
            (payload) => dispatch({ type: ACTION_TYPES.PACKETS_RETRIEVE_PAGE, payload }),
            (response) => _handleContentError(dispatch, response, 'packet')
          );
          break;
      }
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
const initializeRecon = (reconInputs) => {
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
        fetchLanguage(reconInputs)
          .then(({ data }) => {
            dispatch({
              type: ACTION_TYPES.LANGUAGE_RETRIEVE_SUCCESS,
              payload: data
            });
          })
          .catch((response) => {
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
          })
          .catch((response) => {
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
        })
        .catch((response) => {
          Logger.error('Could not retrieve recon event summary', response);
          dispatch({ type: ACTION_TYPES.SUMMARY_RETRIEVE_FAILURE });
        });

      // if meta not passed in then need to fetch it now
      // (even if its not being displayed) as we need to
      // use meta to determine which data to fetch and
      // which recon view to display
      if (!reconInputs.meta) {
        _dispatchMeta(dispatch, dataState);
      } else {
        _dispatchEvent(dispatch, dataState.currentReconView, reconInputs.meta);
      }
    }
  };
};

/*
 * Function reused whenever meta is determined, either when it is passed in
 * or when it is retrieved
 */
const _dispatchEvent = (dispatch, reconView, meta) => {
  // TODO we should optimize this and do the meta hashing in redux or a central location
  // we currently do this here and for the event table
  const eventType = determineEventType(meta);

  dispatch({
    type: ACTION_TYPES.SET_EVENT_TYPE,
    payload: eventType
  });

  // If we need to force a specific view based on eventType, do so
  const newReconView = eventType.forcedView || reconView;

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
    const { recon: { visuals, data } } = getState();

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

/**
 * Subscribe to notifications. Notifications tell us when any file downloads are finished/failed.
 * Eventually we will have a standalone notifications UI outside of recon, but for now it's all handled internally in recon.
 * @public
 */
const initializeNotifications = () => {
  return (dispatch) => {
    fetchNotifications(
      // on successful init, will received a function for stopping all notification callbacks
      (response) => {
        dispatch({
          type: ACTION_TYPES.NOTIFICATION_INIT_SUCCESS,
          payload: { cancelFn: response }
        });
      },
      // some job has finished and is ready for download
      ({ data }) => {
        dispatch({
          type: ACTION_TYPES.FILE_EXTRACT_JOB_SUCCESS,
          payload: data
        });
      },
      // some job failed
      (err) => {
        Logger.error('Error in file extract job', err);
      }
    );
  };
};

export {
  setNewReconView,
  initializeRecon,
  toggleMetaData,
  initializeNotifications
};
