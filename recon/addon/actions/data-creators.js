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
import { lookup } from 'ember-dependency-lookup';
import { later } from '@ember/runloop';

import * as ACTION_TYPES from './types';
import { createToggleActionCreator } from './visual-creators';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';
import {
  eventTypeFromMetaArray,
  isEndpointEvent,
  isLogEvent
} from 'recon/reducers/meta/selectors';
import {
  RECON_VIEW_TYPES_BY_NAME,
  doesStateHaveViewData
} from 'recon/utils/reconstruction-types';
import {
  FATAL_ERROR_CODES,
  GENERIC_API_ERROR_CODE
} from 'recon/utils/error-codes';
import { killAllBatching } from './util/batch-data-handler';
import {
  fetchAliases,
  fetchLanguage,
  fetchMeta,
  fetchNotifications,
  fetchPacketData,
  batchPacketData,
  fetchReconFiles,
  fetchReconSummary,
  fetchTextData,
  batchTextData,
  cursorFirst,
  cursorPrevious,
  cursorNext,
  cursorLast
} from './fetch';
import { packetTotal } from 'recon/reducers/header/selectors';
import CookieStore from 'component-lib/session-stores/application';
import { getStoredState } from 'redux-persist';
import _ from 'lodash';

const cookieStore = CookieStore.create();
const authCookie = {}; // cache this to minimize reading from cookie-store.

/**
 * Will fetch and dispatch event meta
 * @param {object} dataState
 * @returns {function} redux-thunk
 * @private
 */
const _retrieveMeta = (dataState) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.META_RETRIEVE,
      promise: fetchMeta(dataState),
      meta: {
        onSuccess(data) {
          // have new meta, now need to possibly set to new recon view
          // and fetch data for that view
          dispatch(determineReconView(data));
        },
        onFailure(response) {
          handleInvestigateErrorCode(response, 'RETRIEVE_META');
        }
      }
    });
  };
};

const _fetchTextData = function(dispatch, state) {
  fetchTextData(
    _getTextAndPacketInputs(state),
    (payload) => dispatch({ type: ACTION_TYPES.TEXT_RECEIVE_PAGE, payload }),
    (payload) => dispatch({ type: ACTION_TYPES.TEXT_RENDER_NEXT, payload }),
    (payload) => dispatch({ type: ACTION_TYPES.TEXT_UPDATE_CURSOR, payload }),
    (response) => dispatch(_handleContentError(response, 'decode'))
  );
};

/**
 * Generic handler for errors fetching recon view data.
 * @param {object} response - Promise response object.
 * @param {string} type - Type of recon data.
 * @return {function} redux-thunk
 * @private
 */
const _handleContentError = (response) => {
  return (dispatch) => {
    if (response.code !== 2) {
      handleInvestigateErrorCode(response);
    }
    dispatch({
      type: ACTION_TYPES.CONTENT_RETRIEVE_FAILURE,
      payload: response.code
    });
  };
};

const _getTextAndPacketInputs = ({ recon: { data, packets, text } }) => ({
  endpointId: data.endpointId,
  eventId: data.eventId,
  packetsPageSize: packets.packetsPageSize,
  packetsRowIndex: (packets.pageNumber - 1) * packets.packetsPageSize,
  decode: text.decode
});

/**
 * We need to set the index and total for the event footer, after we receive
 * them from investigate.
 * @param {number} index - The event index in the list.
 * @param {number} total - The total number of events.
 * @return {object}
 * @public
 */
const setIndexAndTotal = (index, total) => ({
  type: ACTION_TYPES.SET_INDEX_AND_TOTAL,
  payload: { index, total }
});

const _handleFetchingNewData = (newViewCode) => {
  return (dispatch, getState) => {
    dispatch({ type: ACTION_TYPES.CONTENT_RETRIEVE_STARTED });

    // Switches on the view type and manages retrieving data
    // and ensuring the data is dispatched into state correctly
    const state = getState();
    switch (newViewCode) {
      case RECON_VIEW_TYPES_BY_NAME.FILE.code:
        // file view doesn't do any batching,
        // so use external interface to kill any batching
        // that may be occuring for other views
        killAllBatching();
        fetchReconFiles(state.recon.data)
          .then(({ data }) => {
            dispatch({
              type: ACTION_TYPES.FILES_RETRIEVE_SUCCESS,
              payload: data
            });
          })
          .catch((response) => {
            dispatch(_handleContentError(response, 'file'));
          });
        break;
      case RECON_VIEW_TYPES_BY_NAME.PACKET.code:
        fetchPacketData(
          _getTextAndPacketInputs(state),
          (payload) => dispatch({ type: ACTION_TYPES.PACKETS_RECEIVE_PAGE, payload: payload.data }),
          (payload) => dispatch({ type: ACTION_TYPES.PACKETS_RENDER_NEXT, payload }),
          (response) => dispatch(_handleContentError(response, 'packet'))
        );
        break;
      case RECON_VIEW_TYPES_BY_NAME.TEXT.code:
        _fetchTextData(dispatch, state);
        break;
    }
  };
};

