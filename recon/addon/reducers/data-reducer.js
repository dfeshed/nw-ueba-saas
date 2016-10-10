import { RECON_VIEW_TYPES_BY_NAME } from '../utils/reconstruction-types';
import * as ACTION_TYPES from '../actions/types';
import reduxActions from 'npm:redux-actions';

const dataInitialState = {
  // view defaults to packet
  currentReconView: RECON_VIEW_TYPES_BY_NAME.PACKET,

  // Recon inputs
  endpointId: null,
  eventId: null,
  total: null,
  index: null,

  // Recon inputs or fetched if not provided
  meta: null,
  aliases: null,
  language: null,

  // Fetched data
  headerItems: null,
  files: null,
  packetFields: null,
  packets: null,

  // Error state
  metaError: null,
  headerError: null,
  contentError: null,

  // loading states
  contentLoading: false,
  metaLoading: false
};

const data = reduxActions.handleActions({
  [ACTION_TYPES.INITIALIZE]: (state, { payload }) => {
    // only clear out data if its a new event
    if (state.eventId === payload.eventId) {
      return {
        ...state,
        ...payload
      };
    } else {
      // reset to initial data state
      // then persist user's current recon view
      // then overlay the inputs
      return {
        ...dataInitialState,
        currentReconView: state.currentReconView,
        ...payload
      };
    }
  },

  [ACTION_TYPES.CHANGE_RECON_VIEW]: (state, { payload: { newView } }) => ({
    ...state,
    currentReconView: newView
  }),

  [ACTION_TYPES.META_RETRIEVE_STARTED]: (state) => ({
    ...state,
    metaError: null,
    metaLoading: true
  }),
  [ACTION_TYPES.META_RETRIEVE_SUCCESS]: (state, { payload }) => ({
    ...state,
    meta: payload,
    metaLoading: false
  }),
  [ACTION_TYPES.META_RETRIEVE_FAILURE]: (state) => ({
    ...state,
    metaError: true,
    meta: null,
    metaLoading: false
  }),

  [ACTION_TYPES.SUMMARY_RETRIEVE_SUCCESS]: (state, { payload }) => ({
    ...state,
    headerItems: payload.headerItems,
    packetFields: payload.packetFields, // temporary until this data comes from packet retrieve
    headerError: null
  }),
  [ACTION_TYPES.SUMMARY_RETRIEVE_FAILURE]: (state) => ({
    ...state,
    headerError: true,
    headerItems: null
  }),

  [ACTION_TYPES.RECON_FILES_RETRIEVE_SUCCESS]: (state, { payload }) => ({
    ...state,
    files: payload,
    contentLoading: false
  }),

  // have packets already? then need to create new packet array with new ones at end
  [ACTION_TYPES.RECON_PACKETS_RETRIEVE_PAGE]: (state, { payload }) => ({
    ...state,
    packets: state.packets ? [ ...state.packets, ...payload ] : payload,
    contentLoading: false
  }),

  [ACTION_TYPES.RECON_CONTENT_RETRIEVE_FAILURE]: (state, { payload }) => ({
    ...state,
    contentError: payload,
    contentLoading: false
  }),

  [ACTION_TYPES.RECON_CONTENT_RETRIEVE_STARTED]: (state) => ({
    ...state,
    contentError: null,
    contentLoading: true
  })

}, dataInitialState);

export default data;
