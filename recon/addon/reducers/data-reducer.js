import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import { RECON_VIEW_TYPES_BY_NAME } from '../utils/reconstruction-types';
import * as ACTION_TYPES from '../actions/types';

const dataInitialState = {
  // view defaults to packet
  currentReconView: RECON_VIEW_TYPES_BY_NAME.PACKET,

  // Recon inputs
  endpointId: null,
  eventId: null,
  total: null,
  index: null,

  // Recon inputs or fetched if not provided
  aliases: null,
  language: null,

  // Fetched data
  headerItems: null,
  headerLoading: null,

  // callback for stopping notifications
  // (obtained at run-time as a result from notifications initialization)
  stopNotifications: null,

  // Error state
  headerError: null,
  contentError: null,

  // loading states
  contentLoading: false
};

const dataReceivedDoneLoading = (state) => ({
  ...state,
  contentLoading: false
});

const data = handleActions({
  [ACTION_TYPES.INITIALIZE]: (state, { payload }) => ({
    ...dataInitialState,
    currentReconView: state.currentReconView,
    stopNotifications: state.stopNotifications,
    ...payload
  }),

  [ACTION_TYPES.CHANGE_RECON_VIEW]: (state, { payload: { newView } }) => ({
    ...state,
    currentReconView: newView
  }),

  // Summary reducing
  [ACTION_TYPES.SUMMARY_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s, headerItems: null, headerError: null, headerLoading: true }),
      finish: (s) => ({ ...s, headerLoading: false }),
      failure: (s) => ({ ...s, headerError: true }),
      success: (s) => ({ ...s, headerItems: action.payload.headerItems })
    });
  },

  // Generic content handling
  [ACTION_TYPES.CONTENT_RETRIEVE_STARTED]: (state) => ({
    ...state,
    contentError: null,
    contentLoading: true
  }),
  [ACTION_TYPES.CONTENT_RETRIEVE_FAILURE]: (state, { payload }) => ({
    ...state,
    contentError: payload,
    contentLoading: false
  }),
  [ACTION_TYPES.TEXT_DECODE_PAGE]: dataReceivedDoneLoading,
  [ACTION_TYPES.PACKETS_RETRIEVE_PAGE]: dataReceivedDoneLoading,
  [ACTION_TYPES.FILES_RETRIEVE_SUCCESS]: dataReceivedDoneLoading,

  // Notifications
  [ACTION_TYPES.NOTIFICATION_INIT_SUCCESS]: (state, { payload }) => ({
    ...state,
    stopNotifications: payload.cancelFn
  }),
  [ACTION_TYPES.NOTIFICATION_TEARDOWN_SUCCESS]: (state) => ({
    // clear the callback that tears down notifications
    ...state,
    stopNotifications: null
  })
}, dataInitialState);

export default data;