const _handleRenderingStateData = (newViewCode) => {
  return (dispatch, getState) => {
    switch (newViewCode) {
      case RECON_VIEW_TYPES_BY_NAME.FILE.code:
        // just need to kill any batching
        // that may be occuring for other views
        // file view has no special handling for
        // data being in state
        killAllBatching();
        break;
      case RECON_VIEW_TYPES_BY_NAME.PACKET.code:
        batchPacketData(
          getState().recon.packets.packets,
          (payload) => dispatch({ type: ACTION_TYPES.PACKETS_RENDER_NEXT, payload }),
        );
        break;
      case RECON_VIEW_TYPES_BY_NAME.TEXT.code:
        batchTextData(
          getState().recon.text.textContent,
          (payload) => dispatch({ type: ACTION_TYPES.TEXT_RENDER_NEXT, payload }),
        );
        break;
    }
  };
};

const pageFirst = () => {
  return (dispatch) => {
    dispatch(_changePageNumber(1));
  };
};

const pagePrevious = () => {
  return (dispatch, getState) => {
    const pageNumber = Number(getState().recon.packets.pageNumber) - 1;
    dispatch(_changePageNumber(pageNumber));
  };
};

const pageNext = () => {
  return (dispatch, getState) => {
    const pageNumber = Number(getState().recon.packets.pageNumber) + 1;
    dispatch(_changePageNumber(pageNumber));
  };
};

const pageLast = () => {
  return (dispatch, getState) => {
    const { recon, recon: { packets: { packetsPageSize } } } = getState();
    const pageNumber = Math.ceil(packetTotal(recon) / packetsPageSize);
    dispatch(_changePageNumber(pageNumber));
  };
};

const jumpToPage = (newPage) => {
  if (newPage % 1 === 0) {
    return (dispatch, getState) => {
      const { recon, recon: { packets: { packetsPageSize } } } = getState();
      const totalPages = Math.ceil(packetTotal(recon) / packetsPageSize);
      if (newPage > 1 && newPage <= totalPages) {
        dispatch(_changePageNumber(newPage));
      } else if (newPage <= 1) {
        dispatch(pageFirst());
      } else if (newPage > totalPages) {
        dispatch(pageLast());
      }
    };
  }
};

const _changePageNumber = (pageNumber) => {
  return (dispatch, getState) => {
    dispatch({
      type: ACTION_TYPES.CHANGE_PAGE_NUMBER,
      payload: pageNumber
    });
    dispatch(_handleFetchingNewData(getState().recon.visuals.currentReconView.code));
  };
};

const changePacketsPerPage = (packetsPerPage) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.CHANGE_PACKETS_PER_PAGE,
      payload: packetsPerPage
    });
    dispatch(pageFirst());
  };
};

const textPageFirst = () => {
  return (dispatch) => {
    dispatch(_textChangePageNumber(1));
    cursorFirst();
  };
};

const textPagePrevious = () => {
  return (dispatch, getState) => {
    const textPageNumber = Number(getState().recon.text.textPageNumber) - 1;
    dispatch(_textChangePageNumber(textPageNumber));
    cursorPrevious();
  };
};

const textPageNext = () => {
  return (dispatch, getState) => {
    const textPageNumber = Number(getState().recon.text.textPageNumber) + 1;
    dispatch(_textChangePageNumber(textPageNumber));
    cursorNext();
  };
};

const textPageLast = () => {
  return (dispatch, getState) => {
    const { recon: { text: { textLastPage } } } = getState();
    dispatch(_textChangePageNumber(textLastPage));
    cursorLast();
  };
};

const _textChangePageNumber = (textPageNumber) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.TEXT_CHANGE_PAGE_NUMBER,
      payload: textPageNumber
    });
  };
};

/**
 * An Action Creator for changing a recon view. Dispatches action to update
 * visual indicators, then will either fetch the data for the recon view or
 * prepare the data already in state.
 * @param {object} newView - An object from the reconstruction-types.js list
 * @returns {function} redux-thunk
 * @public
 */
const setNewReconView = (newView) => {
  return (dispatch, getState) => {
    // Open web/email view of classic NW events reconstruction page in a new browser tab
    // without changing any content in current tab.
    if (newView.isClassicReconView) {
      const state = getState().recon.data;
      const path = `/investigation/${state.endpointId}/navigate/event/${newView.name}/${state.eventId}`;
      window.open(path, '_blank');
      return;
    }

    // first dispatch the new view
    dispatch({
      type: ACTION_TYPES.CHANGE_RECON_VIEW,
      payload: { newView }
    });

    // No need to fetch/dispatch recon view data if it already exists
    // in state. Means this recon view, for this event, has already had
    // its data fetched. On INITIALIZE the recon view data is wiped out
    const reconState = getState().recon;
    if (!doesStateHaveViewData(reconState, newView)) {
      dispatch(_handleFetchingNewData(newView.code));
    } else {
      dispatch(_handleRenderingStateData(newView.code));
    }
  };
};

/**
 * An Action Creator thunk that builds/sends action for initializing recon.
 * If the incoming event is the same as the existing event, no dispatch occurs,
 * otherwise...
 * ```
 * 1. The inputs are dispatched
 * 2. If language is not an input, it is fetched/dispatched
 * 3. If aliases is not an input, it is fetched/dispatched
 * 4. The summary data for the event is fetched/dispatched
 * 5. If meta is not provided, meta is fetched/dispatched
 * 6. If meta is provided, the event data is fetched
 * ```
 * @param {object} reconInputs - The hash of inputs provided to recon
 * @returns {function} redux-thunk
 * @public
 */
const initializeRecon = (reconInputs) => {
  return (dispatch, getState) => {
    const dataState = getState().recon.data;
    dispatch({
      type: ACTION_TYPES.OPEN_RECON
    });

    // Truncating eventId after decimal, if user enter a decimal eventId
    // This is just one particular case that we are handling here, not fair to the numerous other edge cases
    // In the future, we should display an error message if the user tries to alter the url
    // and not try to present something relevant if user purposely manipulates it
    const truncatedEvent = reconInputs.eventId;
    reconInputs.eventId = truncatedEvent.toString().replace(/[.][0-9]+$/, '');

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
              handleInvestigateErrorCode(response, 'FETCH_LANGUAGE');
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
              handleInvestigateErrorCode(response, 'FETCH_ALIASES');
            }
          }
        });
      }

      dispatch({
        type: ACTION_TYPES.SUMMARY_RETRIEVE,
        promise: fetchReconSummary(reconInputs),
        meta: {
          onFailure(response) {
            const { errorCode } = handleInvestigateErrorCode(response, 'FETCH_RECON_SUMMARY');
            dispatch(_checkForFatalApiError(errorCode));
          }
        }
      });

      // if meta not passed in then need to fetch it now
      // (even if its not being displayed) as we need to
      // use meta to determine which data to fetch and
      // which recon view to display
      if (!reconInputs.meta) {
        dispatch(_retrieveMeta(reconInputs));
      } else {
        dispatch(determineReconView(reconInputs.meta));
      }
    }
  };
};

/**
 * This action creator will shut down recon completely. We check for error codes
 * that we are expecting, and only call this dispatch function when we know that
 * error code is being handled.
 * @param {number} code - API error code.
 * @return {function} Redux Thunk that determines if the supplied code is fatal.
 * @private
 */
const _checkForFatalApiError = (code) => {
  return (dispatch) => {
    if (FATAL_ERROR_CODES.includes(code)) {
      dispatch({
        type: ACTION_TYPES.SET_FATAL_API_ERROR_FLAG,
        payload: code || GENERIC_API_ERROR_CODE
      });
    }
  };
};

/**
 * Function reused whenever meta is determined, either when it is passed in
 * or when it is retrieved
 * @private
 */
const determineReconView = (meta) => {
  return (dispatch, getState) => {
    const { forcedView } = eventTypeFromMetaArray(meta);
    if (authCookie.reconPrefInitialized) {
      _reconPreferencesAlreadyInitialized(dispatch, forcedView, getState);
      return;
    }

    /* Check the flag in authenticated cookie */
    cookieStore.restore().then(({ authenticated = {} }) => {
      _.merge(authCookie, authenticated);
    }).finally(() => {
      if (authCookie.reconPrefInitialized) {
        _reconPreferencesAlreadyInitialized(dispatch, forcedView, getState);
      } else {
        _initReconPreferences(dispatch, forcedView);
      }
    });
  };
};

const _reconPreferencesAlreadyInitialized = (dispatch, forcedView, getState) => {
  if (forcedView) {
    dispatch(setNewReconView(forcedView));
  } else {
    getStoredState({}, (err, storedState) => {
      let newView = getState().recon.visuals.defaultReconView;
      const storedReconView = _.get(storedState, 'recon.visuals.currentReconView.name');
      if (!err && storedReconView) {
        newView = RECON_VIEW_TYPES_BY_NAME[storedReconView];
      }
      dispatch(setNewReconView(newView));
    });
  }
};

const _initReconPreferences = (dispatch, forcedView) => {
  const prefService = lookup('service:preferences');
  /* For the first time after login, get default prefs from backend and override */
  prefService.getPreferences('investigate-events-preferences').then((data) => {
    dispatch({
      type: ACTION_TYPES.RESET_PREFERENCES,
      payload: data || {}
    });
    authCookie.reconPrefInitialized = true;
    cookieStore.persist({ authenticated: authCookie });
    const defaultView = _.get(data, 'eventAnalysisPreferences.currentReconView', RECON_VIEW_TYPES_BY_NAME.TEXT.name);
    const reconView = forcedView || RECON_VIEW_TYPES_BY_NAME[defaultView];
    dispatch(setNewReconView(reconView));
  });
};

const reconPreferencesUpdated = (preferences) => {
  return (dispatch, getState) => {
    const newReconView = _.get(preferences, 'eventAnalysisPreferences.currentReconView');
    const currentReconView = getState().recon.visuals.currentReconView.name;
    const reconViewChanged = newReconView !== currentReconView;

    dispatch({ type: ACTION_TYPES.SET_PREFERENCES, payload: preferences });

    /*
     * If its a packet event, we need to update the currentReconView to be same as the one selected in Preferences Panel..
     * But for Log/Endpoint Event , it needs to remain 'Text Analysis' always.
     */
    if (newReconView && reconViewChanged && !(isLogEvent(getState().recon) || isEndpointEvent(getState().recon))) {
      dispatch(setNewReconView(RECON_VIEW_TYPES_BY_NAME[newReconView]));
    }
  };
};

/**
 * This Action Creator thunk can possibly dispatch two actions:
 * ```
 * 1. It will always dispatch the TOGGLE_META action which
 *   is responsible for switching meta display state
 * 2. If no meta is available (wasn't passed to recon)
 *   then this action will retrieve the meta for the event
 *   and dispatch a META_RETRIEVED action
 * ```
 * @param {boolean} [setTo] - A means to force the 'toggle' to set meta one way
 * or another
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
      dispatch(_retrieveMeta(meta));
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
      (response) => {
        handleInvestigateErrorCode(response, 'FETCH_NOTIFICATIONS');
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
    _fetchTextData(dispatch, getState());
  };
};

const teardownNotifications = () => ({ type: ACTION_TYPES.NOTIFICATION_TEARDOWN_SUCCESS });

// When payload is toggled, data is flushed so the packets can be incrementally rendered again
// So must batch the packet data into state like it is batched when first fetched
const _toggleActionCreator = createToggleActionCreator(ACTION_TYPES.TOGGLE_PACKET_PAYLOAD_ONLY);
const togglePayloadOnly = (setTo) => {
  return (dispatch, getState) => {
    dispatch(_toggleActionCreator(setTo));

    // delay batching the packet data,
    // want any side effects of toggling
    // the flag to take affect in the UI
    // before processing the results
    later(() => {
      batchPacketData(
        getState().recon.packets.packets,
        (payload) => dispatch({ type: ACTION_TYPES.PACKETS_RENDER_NEXT, payload })
      );
    }, 250);
  };
};

export {
  decodeText,
  initializeNotifications,
  initializeRecon,
  setIndexAndTotal,
  pageFirst,
  pagePrevious,
  pageNext,
  pageLast,
  changePacketsPerPage,
  textPageFirst,
  textPagePrevious,
  textPageNext,
  textPageLast,
  setNewReconView,
  teardownNotifications,
  toggleMetaData,
  togglePayloadOnly,
  jumpToPage,
  determineReconView,
  reconPreferencesUpdated,
  cookieStore as _cookieStore, // exported for testing only
  authCookie as _authCookie // exported for testing only
};
